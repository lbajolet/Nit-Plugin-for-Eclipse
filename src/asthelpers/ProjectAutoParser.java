package asthelpers;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import plugin.NitActivator;
import builder.NitCompilerMessageInterpreter;

public class ProjectAutoParser {

	private class CompilerCallLightJob extends Job {

		private boolean isActive;

		ConcurrentLinkedQueue<IFile> workToBeDone;

		NitCompilerMessageInterpreter ncmi;

		public CompilerCallLightJob(String name) {
			super(name);
			workToBeDone = new ConcurrentLinkedQueue<IFile>();
			this.isActive = false;
			this.ncmi = new NitCompilerMessageInterpreter();
		}

		public void queueJob(IFile fileToAdd) {
			workToBeDone.add(fileToAdd);
			if (this.isActive == false) {
				this.schedule();
			}
		}

		@Override
		protected IStatus run(IProgressMonitor monitor) {
			this.isActive = true;
			Process compileProcess = null;
			int totalFiles = nitFilesOfProject.size();
			
			monitor.beginTask("Parsing full project", totalFiles);

			String pathToCompiler = NitActivator.getDefault()
					.getPreferenceStore().getString("compilerLocation");

			if (pathToCompiler != null) {

				while (!this.workToBeDone.isEmpty()) {

					IFile toParse = workToBeDone.poll();
					target = toParse;

					try {
						compileProcess = Runtime.getRuntime().exec(
								pathToCompiler
										+ " --only-metamodel --no-color "
										+ toParse.getLocation().toString());
					} catch (IOException e1) {
						e1.printStackTrace();
					}

					int endCode = 0;

					try {
						endCode = compileProcess.waitFor();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					try {
						// Get the messages
						InputStream inpt = compileProcess.getErrorStream();
						Reader in = new InputStreamReader(inpt, "UTF-8");
						StringBuilder result = new StringBuilder();
						char[] buf = new char[0x10000];
						int read = 0;
						do {
							read = in.read(buf, 0, buf.length);
							if (read > 0) {
								result.append(buf, 0, read);
							}
						} while (read >= 0);

						ncmi.addMessagesToProblems(ncmi
								.processMessagesOfCompiler(result.toString()),
								target.getProject(), nitFilesOfProject);
					} catch (Exception e) {
						e.printStackTrace();
					}
					monitor.worked(1);
				}
			}

			this.isActive = false;
			
			monitor.done();

			return Status.OK_STATUS;
		}

	}

	/**
	 * Project to parse
	 */
	private IProject projectToParse;

	/**
	 * The Parser Helper
	 */
	private AstParserHelper aph;

	/**
	 * Target of parser, updated on every Visit operation
	 */
	private IFile target;

	/**
	 * Reposit of all problematic files in the current project, updated on each
	 * compilation
	 */
	private HashMap<String, IFile> nitFilesOfProject;

	CompilerCallLightJob cclj;

	/**
	 * @author r4pass A visitor being needed to get the files of a project, here
	 *         it is.
	 */
	private class NitProjectVisitor implements IResourceVisitor {

		public NitProjectVisitor() {
		}

		@Override
		public boolean visit(IResource resource) throws CoreException {
			if (resource instanceof IFile) {
				IFile fichier = (IFile) resource;
				String extension = fichier.getFileExtension();
				if (extension != null && extension.equals("nit")) {
					fichier.deleteMarkers(IMarker.PROBLEM, true,
							IResource.DEPTH_INFINITE);
					if (aph.getAstForDocument(fichier) == null) {
						cclj.queueJob(fichier);
					}
				}
			}
			return true;
		}
	}

	private class NitFilesOfProjectParser implements IResourceVisitor {

		@Override
		public boolean visit(IResource resource) throws CoreException {
			if (resource instanceof IFile) {
				IFile fichier = (IFile) resource;
				String extension = fichier.getFileExtension();
				if (extension != null && extension.equals("nit")) {
					nitFilesOfProject.put(fichier.getName(), fichier);
				}
			}
			return true;
		}
	}

	private class ParsingJob extends Job {

		/**
		 * Default constructor for the Job
		 * 
		 * @param name
		 *            Name to give for I don't know what, required by the super
		 *            constructor
		 */
		public ParsingJob(String name) {
			super(name);
		}

		@Override
		protected IStatus run(IProgressMonitor monitor) {
			monitor.beginTask("Parsing project " + projectToParse.getName(),
					100);
			try {
				projectToParse.accept(new NitProjectVisitor());
			} catch (CoreException e) {
				e.printStackTrace();
			}
			monitor.done();
			return Status.OK_STATUS;
		}

	}

	/**
	 * Constructor
	 */
	public ProjectAutoParser() {
		this.aph = new AstParserHelper();
		this.nitFilesOfProject = new HashMap<String, IFile>();
		this.cclj = new CompilerCallLightJob("Parsing");
	}

	/**
	 * Sets the project to be fully parsed
	 * 
	 * @param proj
	 *            Projet to be set as default
	 */
	public void setProject(IProject proj) {
		this.projectToParse = proj;
		ParsingJob pj = new ParsingJob("Parsing project "
				+ this.projectToParse.getName());
		try {
			this.nitFilesOfProject.clear();
			proj.accept(new NitFilesOfProjectParser());
			pj.schedule();

		} catch (CoreException e) {
			e.printStackTrace();
		}
	}
	
	public void addToQueue(IFile fichier){
		this.cclj.queueJob(fichier);
	}

}
