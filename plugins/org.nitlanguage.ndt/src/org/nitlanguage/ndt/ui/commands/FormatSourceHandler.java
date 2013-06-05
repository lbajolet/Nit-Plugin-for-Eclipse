package org.nitlanguage.ndt.ui.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.handlers.HandlerUtil;
import org.nitlanguage.ndt.ui.editor.NitEditor;

public class FormatSourceHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		System.out.println("Format Source");
		System.out.println("Step 1 : Correct Indent");
		NitEditor editor = (NitEditor)(HandlerUtil.getActiveEditor(event));
		editor.correct_indent();
		System.out.println("Step 2 : Remove Superfluous Lines");
		editor.clean();
		System.out.println("Step 3 : Trim whitespaces");
		editor.trimWhitespaces();
		return null;
	}
}
