package org.nitlanguage.ndt.core.builder;

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
import org.nitlanguage.ndt.core.plugin.NitActivator;

/**
 * The builder for Nit Projets. Used to call for the Nit Compiler.
 * The builder is not incremental since the compiler is not.
 * @author lucas.bajolet 
 */
public class NitBuilder extends IncrementalProjectBuilder {
	//public static final String BUILDER_ID = "org.uqam.nit.ndt.builder";
	public static final String BUILDER_ID = "org.nitlanguage.ndt.builder";
	public static final String NIT_EXTENSION = ".nit";
	public static final String MSG_CAT_COMPILER = "Nit Compiling Process";
	public static final String MSG_COMPILER_MISSING = "You need to set the Nit compiler location in Window/Properties/Nit";
	public static final String PREFERENCE_KEY_COMPILER = "compilerLocation";
	
	//Do not use color to display errors and warnings
	public static final String NIT_COMPILER_PARAM_NO_COLOR = "--no-color";
	//Output file
	public static final String NIT_COMPILER_PARAM_OUTPUT = "-o";
	
	/*
	 * (non-Javadoc)
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
	 * @param resource The IResource (File...) to build
	 */
	void checkNit(IResource resource) {
		if (resource instanceof IFile && resource.getName().endsWith(NIT_EXTENSION)) {
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
	 * @param monitor Progress bar in the Eclipse UI
	 * @throws CoreException
	 */
	protected void fullBuild(final IProgressMonitor monitor)
			throws CoreException {
		getProject().accept(new NitResourceVisitor());
	}

	/**
	 * Performs a full build (the only way to compile Nit at the moment)
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
	
	/**
	 * Visitor for the files of the project
	 * Visits every element of a Project to build it
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
								.getString(PREFERENCE_KEY_COMPILER);
						if (path != null && path != "") {
							NitCompilerCallerClass ncc = self
									.getCompilerCaller();
							ncc.setPath(path
									+ NIT_COMPILER_PARAM_NO_COLOR + " "
									+ NIT_COMPILER_PARAM_OUTPUT + " "
									+ defaultFile
											.getLocation()
											.toString()
											.substring(
													0,
													defaultFile.getLocation()
															.toString()
															.length() - 4));
							ncc.call();
						} else {
							// TODO : Probably means the compiler was not set in
							// parameters, therefore, add warning !
							NitActivator
									.getDefault()
									.getLog()
									.log(new Status(Status.ERROR, MSG_CAT_COMPILER, MSG_COMPILER_MISSING));
						}
					} else {}
				}
			}
			return true;
		}
	}
}
