package org.nitlanguage.ndt.ui.editor;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.Set;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.projection.ProjectionAnnotation;
import org.eclipse.jface.text.source.projection.ProjectionAnnotationModel;
import org.eclipse.jface.text.source.projection.ProjectionSupport;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.ContentAssistAction;
import org.eclipse.ui.texteditor.ITextEditorActionDefinitionIds;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.nitlanguage.ndt.core.asthelpers.ProjectAutoParser;
import org.nitlanguage.ndt.core.plugin.NitActivator;
import org.nitlanguage.ndt.ui.editor.outline.NitOutlinePage;

/**
 * The editor for nit, bound to the completion methods and the
 * highlighting functionalities
 * @author lucas.bajolet
 * @author nathan.heu
 */
public class NitEditor extends TextEditor {
	public static final String NIT_FILE_EXTENSION = "nit";
	char comment = '#';
	
	ProjectionSupport projectionSupport;
	Annotation[] oldAnnotations;
	ProjectionAnnotationModel annotationModel;
	
	NitOutlinePage outlinePage;
	
	public NitEditor() {
		super();
		//clean doc (whitespaces,...)
		setDocumentProvider(new NitDocumentProvider());
		setSourceViewerConfiguration(new NitEditorConfiguration(this));
	}
	
	/**
	 * Get the file being edited
	 * @return
	 */
	public IFile getCurrentFile(){
		 return ((IFileEditorInput)this.getEditorInput()).getFile();
	}
	
	/**
	 * Get the portion of text selected in the editor
	 * @return
	 */
	public TextSelection getSelection(){
		ISelection infos = doGetSelection();
		if(infos.isEmpty()) return null;
		if(infos instanceof TextSelection && !infos.isEmpty()){
			TextSelection selec = (TextSelection)infos;
			if(selec.getLength() == -1) return null;
			else return selec;
		}
		else return null;
	}
	
	/**
	 * Defines the portion of text selected in the editor
	 * @param offset
	 * @param length
	 */
	public void setSelection(int offset, int length){
		TextSelection selection = new TextSelection(offset, length);
		doSetSelection(selection);
	}
	
	/**
	 * Returns a map containing the text selected in the editor line by line with the associated line number as key
	 * @return
	 */
	public HashMap<Integer, String> getSelectedLines(){
		TextSelection selection = getSelection();
		if(selection == null) return null;
		HashMap<Integer, String> lines = new HashMap<Integer, String>();
		int startl = selection.getStartLine(); 
		int endl = selection.getEndLine();
		if(startl != endl) {
			IDocument doc = getDocumentProvider().getDocument(getEditorInput());
			for(int i = startl; i <= endl; i++){
				try {
					IRegion line_infos = doc.getLineInformation(i);
					lines.put(i, doc.get(line_infos.getOffset(), line_infos.getLength()) );
				} catch (BadLocationException e) {
					return null;
				} 
			}
		}
		else {
			lines.put(startl, selection.getText());
		}
		return lines;
	}
	
	/**
	 * Add a comment clause at the beginning of each line specified by the set
	 * @param lines
	 * @return
	 */
	public boolean comment(Set<Integer> lines){
		IDocument doc = getDocumentProvider().getDocument(getEditorInput());
		for(int line : lines){
			try {
				IRegion line_infos = doc.getLineInformation(line);
				String value = doc.get(line_infos.getOffset(), line_infos.getLength());
				doc.replace(line_infos.getOffset(), line_infos.getLength(), Character.toString(comment) + value);
			} catch (BadLocationException e) {
				return false;
			} 
		}
		return true;
	}
	
	/**
	 * Remove the comment clause at the beginning of each line specified by the set
	 * @param lines
	 * @return
	 */
	public boolean uncomment(Set<Integer> lines){
		IDocument doc = getDocumentProvider().getDocument(getEditorInput());
		for(int line : lines){
			try {
				IRegion line_infos = doc.getLineInformation(line);
				String value = doc.get(line_infos.getOffset(), line_infos.getLength());
				for(int i = 0; i < value.length(); i++ ){
					if(Character.isWhitespace(value.charAt(i))) continue;
					else if(value.charAt(i) == comment){
						String new_value = value.substring(0, i) + value.substring(i+1, value.length());
						doc.replace(line_infos.getOffset(), line_infos.getLength(), new_value);
					}
				}
				
			} catch (BadLocationException e) {
				return false;
			} 
		}
		return true;
	}
	
