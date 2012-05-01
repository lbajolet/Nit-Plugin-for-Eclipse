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
import org.eclipse.core.runtime.Status;

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
				NitNature self = null;
				try {
					self = (NitNature) proj.getNature(NitNature.NATURE_ID);
				} catch (CoreException e) {
					// If an exception is thrown, the project was not a Nit
					// Project, therefore, cannot be used to build Nit
				}
				if (self != null) {
					IFile defaultFile = self.getDefaultFile();
					if (defaultFile != null) {
						String path = NitActivator.getDefault()
								.getPreferenceStore()
								.getString("compilerLocation");
						if (path != null && path != "") {
							NitCompilerCallerClass ncc = self
									.getCompilerCaller();
							ncc.setPath(path
									+ " --no-color -o "
									+ defaultFile.getLocation().toString().substring(0,
											defaultFile.getLocation().toString().length() - 4));
							ncc.call();
						} else {
							// TODO : Probably means the compiler was not set in
							// parameters, therefore, add warning !
							NitActivator
									.getDefault()
									.getLog()
									.log(new Status(Status.WARNING,
											"Nit Compiling Process",
											"You need to set the Nit compiler location in Window/Properties/Nit"));
						}
					} else {
						NitActivator
								.getDefault()
								.getLog()
								.log(new Status(
										Status.WARNING,
										"Nit Compiling Process",
										"You need to set a target for compilation (Right click on a nit file > Define as default for project)"));
					}
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
