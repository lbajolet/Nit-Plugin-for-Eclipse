package org.nitlanguage.ndt.ui.editor;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.Stack;

import org.eclipse.core.internal.filebuffers.SynchronizableDocument;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
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
import org.eclipse.swt.widgets.Display;
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
import org.nitlanguage.ndt.ui.docmodel.Declaration;
import org.nitlanguage.ndt.ui.docmodel.DeclarationType;
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
     * Extract line characters from the document
     * @param index
     * @return
     */
	public String lineAt(int index){
		try {
			IRegion lineRegion = getDocument().getLineInformation(index);
			return getDocument().get(lineRegion.getOffset(), lineRegion.getLength());
		} catch (BadLocationException e) {
			return null;
		}
	}
	
	public IDocument getDocument(){
		return getDocumentProvider().getDocument(getEditorInput());		
	}
	
	/**
	 * Add a comment clause at the beginning of each line specified by the set
	 * @param lines
	 * @return
	 */
	public boolean comment(Set<Integer> lines){
		IDocument doc = getDocument();
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
	
	protected String charPrefix(String value, char prefix){
		return charPrefix(value, prefix, 1);
	}
	
	protected String charPrefix(String value, char prefix, int repeat){
		StringBuffer out = new StringBuffer();
		for (int i = 0; i < repeat; i++) out.append(prefix);
		out.append(value);
		return out.toString();
	}
	
	protected String shift(String value, int offset){
		return charPrefix(value.trim(), '\t', offset);
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
	
	public void clean(){
    	IDocument srcDoc = getDocument();
    	List<String> targetDoc = new ArrayList<String>();
    	
		int nbSrcLines = srcDoc.getNumberOfLines();
		
		boolean isModuleSet = false;
		boolean isFirstImport = true;
		boolean isFirstFunction = true;
		//carriage return
		String CR = "\n";
		
		for(int i = 0; i < nbSrcLines; i++)
		{
			try {
				IRegion currentLine = srcDoc.getLineInformation(i);
				int beginOffset = currentLine.getOffset();
				int endOffset = currentLine.getLength();
				String rawLine = srcDoc.get(beginOffset, endOffset);
				
				DeclarationType type_decl = Declaration.evaluateDeclaration(rawLine, lineAt(i+1));
				
				//if module is undefined and current line is empty
				//then we remove this line from target doc
				if(!isModuleSet){
					if(type_decl == DeclarationType.MODULE) {
						targetDoc.add(rawLine);
						isModuleSet = true;
						continue;
					}
					else if(type_decl == DeclarationType.EMPTY_LINE) {
						continue;
					}
					else if(type_decl == DeclarationType.COMMENT) {
						targetDoc.add(formatComment(rawLine, 1));
						continue;
					}
					else {
						targetDoc.add("module AUTOMATIC_NAME");
						isModuleSet = true;
					}
				}
				else if(type_decl == DeclarationType.IMPORT){
					if(isFirstImport){
						targetDoc.add(CR);
						isFirstImport = false;
					}
					targetDoc.add(rawLine);
				}
				else if(type_decl == DeclarationType.CLASS){
					isFirstFunction = true;
					//if last add wasn't a carriage return
					if(!targetDoc.get(targetDoc.size()-1).equals(CR)){
						targetDoc.add(CR);
					}
					targetDoc.add(rawLine);
				}
				else if(type_decl == DeclarationType.MULTILINE_FUN
							|| type_decl == DeclarationType.MULTILINE_FUN_CLEAN){
					//no CR is added if it is the first function of the class
					if(!isFirstFunction){
						//if last add wasn't a carriage return
						if(!targetDoc.get(targetDoc.size()-1).equals(CR)){
							targetDoc.add(CR);
						}
					} else isFirstFunction = false;	
					if(type_decl == DeclarationType.MULTILINE_FUN){
						int offset = getOffset(rawLine);
						String splitedLine = rawLine.trim().substring(0, rawLine.length()- 3);
						targetDoc.add(charPrefix(splitedLine, '\t', offset));
						targetDoc.add(charPrefix("do", '\t', offset));
					}
					else targetDoc.add(rawLine);
				}
				else if(type_decl == DeclarationType.EMPTY_LINE){
					if(!targetDoc.get(targetDoc.size()-1).equals(CR)){
						targetDoc.add(CR);
					}
				} 
				else { 
					targetDoc.add(rawLine);
				}
				
			} catch (BadLocationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		StringBuffer buf = new StringBuffer();
		for(String line : targetDoc){
			buf.append(line);
			if(!line.equals(CR)) buf.append(CR);
		}
		srcDoc.set(buf.toString());
	}
	
	private String formatComment(String comment, int nbSpaces){
		int begin = -1;
		int offset = 0;
		for(int i = 0; i < comment.length(); i++){
			char c = comment.charAt(i);
			if(begin == -1){
				if(Character.isWhitespace(c)) continue;
				if(c == '#') begin = i;
			} else {
				if(Character.isWhitespace(c)) offset++;
				else break;
			}			
		}
		begin++;
		String p1 =  comment.substring(0, begin);
		String tmp = comment.substring( begin + offset, comment.length());
		return p1 + charPrefix(tmp, ' ', nbSpaces);
	}
	
	public int getOffset(String line) {
		char[] s = line.toCharArray();
		int offset = 0;
		for(int i = 0; i < s.length; i++){
			if(i == '\t') offset++;
		}
		return offset;
	}
	
    /**
     * Compute correct offset for each line of the document
     * @throws BadLocationException
     */
    public boolean correct_indent() {
    	IDocument doc = getDocument();
    	Stack<Declaration> regionOffsets = new Stack<Declaration>();
		int nbLines = doc.getNumberOfLines();
		int[] offsets = new int[nbLines];
		
		for(int i = 0; i < nbLines; i++)
		{
			IRegion currentLine;
			int beginOffset = 0, endOffset = 0;
			DeclarationType type_decl = null;
			try {
				currentLine = doc.getLineInformation(i);
				beginOffset = currentLine.getOffset();
				endOffset = currentLine.getLength();
				String rawLine = doc.get(beginOffset, endOffset);
				type_decl = Declaration.evaluateDeclaration(rawLine, lineAt(i+1));
			} catch (BadLocationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(type_decl == null) return false;
			/*
			if(type_decl == DeclarationType.MODULE
				|| type_decl == DeclarationType.OTHER
				|| type_decl == DeclarationType.COMMENT
				|| type_decl == DeclarationType.INLINE_FUN
				|| type_decl == DeclarationType.INLINE_IF)
			{
				continue;
			} */
			if(type_decl == DeclarationType.MULTILINE_FUN_CLEAN 
					|| type_decl == DeclarationType.MULTILINE_FUN 
					|| type_decl == DeclarationType.CLASS
					|| type_decl == DeclarationType.STRUCT)
			{
				regionOffsets.push(new Declaration(beginOffset, type_decl));
			}
			else if(type_decl == DeclarationType.ELSE){
				offsets[i]--;
			}
			else if(type_decl == DeclarationType.END)
			{
				if(regionOffsets.isEmpty()) throw new Error("try to pop an empty stack");
				
				Declaration dec = regionOffsets.pop();
				int realOffset = beginOffset - dec.getBeginOffset() + endOffset;
				int lineBegin = 0, lineEnd = 0;
				try {
					lineBegin = doc.getLineOfOffset(dec.getBeginOffset());
					//if declaration is a clean multiline function declaration
					//*clean means that do keyword is on a new line according to good practice
					//then next line (do) must have the same offset that multiline fun declaration
					if(dec.getType() == DeclarationType.MULTILINE_FUN_CLEAN) lineBegin++;
					lineEnd = doc.getLineOfOffset(dec.getBeginOffset() + realOffset);
				} catch (BadLocationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				/*
				 Block begin : dec.getBeginOffset(), line = lineBegin
				 Block end : dec.getBeginOffset() + realOffset, line = lineEnd
				 Line to shift : (lineBegin+1) to lineEnd-1
				 */
				
				for(int l = lineBegin+1; l < lineEnd; l++)
				{
					offsets[l]++;
				}
			} 
		}
		if(offsets != null && offsets.length != 0){
			Display.getDefault().syncExec(new IndentJob(offsets));
			return true;
		} else {
			System.out.println("Problem : Impossible to indent the module");
			return false;
		}
    }
    
    /**
     * Set the correct offset for each line of the document
     * @author nit
     */
    class IndentJob implements Runnable{
    	int[] offsets;
    	
    	public IndentJob(int[] offsets){
    		this.offsets = offsets;
    	}
    	
		@Override
		public void run() {
			IDocument doc = getDocumentProvider().getDocument(getEditorInput());
			for(int i = 0; i < doc.getNumberOfLines(); i++)
			{
				try {
					IRegion currentLine = doc.getLineInformation(i);
					int indent = offsets[i];
					int beginOffset = currentLine.getOffset();
					int length = currentLine.getLength();
					String rawLine = doc.get(beginOffset, length);
					String shiftedLine = shift(rawLine, indent);
					if(!rawLine.equals(shiftedLine)){
						doc.replace(beginOffset, length, shiftedLine);
					}
				} catch (BadLocationException e) {
					e.printStackTrace();
				}
			}	
		}    	
    }
}