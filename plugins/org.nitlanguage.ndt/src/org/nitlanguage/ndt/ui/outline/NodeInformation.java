package org.nitlanguage.ndt.ui.outline;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.TextSelection;
import org.nitlanguage.ndt.ui.editor.NitEditor;

/**
 * Convenience structure aimed to contain informations
 * about AST node -mapping with position in original document
 * regardless of the nodes hierarchy
 * @author nathan
 */
class NodeInformation {
	int line = -1;
	int position = -1;
	int length = -1;

	public NodeInformation(){
	}
	
	public NodeInformation(int line, int position, int length){
		this.line = line;
		this.position = position;
		this.length = length;
	}
	
	/**
	 * Returns the number of lines from the beginning
	 * of the document to the line containing the node identifier
	 * @return the line
	 */
	public int getLine(){
		return line;
	}
	
	/**
	 * Returns the number of characters between the beginning
	 * of the line and the beginning of the node identifier
	 * @return the position
	 */
	public int getPos(){
		return position;
	}
	
	/**
	 * Returns the number of characters in the node identifier
	 * @return the size of the node identifer
	 */
	public int getLenght(){
		return length;
	}
	
	/**
	 * Returns the number of characters from the beginning
	 * of the document to the beginning of the node identifier
	 * @param editor
	 * @return the offset of the node identifier
	 */
	public int getOffsetInEditor(NitEditor editor){
		int lineOffset;
		try {
			lineOffset = editor.getDocument().getLineOffset(getLine() - 1);
    		int charOffset = lineOffset + getPos() - 1;
    		return charOffset;
		} catch (BadLocationException e) {
			return -1;
		}
	}
	
	/**
	 * Returns true if the node is matching the text selection
	 * @param text
	 * @param editor
	 * @return
	 */
	public boolean equals(TextSelection text, NitEditor editor){
		return (text.getOffset() == getOffsetInEditor(editor)
				&& text.getLength() == getLenght());
	}
	
	/**
	 * Returns whether or not the node informations are correct
	 * @return
	 */
	public boolean isValid(){
		return(line != -1 && position != -1 && length != -1);
	}
}