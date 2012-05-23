package asthelpers;

import java.io.File;
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
import builder.NitNature;

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
			if (!workToBeDone.contains(fileToAdd)) {
				workToBeDone.add(fileToAdd);
			}
			if (this.isActive == false) {
				this.schedule();
			}
		}

		@Override
		protected IStatus run(IProgressMonitor monitor) {
			this.isActive = true;
			Process compileProcess = null;
			int totalFiles = nitFilesOfProject.size();
			AstParserHelper aph = new AstParserHelper();

			monitor.beginTask("Parsing nit files", totalFiles);

			String pathToCompiler = NitActivator.getDefault()
					.getPreferenceStore().getString("compilerLocation");

			if (pathToCompiler != null) {

				while (!this.workToBeDone.isEmpty() && !monitor.isCanceled()) {

					IFile toParse = workToBeDone.poll();
					target = toParse;

					// Parse and add to main AST
					aph.getAstForDocument(toParse);

					// Remove markers of file
					try {
						toParse.deleteMarkers(IMarker.PROBLEM, true,
								IResource.DEPTH_INFINITE);
					} catch (CoreException e2) {
						if (NitActivator.DEBUG_MODE)
							e2.printStackTrace();
					}

					try {
						String peon = pathToCompiler
								+ " --only-metamodel --no-color "
								+ toParse.getLocation().toString();

						// Check if compiler is accessible
						File comp = new File(pathToCompiler);
						if (!comp.exists() || !comp.isFile()
								|| !comp.canExecute()) {
							NitActivator
									.getDefault()
									.getLog()
									.log(new Status(
											Status.ERROR,
											"Error with nit compiler",
											"Nit compiler cannot be found or cannot be run, are you sure the path you have set is valid ?"));
						}

						compileProcess = Runtime.getRuntime().exec(peon);
					} catch (IOException e1) {
						if (NitActivator.DEBUG_MODE)
							e1.printStackTrace();
					}

					try {
						compileProcess.waitFor();
					} catch (InterruptedException e) {
						if (NitActivator.DEBUG_MODE)
							e.printStackTrace();
					} catch (NullPointerException e) {
						if (NitActivator.DEBUG_MODE)
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
								target, nitFilesOfProject);
					} catch (Exception e) {
						if (NitActivator.DEBUG_MODE)
							e.printStackTrace();
					}
					monitor.worked(1);
				}
			}

			this.isActive = false;

			if (monitor.isCanceled()) {
				this.workToBeDone.clear();
			}

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
				if (NitActivator.DEBUG_MODE)
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
		try {
			proj.deleteMarkers(IMarker.PROBLEM, true, IResource.DEPTH_INFINITE);
		} catch (CoreException e1) {
			if (NitActivator.DEBUG_MODE)
				e1.printStackTrace();
		}
		ParsingJob pj = new ParsingJob("Parsing project "
				+ this.projectToParse.getName());
		try {
			this.nitFilesOfProject.clear();
			proj.accept(new NitFilesOfProjectParser());
			pj.schedule();
		} catch (CoreException e) {
			if (NitActivator.DEBUG_MODE)
				e.printStackTrace();
		}
	}

	public HashMap<String, IFile> buildFilesInProjectRepo(IProject proj) {
		this.nitFilesOfProject.clear();
		try {
			proj.accept(new NitFilesOfProjectParser());
		} catch (CoreException e) {
			if (NitActivator.DEBUG_MODE)
				e.printStackTrace();
		}

		return nitFilesOfProject;
	}

	public void addToQueue(IFile fichier) {
		buildFilesInProjectRepo(fichier.getProject());
		this.cclj.queueJob(fichier);
	}

}
