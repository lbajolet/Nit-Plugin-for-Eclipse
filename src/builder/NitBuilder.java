package builder;

import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import plugin.NitActivator;

/**
 * @author R4PaSs The builder for nit projet, is used to call for the Nit
 *         Compiler The builder is not incremental since the compiler is not
 */
public class NitBuilder extends IncrementalProjectBuilder {

	public static final String BUILDER_ID = "org.uqam.nit.ndt.builder";

	private static final String MARKER_TYPE = "org.uqam.nit.ndt.nitProblem";

	/**
	 * Visitor for the files of the project
	 * 
	 * @author R4PaSs Visits every element of a Project to build it
	 */
	class NitResourceVisitor implements IResourceVisitor {
		public boolean visit(IResource resource) {
			if (resource instanceof IProject) {
				IProject proj = resource.getProject();
				NitNature self;
				try {
					self = (NitNature) proj.getNature(NitNature.NATURE_ID);
					IFile defaultFile = self.getDefaultFile();
					if (defaultFile != null) {
						self.getCompilerCaller().setPath(
								NitActivator.getDefault().getPreferenceStore()
										.getString("compilerLocation")
										+ " --no-color");
						self.getCompilerCaller().call();
					} else {
						// TODO : Pop messagebox with the error
					}
				} catch (CoreException e) {

				}
			}
			return true;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.internal.events.InternalBuilder#build(int,
	 * java.util.Map, org.eclipse.core.runtime.IProgressMonitor)
	 */
	protected IProject[] build(int kind, Map args, IProgressMonitor monitor)
			throws CoreException {

		fullBuild(monitor);

		return null;
	}

	/**
	 * Checks the nit text in every nit file of the project
	 * 
	 * @param resource
	 *            The IResource (File...) to build
	 */
	void checkNit(IResource resource) {
		if (resource instanceof IFile && resource.getName().endsWith(".nit")) {
			IFile file = (IFile) resource;
			try {
				file.deleteMarkers(IMarker.PROBLEM, true,
						IResource.DEPTH_INFINITE);
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
	}

	//
	// /**
	// * Gets the message from the parser
	// *
	// * @param message
	// * Message sent by the parser
	// * @return The line returned by a parser exception
	// */
	// private int getLineOfMessage(String message) {
	// try {
	// String lines;
	// Pattern pat = Pattern.compile("[0-9]*,[0-9]*");
	// Matcher mat = pat.matcher(message);
	// mat.find();
	// MatchResult matches = mat.toMatchResult();
	// lines = message.substring(matches.start(), matches.end());
	// return Integer.parseInt(lines.split(",")[0]);
	// } catch (Exception exc) {
	// exc.getMessage();
	// }
	// return -1;
	// }

	/**
	 * Delete all markers set on a IFile object
	 * 
	 * @param file
	 *            The file to delete markers in
	 */
	private void deleteMarkers(IFile file) {
		try {
			file.deleteMarkers(MARKER_TYPE, false, IResource.DEPTH_ZERO);
		} catch (CoreException ce) {
		}
	}

	/**
	 * Performs a full build (the only way to compile Nit at the moment)
	 * 
	 * @param monitor
	 *            Progress bar in the Eclipse UI
	 * @throws CoreException
	 */
	protected void fullBuild(final IProgressMonitor monitor)
			throws CoreException {
		getProject().accept(new NitResourceVisitor());
		// int toto = 0;
	}

	/**
	 * Performs a full build (the only way to compile Nit at the moment)
	 * 
	 * @param delta
	 *            Used by Eclipse to store the modifications since the last
	 *            compiling attempt
	 * @param monitor
	 *            Progress bar in the Eclipse UI
	 * @throws CoreException
	 */
	protected void incrementalBuild(IResourceDelta delta,
			IProgressMonitor monitor) throws CoreException {
		fullBuild(monitor);
	}
}
