package org.nitlanguage.ndt.ui.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.handlers.HandlerUtil;
import org.nitlanguage.ndt.ui.editor.NitEditor;

public class CorrectIndentationHandler extends AbstractHandler {
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		System.out.println("Functionnality : Correct Indentation");
		NitEditor editor = (NitEditor)(HandlerUtil.getActiveEditor(event));
		editor.correct_indent();
		return null;
	}
	

}
