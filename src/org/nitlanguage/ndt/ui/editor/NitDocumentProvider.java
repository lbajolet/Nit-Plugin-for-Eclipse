package org.nitlanguage.ndt.ui.editor;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.text.edits.DeleteEdit;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.text.undo.DocumentUndoManagerRegistry;
import org.eclipse.text.undo.IDocumentUndoManager;
import org.eclipse.ui.editors.text.TextFileDocumentProvider;

/**
 * 
 * @author nathan.heu
 */
public class NitDocumentProvider extends TextFileDocumentProvider{
	/**
	 * Default constructor
	 * Called when launching the plugin if the editor is part of the run
	 */
	public NitDocumentProvider()
	{
	}
	
	/**
	 * If this DocumentProvider is registered as default for a given AbstractTextEditor
	 * this method will be called for each save action
	 */
	protected DocumentProviderOperation createSaveOperation(final Object element, final IDocument document, final boolean overwrite) throws CoreException 
	{
		performCleanActions(document);
		return super.createSaveOperation(element, document, overwrite);
	}
	
	/**
	 * Manages the cleaning of the document being edited
	 */
	private void performCleanActions(IDocument document) {
          TextEdit edit = trimWhitespaces(document);
          if (edit != null) {
                try {
                      IDocumentUndoManager manager = DocumentUndoManagerRegistry.getDocumentUndoManager(document);
                      manager.beginCompoundChange();
                      edit.apply(document);
                      manager.endCompoundChange();
                } catch (MalformedTreeException e) {
                	
                } catch (BadLocationException e) {
                	
                }
          }
	}
	
	/**
	 * Removes whitespaces at the end of each line of the document (if present) 
	 * Prevent versioning conflicts
	 * @param document
	 * @return
	 */
	private TextEdit trimWhitespaces(IDocument document) 
	{
        TextEdit rootEdit = null;
        TextEdit lastWhitespaceEdit = null;
        try {
              int firstLine = document.getLineOfOffset(0);
              int lastLine = document.getLineOfOffset(document.getLength()-1);
              for (int line = firstLine; line <= lastLine; line++) {
                    IRegion lineRegion = document.getLineInformation(line);
                    if (lineRegion.getLength() == 0) continue;
                    int lineStart = lineRegion.getOffset();
                    int lineEnd = lineStart + lineRegion.getLength();
                    // Find the rightmost non whitespace character
                    int charPos = lineEnd - 1;
                    while (charPos >= lineStart && Character.isWhitespace(document.getChar(charPos)))
                          charPos--;
                    charPos++;
                    if (charPos < lineEnd) {
                          lastWhitespaceEdit= new DeleteEdit(charPos, lineEnd - charPos);
                          if (rootEdit == null) {rootEdit = new MultiTextEdit();}
                          rootEdit.addChild(lastWhitespaceEdit);
                    }
              }
        } catch (BadLocationException e) {}
        return rootEdit;
  }
}
