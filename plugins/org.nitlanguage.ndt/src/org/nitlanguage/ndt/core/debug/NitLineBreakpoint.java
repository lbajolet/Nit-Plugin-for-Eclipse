package org.nitlanguage.ndt.core.debug;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.LineBreakpoint;

public class NitLineBreakpoint extends LineBreakpoint {

	public NitLineBreakpoint() {

	}

	/**
	 * Constructs a line breakpoint on the given resource at the given line
	 * number. The line number is 1-based (i.e. the first line of a file is line
	 * number 1). The PDA VM uses 0-based line numbers, so this line number
	 * translation is done at breakpoint install time.
	 * 
	 * @param resource
	 *            file on which to set the breakpoint
	 * @param lineNumber
	 *            1-based line number of the breakpoint
	 * @throws CoreException
	 *             if unable to create the breakpoint
	 */
	public NitLineBreakpoint(final IResource resource, final int lineNumber)
			throws CoreException {
		IWorkspaceRunnable runnable = new IWorkspaceRunnable() {
			public void run(IProgressMonitor monitor) throws CoreException {
				IMarker marker = resource
						.createMarker(NitDebugConstants.BREAKPOINT_MARKER_TYPE);
				setMarker(marker);
				marker.setAttribute(IBreakpoint.ENABLED, true);
				marker.setAttribute(IMarker.LINE_NUMBER, lineNumber);
				marker.setAttribute(IBreakpoint.ID, getModelIdentifier());
				marker.setAttribute(IMarker.MESSAGE, "Breakpoint: "
						+ resource.getName() + " -- " + lineNumber);
			}
		};
		run(getMarkerRule(resource), runnable);
	}

	@Override
	public String getModelIdentifier() {
		return NitDebugConstants.MODEL_IDENTIFIER;
	}

}
