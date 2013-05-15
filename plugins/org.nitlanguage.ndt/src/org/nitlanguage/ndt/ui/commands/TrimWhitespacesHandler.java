package org.nitlanguage.ndt.ui.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.handlers.HandlerUtil;
import org.nitlanguage.ndt.ui.editor.NitEditor;

public class TrimWhitespacesHandler extends AbstractHandler {
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		System.out.println("Functionnality : Trim Whitespaces");
		NitEditor editor = (NitEditor)(HandlerUtil.getActiveEditor(event));
		editor.trimWhitespaces();
		return null;
	}
}
