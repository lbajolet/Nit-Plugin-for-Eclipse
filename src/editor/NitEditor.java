package editor;

import java.io.IOException;
import java.io.StringReader;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.ContentAssistAction;
import org.eclipse.ui.texteditor.ITextEditorActionDefinitionIds;

import asthelpers.ProjectAutoParser;
import builder.NitNature;

public class NitEditor extends TextEditor {

	public NitEditor() {
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

	@Override
	public void doSave(IProgressMonitor monitor) {
		super.doSave(monitor);
		try {
			ProjectAutoParser pap = new ProjectAutoParser();

			IFile fileBoundToIDocument = null;

			IEditorInput ie = this.getEditorInput();
			if (ie instanceof FileEditorInput) {
				fileBoundToIDocument = ((FileEditorInput) ie).getFile();
			}

			try {
				fileBoundToIDocument.getProject()
						.getNature(NitNature.NATURE_ID);
			} catch (Exception e) {
				e.printStackTrace();
			}

			pap.addToQueue(fileBoundToIDocument);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}