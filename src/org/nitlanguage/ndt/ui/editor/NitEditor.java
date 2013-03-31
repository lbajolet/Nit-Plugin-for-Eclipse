package org.nitlanguage.ndt.ui.editor;

import java.io.IOException;
import java.io.StringReader;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.AnnotationModel;
import org.eclipse.jface.text.source.projection.ProjectionSupport;
import org.eclipse.ui.IEditorReference;
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

	ProjectionSupport projectionSupport;
	AnnotationModel annotationModel;
	NitOutlinePage outlinePage;
	
	public NitEditor() {
		//clean doc (whitespaces,...)
		setDocumentProvider(new NitDocumentProvider());
		setSourceViewerConfiguration(new NitEditorConfiguration());
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
					if (fileBoundToIDocument.getFileExtension().contains("nit")) {
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