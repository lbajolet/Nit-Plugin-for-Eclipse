package asthelpers;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.editors.text.TextFileDocumentProvider;
import org.eclipse.ui.texteditor.IDocumentProvider;

public class ProjectAutoParser {

	/**
	 * Project to parse
	 */
	private IProject projectToParse;

	/**
	 * The Parser Helper
	 */
	private AstParserHelper aph;

	/**
	 * @author r4pass A visitor being needed to get the files of a project, here
	 *         it is.
	 */
	private class NitProjectVisitor implements IResourceVisitor {
		@Override
		public boolean visit(IResource resource) throws CoreException {
			if (resource instanceof IFile) {
				IFile fichier = (IFile) resource;
				IDocumentProvider provider = new TextFileDocumentProvider();
				if (fichier.getFileExtension().equals("nit")) {
					provider.connect(fichier);
					aph.getAstForDocument(provider.getDocument(fichier));
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
	}

}
