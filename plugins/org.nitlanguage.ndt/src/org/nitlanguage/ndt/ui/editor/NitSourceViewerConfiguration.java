package org.nitlanguage.ndt.ui.editor;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.reconciler.IReconciler;
import org.eclipse.jface.text.reconciler.MonoReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.custom.LineBackgroundEvent;
import org.eclipse.swt.custom.LineBackgroundListener;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.ui.editors.text.TextSourceViewerConfiguration;
import org.eclipse.ui.texteditor.ITextEditor;

/**
 * Bundles the configuration space of a source viewer.
 * @author lucas.bajolet
 * @author nathan.heu
 */
public class NitSourceViewerConfiguration extends TextSourceViewerConfiguration {
	private ITokenScanner scanner = null;
	private NitEditor editor = null;
	private ContentAssistant assistant = null;
	 
	public NitSourceViewerConfiguration(NitEditor editor){
		this.editor = editor;
	}
	
	/**
	 * Returns the parent ITextEditor
	 * @return
	 */
	public ITextEditor getEditor(){
		return editor;
	}
	
	@Override
	public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {
		PresentationReconciler reconciler = new PresentationReconciler();
 
		DefaultDamagerRepairer dr = new DefaultDamagerRepairer(getScanner());
		reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
		reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);
		
		return reconciler;
	}
	
	@Override
	public IContentAssistant getContentAssistant(ISourceViewer sourceViewer) {
		if(assistant==null){
			assistant = new ContentAssistant();
			assistant.setContentAssistProcessor(new EditorContentAssistProcessor(), IDocument.DEFAULT_CONTENT_TYPE);
			assistant.setInformationControlCreator(getInformationControlCreator(sourceViewer));
		}
		return assistant;
	}

	/**
	 * Returns the ITokenScanner used by the presentation damager&rapairer
	 * @return
	 */
	private ITokenScanner getScanner(){
		if(scanner == null){
			scanner = new NitScanner();
			editor.addLineBackgroundListener((NitScanner)scanner);
		}
		return scanner;
	}
	
	/**
	 * org.eclipse.jface.text.reconciler.IReconciler
	 * An IReconciler defines and maintains a model of the content of the text viewer's 
	 * document in the presence of changes applied to this document.
	 */
    public IReconciler getReconciler(ISourceViewer sourceViewer)
    {
        NitReconcilingStrategy strategy = new NitReconcilingStrategy(editor);        
        //Standard implementation of IReconciler. 
        //The reconciler is configured with a single reconciling strategy that is used 
        //independently from where a dirty region is located in the reconciler's document.
        MonoReconciler reconciler = new MonoReconciler(strategy,false);        
        return reconciler;
    }
}
