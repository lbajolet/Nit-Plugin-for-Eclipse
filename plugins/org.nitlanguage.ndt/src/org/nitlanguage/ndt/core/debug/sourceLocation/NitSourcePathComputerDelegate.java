package org.nitlanguage.ndt.core.debug.sourceLocation;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.sourcelookup.ISourceContainer;
import org.eclipse.debug.core.sourcelookup.ISourcePathComputerDelegate;
import org.eclipse.debug.core.sourcelookup.containers.WorkspaceSourceContainer;

public class NitSourcePathComputerDelegate implements
		ISourcePathComputerDelegate {

	// Looks in the whole workspace by default
	// Ideally check in the project first
	@Override
	public ISourceContainer[] computeSourceContainers(
			ILaunchConfiguration configuration, IProgressMonitor monitor)
			throws CoreException {
		System.out.println("In source container computer");
		return new ISourceContainer[] { new WorkspaceSourceContainer() };
	}
}
