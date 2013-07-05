package org.nitlanguage.ndt.ui.debug;

import org.eclipse.core.resources.IFile;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.ui.IDebugModelPresentation;
import org.eclipse.debug.ui.IValueDetailListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.part.FileEditorInput;
import org.nitlanguage.ndt.core.debug.NitStackFrame;
import org.nitlanguage.ndt.ui.editor.NitEditor;

public class NitDebugModelPresentation extends LabelProvider implements
		IDebugModelPresentation {

	@Override
	public IEditorInput getEditorInput(Object element) {
		if (element instanceof IFile) {
			return new FileEditorInput((IFile) element);
		} else if (element instanceof NitStackFrame) {
			System.out.println("Screw me");
		}
		return null;
	}

	@Override
	public String getEditorId(IEditorInput input, Object element) {
		return NitEditor.NIT_EDITOR_ID;
	}

	@Override
	public void setAttribute(String attribute, Object value) {
	}

	@Override
	public void computeDetail(IValue value, IValueDetailListener listener) {
		String detail = "";
		try {
			detail = value.getValueString();
		} catch (DebugException e) {
		}
		listener.detailComputed(value, detail);
	}

}
