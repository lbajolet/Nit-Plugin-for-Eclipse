package org.nitlanguage.ndt.ui.debug;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.ILineBreakpoint;
import org.eclipse.debug.ui.actions.IToggleBreakpointsTarget;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.texteditor.ITextEditor;
import org.nitlanguage.ndt.core.debug.NitDebugConstants;
import org.nitlanguage.ndt.core.debug.NitLineBreakpoint;

public class NitBreakpointAdapter implements IToggleBreakpointsTarget {

	@Override
	public void toggleLineBreakpoints(IWorkbenchPart part, ISelection selection)
			throws CoreException {
		ITextEditor textEditor = getEditor(part);
		if (textEditor != null) {
			IResource resource = (IResource) textEditor.getEditorInput()
					.getAdapter(IResource.class);
			ITextSelection textSelection = (ITextSelection) selection;
			int lineNumber = textSelection.getStartLine();
			IBreakpoint[] breakpoints = DebugPlugin.getDefault()
					.getBreakpointManager()
					.getBreakpoints(NitDebugConstants.MODEL_IDENTIFIER);
			for (int i = 0; i < breakpoints.length; i++) {
				IBreakpoint breakpoint = breakpoints[i];
				if (resource.equals(breakpoint.getMarker().getResource())) {
					if (((ILineBreakpoint) breakpoint).getLineNumber() == (lineNumber + 1)) {
						// remove
						breakpoint.delete();
						return;
					}
				}
			}
			// create line breakpoint (doc line numbers start at 0)
			NitLineBreakpoint lineBreakpoint = new NitLineBreakpoint(resource,
					lineNumber + 1);
			DebugPlugin.getDefault().getBreakpointManager()
					.addBreakpoint(lineBreakpoint);
		}
	}

	/**
	 * Returns the editor being used to edit a PDA file, associated with the
	 * given part, or <code>null</code> if none.
	 * 
	 * @param part
	 *            workbench part
	 * @return the editor being used to edit a PDA file, associated with the
	 *         given part, or <code>null</code> if none
	 */
	private ITextEditor getEditor(IWorkbenchPart part) {
		if (part instanceof ITextEditor) {
			ITextEditor editorPart = (ITextEditor) part;
			IResource resource = (IResource) editorPart.getEditorInput()
					.getAdapter(IResource.class);
			if (resource != null) {
				String extension = resource.getFileExtension();
				if (extension != null && extension.equals("pda")) {
					return editorPart;
				}
			}
		}
		return null;
	}

	@Override
	public boolean canToggleLineBreakpoints(IWorkbenchPart part,
			ISelection selection) {
		return false;
	}

	@Override
	public void toggleMethodBreakpoints(IWorkbenchPart part,
			ISelection selection) throws CoreException {
	}

	@Override
	public boolean canToggleMethodBreakpoints(IWorkbenchPart part,
			ISelection selection) {
		return false;
	}

	@Override
	public void toggleWatchpoints(IWorkbenchPart part, ISelection selection)
			throws CoreException {
	}

	@Override
	public boolean canToggleWatchpoints(IWorkbenchPart part,
			ISelection selection) {
		return false;
	}

}
