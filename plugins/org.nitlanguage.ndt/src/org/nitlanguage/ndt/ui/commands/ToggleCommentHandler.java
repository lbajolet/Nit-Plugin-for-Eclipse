package org.nitlanguage.ndt.ui.commands;

import java.util.HashMap;
import java.util.Map.Entry;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.nitlanguage.ndt.ui.editor.NitEditor;

public class ToggleCommentHandler extends AbstractHandler {
	
	char comment = '#';
	
	/*
	 * Performs processing of code comments
	 * If the selected portion of code has no comment, comments are added to the beginning of each line
	 * Else if each line begin with a comment, comments are removed.
	 * Else (some lines begin with a comment and some other does not have comment) comments are added to the beginning of each line
	 * @see org.eclipse.core.commands.AbstractHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
	 @Override
	 public Object execute(ExecutionEvent event) throws ExecutionException {
	     //HandlerUtil.getActiveWorkbenchWindow(event).close();
		 NitEditor edit = (NitEditor)(HandlerUtil.getActiveEditor(event));
		 HashMap<Integer, String> lines = edit.getSelectedLines();
		 boolean action_uncomment = true;
		 
		 for(Entry<Integer, String> line : lines.entrySet()){
			 if(startsWith(line.getValue(), comment)) continue;
			 else action_uncomment = false;
		 }
		 TextSelection sel = edit.getSelection();
		 int dif = 0;
		 if(action_uncomment) {
			 dif -= lines.size();
			 edit.uncomment(lines.keySet());		 
		 }
		 else {
			 dif += lines.size();
			 edit.comment(lines.keySet());
		 }
		 edit.setSelection(sel.getOffset(), sel.getLength() + dif);
		 return null;
	 }
	 
	 /*
	  * Tests if this string starts with the specified character 
	  */
	 private boolean startsWith(String s, char c){
		for(int i = 0; i < s.length(); i++ ){
			if(Character.isWhitespace(s.charAt(i))) continue;
			else if(s.charAt(i) == c) return true;
			else return false;
		}
		return false;
	 }
}
