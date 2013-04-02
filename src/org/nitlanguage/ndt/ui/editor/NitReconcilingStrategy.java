package org.nitlanguage.ndt.ui.editor;

import java.nio.Buffer;
import java.util.ArrayList;
import java.util.Stack;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.reconciler.DirtyRegion;
import org.eclipse.jface.text.reconciler.IReconcilingStrategy;
import org.eclipse.jface.text.reconciler.IReconcilingStrategyExtension;
import org.eclipse.swt.widgets.Display;

public class NitReconcilingStrategy implements IReconcilingStrategy,
IReconcilingStrategyExtension {
	private NitEditor editor = null;
    private IDocument fDocument;
    /** The offset of the next character to be read */
    protected int fOffset;
    /** The end offset of the range to be scanned */
    protected int fRangeEnd;
    /**
     * next character position - used locally and only valid while
     * {@link #calculatePositions()} is in progress.
     */
    protected int cNextPos = 0;
    /** number of newLines found by {@link #classifyTag()} */
    protected int cNewLines = 0;
    /** holds the calculated positions */
    protected final ArrayList fPositions = new ArrayList();
    protected char cLastNLChar = ' ';
    
    Stack<Declaration> regionOffsets;
    
	public NitReconcilingStrategy(NitEditor editor){
		this.editor = editor;
	}
	
	@Override
	public void setProgressMonitor(IProgressMonitor monitor) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void initialReconcile() {
        fOffset = 0;
        fRangeEnd = fDocument.getLength();
        calculatePositions();		
	}

	@Override
	public void setDocument(IDocument document) {
        this.fDocument = document;	
	}

	@Override
	public void reconcile(DirtyRegion dirtyRegion, IRegion subRegion) {
		initialReconcile();		
	}

	@Override
	public void reconcile(IRegion partition) {
		initialReconcile();	
	}
	
    /**
     * uses {@link #fDocument}, {@link #fOffset} and {@link #fRangeEnd} to
     * calculate {@link #fPositions}. About syntax errors: this method is not a
     * validator, it is useful.
     * @throws BadLocationException 
     */
    protected void calculatePositions(){
            fPositions.clear();
            cNextPos = fOffset;
            try {
				processRegions();
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
            Display.getDefault().asyncExec(new Runnable() {
                    public void run() {
                            editor.updateFoldingStructure(fPositions);
                    }
            });
    }
    
    /**
     * Remove visibility (public, protected, private) and refinment (redef) keywords
     * @param line
     * @return
     */
    private String removeModificatorKeyword(String line){
    	if(line.startsWith("redef ")){
    		return line.substring(5, line.length()-1).trim();
    	} else if(line.startsWith("public ")){
    		return line.substring(6, line.length()-1).trim();
    	} else if(line.startsWith("protected ")){
    		return line.substring(9, line.length()-1).trim();
    	} else if(line.startsWith("private ")){
    		return line.substring(7, line.length()-1).trim();
    	} else {
    		return line;
    	}
    }
    
    private String cleanLine(String line){
    	char sep_line[] = line.toCharArray();
    	int nbBlankChars = 0;
    	for(char c : sep_line)
    	{
    		if(c == '\t' || c == '\n' || c == ' ') nbBlankChars++;
    		else break;
    	}
    	if (nbBlankChars > 0) return line.substring(nbBlankChars);
    	return line;
    }
    
    private DeclarationType evaluateDeclaration(String line, int nextLineIndex){
    	line = removeModificatorKeyword(cleanLine(line));
    	
    	if(line.startsWith("#")){
    		return DeclarationType.COMMENT;
    	} else if(line.startsWith("module ")){
    		return DeclarationType.MODULE;
    	} else if(line.startsWith("class ") 
    				|| line.startsWith("interface ")){
    		return DeclarationType.CLASS;
    	} else if(line.startsWith("fun ") 
    				|| line.startsWith("init ")
    				|| line.startsWith("init(")){
    		String nextLine = cleanLine(extractLineValue(nextLineIndex));
    		if(line.endsWith(" end") || 
    				(!line.endsWith(" do") && !nextLine.equals("do"))){
    			return DeclarationType.INLINE_FUN;
    		}
    		return DeclarationType.MULTILINE_FUN;
    	} else if(line.startsWith("do ")
					|| line.startsWith("while ")
    				|| line.startsWith("loop ") 
    				|| line.startsWith("assert ")
    				|| line.startsWith("for ")){
    		return DeclarationType.STRUCT;
    	} else if(line.startsWith("if ")){
    		if(line.endsWith("then")){
        		return DeclarationType.STRUCT;
    		}
    		else return DeclarationType.INLINE_IF;
    	} else if(line.equals("end")){
    		return DeclarationType.END;
    	}
    	return DeclarationType.OTHER;
    }
    
    private class Declaration{
    	private int beginOffset;
    	private DeclarationType type;
    	
    	public Declaration(int beginOffset, DeclarationType type){
    		this.beginOffset = beginOffset;
    		this.type = type;
    	}
    	
    	public int getBeginOffset(){
    		return beginOffset; 
    	}
    	
    	public DeclarationType getType(){
    		return type;
    	}
    }
    
    public enum DeclarationType {
    	MODULE, CLASS, MULTILINE_FUN, INLINE_FUN, END, INLINE_IF, STRUCT, COMMENT, OTHER
    }
    
    public String extractLineValue(int index){
		try {
			IRegion lineRegion = fDocument.getLineInformation(index);
			return fDocument.get(lineRegion.getOffset(), lineRegion.getLength());
		} catch (BadLocationException e) {
			return null;
		}
    }
    
    /**
     * emits tokens to {@link #fPositions}.
     * @throws BadLocationException 
     */
    protected void processRegions() throws BadLocationException{
    		regionOffsets = new Stack<Declaration>();
    		int nbLines = fDocument.getNumberOfLines();
    		for(int i = 0; i < nbLines; i++)
    		{
    			IRegion currentLine = fDocument.getLineInformation(i);
    			int beginOffset = currentLine.getOffset();
    			int endOffset = currentLine.getLength();
    			String rawLine = fDocument.get(beginOffset, endOffset);
    			DeclarationType type_decl = evaluateDeclaration(rawLine, i+1);
    			
    			if(type_decl == DeclarationType.OTHER
    					|| type_decl == DeclarationType.COMMENT)
    			{
    				continue;
    			} 
    			else if(type_decl == DeclarationType.MODULE)
    			{
    				emitPosition(beginOffset, fDocument.getLength());
    				continue;
    			} 
    			else if(type_decl == DeclarationType.END)
    			{
    				if(regionOffsets.isEmpty()) {
    					System.out.println("problem : try to pop an empty stack");
    					continue;
    				}
    				Declaration dec = regionOffsets.pop();
    				if(dec.getType() != DeclarationType.STRUCT){
        				int realOffset = beginOffset - dec.getBeginOffset() + endOffset;
        				emitPosition(dec.getBeginOffset(), realOffset + 1);
    				}
    			} 
    			else if(type_decl == DeclarationType.INLINE_FUN
    						|| type_decl == DeclarationType.INLINE_IF)
    			{
					//no folding if online fun or if
    				//emitPosition(beginOffset, endOffset);	
    			}
    			else if(type_decl == DeclarationType.MULTILINE_FUN 
    						|| type_decl == DeclarationType.CLASS
    						|| type_decl == DeclarationType.STRUCT){
    				regionOffsets.push(new Declaration(beginOffset, type_decl));
    			}
    		}
    }
    
    protected void emitPosition(int startOffset, int length) {
            fPositions.add(new Position(startOffset, length));
    }

}
