package org.nitlanguage.ndt.ui.editor;

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
import org.nitlanguage.ndt.ui.docmodel.Declaration;
import org.nitlanguage.ndt.ui.docmodel.DeclarationType;

/**
 * Reconciling Strategy used by the source viewer to fold code
 * @author nathan.heu
 */
public class NitReconcilingStrategy implements IReconcilingStrategy,
IReconcilingStrategyExtension {
	private NitEditor editor = null;
    private IDocument fDocument;
    private final ArrayList<Position> fPositions = new ArrayList<Position>();
    private Stack<Declaration> regionOffsets;
    
	public NitReconcilingStrategy(NitEditor editor){
		this.editor = editor;
	}
	
	@Override
	public void setProgressMonitor(IProgressMonitor monitor) {
		// TODO Auto-generated method stub		
	}

	@Override
	public void initialReconcile() {
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
    			DeclarationType type_decl = Declaration.evaluateDeclaration(rawLine, editor.lineAt(i+1));
    			
    			if(type_decl == DeclarationType.OTHER
    					|| type_decl == DeclarationType.COMMENT)
    			{
    				continue;
    			} 
    			else if(type_decl == DeclarationType.MODULE)
    			{
    				//is this correct to fold module? there is just one module per file...
    				//emitPosition(beginOffset, fDocument.getLength());
    				continue;
    			} 
    			else if(type_decl == DeclarationType.END)
    			{
    				if(regionOffsets.isEmpty()) {
    					System.out.println("[NitReconcilingStrategy] Folding error : try to pop an empty stack : there is more closing section clauses (end) than opening clauses.");
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
    						|| type_decl == DeclarationType.MULTILINE_FUN_CLEAN
    						|| type_decl == DeclarationType.CLASS
    						|| type_decl == DeclarationType.STRUCT){
    				regionOffsets.push(new Declaration(beginOffset, type_decl));
    			}
    		}
    }
    
    /**
     * Add folding regions position
     * @param startOffset Folding region begin
     * @param length Folding region length
     */
    protected void emitPosition(int startOffset, int length) {
            fPositions.add(new Position(startOffset, length));
    }

}