	@Override
	public void createPartControl(Composite parent){
	    super.createPartControl(parent);
	    ProjectionViewer viewer =(ProjectionViewer)getSourceViewer();
	    projectionSupport = new ProjectionSupport(viewer,getAnnotationAccess(),getSharedColors());
	    projectionSupport.install();
	    //turn projection mode on
	    viewer.doOperation(ProjectionViewer.TOGGLE);
	    annotationModel = viewer.getProjectionAnnotationModel();
	}
	
	@Override
	/**
	 * Creates the source viewer to be used by this editor
	 */
	protected ISourceViewer createSourceViewer(Composite parent, IVerticalRuler ruler, int styles){
		ISourceViewer viewer = new ProjectionViewer(parent, ruler,getOverviewRuler(), isOverviewRulerVisible(), styles);
		// ensure decoration support has been created and configured.
		getSourceViewerDecorationSupport(viewer);
		return viewer;
	}
	
	public void updateFoldingStructure(ArrayList<Position> positions)
	{
		Annotation[] annotations = new Annotation[positions.size()];		
		//this will hold the new annotations along
		//with their corresponding positions
		HashMap<Annotation, Position> newAnnotations = new HashMap<Annotation, Position>();
		
		for(int i =0;i<positions.size();i++)
		{
			ProjectionAnnotation annotation = new ProjectionAnnotation();			
			newAnnotations.put(annotation,positions.get(i));		
			annotations[i]=annotation;
		}		
		annotationModel.modifyAnnotations(oldAnnotations,newAnnotations,null);		
		oldAnnotations=annotations;
	}
	
	@Override
	protected void createActions() {
		super.createActions();
		ResourceBundle resourceBundle = null;
		try {
			resourceBundle = new PropertyResourceBundle(
					new StringReader(
							"ContentAssistProposal.label=Content assist\nContentAssistProposal.tooltip=Content assist\nContentAssistProposal.description=Provides Content Assistance"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		ContentAssistAction action = new ContentAssistAction(resourceBundle,
				"ContentAssistProposal.", this);
		String id = ITextEditorActionDefinitionIds.CONTENT_ASSIST_PROPOSALS;
		action.setActionDefinitionId(id);
		setAction("ContentAssist", action);
	}
	
	@Override
	protected void updateMarkerViews(Annotation annotation) {
		super.updateMarkerViews(annotation);
	}

	/**
	 * Returns an object which is an instance of the given class associated with this object. 
	 * Returns null if no such object can be found.
	 */
	public Object getAdapter(Class required) {
	      if (IContentOutlinePage.class.equals(required)) {
	         if (outlinePage == null) {
	            outlinePage = new NitOutlinePage(this);
	         }
	         return outlinePage;
	      }
	      return super.getAdapter(required);
	   }

	@Override
	public void doSave(IProgressMonitor monitor) {
		super.doSave(monitor);
		outlinePage.refresh(true);
		try {
			ProjectAutoParser pap = new ProjectAutoParser();

			IFile fileBoundToIDocument = null;

			// Get all files open
			IEditorReference[] editors = PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getActivePage()
					.getEditorReferences();

			for (IEditorReference ed : editors) {
				if (ed.getEditorInput() instanceof FileEditorInput) {
					fileBoundToIDocument = ((FileEditorInput) ed
							.getEditorInput()).getFile();
					if (fileBoundToIDocument.getFileExtension().contains(NIT_FILE_EXTENSION)) {
						pap.addToQueue(fileBoundToIDocument);
					}
				}
			}
		} catch (Exception e) {
			if (NitActivator.DEBUG_MODE)
				e.printStackTrace();
		}
	}
}