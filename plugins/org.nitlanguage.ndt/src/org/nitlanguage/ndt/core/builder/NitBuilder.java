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
import org.nitlanguage.ndt.core.BuildMsg;
import org.nitlanguage.ndt.core.PluginParams;
import org.nitlanguage.ndt.core.plugin.NitActivator;

/**
 * The builder for Nit Projets. Used to call for the Nit Compiler. The builder
 * is not incremental since the compiler is not.
 * 
 * @author lucas.bajolet
 */
public class NitBuilder extends IncrementalProjectBuilder {
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
		if (resource instanceof IFile
				&& resource.getName().endsWith(PluginParams.NIT_EXTENSION)) {
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

	/**
	 * Visitor for the files of the project Visits every element of a Project to
	 * build it
	 */
	class NitResourceVisitor implements IResourceVisitor {
		public boolean visit(IResource resource) {
			if (resource instanceof IProject) {
				IProject proj = resource.getProject();
				NitNature self = null;
				try {
					self = (NitNature) proj.getNature(PluginParams.NATURE_ID);
				} catch (CoreException e) {
					// If an exception is thrown, the project was not a Nit
					// Project, therefore, cannot be used to build Nit
				}
				if (self != null) {
					IFile defaultFile = self.getDefaultFile();
					if (defaultFile != null) {
						NitActivator.getDefault().checkNitInstallation();
						String path = NitActivator.getDefault()
								.getNitInstallation().getCompiler();
						if (path != null && path != "") {
							NitCompilerCallerClass ncc = self
									.getCompilerCaller();
							ncc.setPath(path
									+ BuildMsg.COMPILER_ARG_NO_COLOR
									+ " "
									+ BuildMsg.COMPILER_ARG_OUTPUT
									+ " "
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
									.log(new Status(Status.ERROR,
											BuildMsg.MSG_CAT_COMPILER,
											BuildMsg.ERROR_COMPILER_MISSING));
						}
					} else {
					}
				}
			}
			return true;
		}
	}
}
