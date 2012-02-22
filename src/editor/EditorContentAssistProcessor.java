package editor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PushbackReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import lexer.Lexer;
import lexer.LexerException;
import node.AModule;
import node.AStdClassdef;
import node.AStdImport;
import node.ATopClassdef;
import node.PClassdef;
import node.PImport;
import node.Start;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IFileEditorMapping;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.ui.internal.Workbench;
import org.eclipse.ui.internal.editors.text.FileEditorInputAdapterFactory;
import org.eclipse.ui.part.EditorPart;

import parser.Parser;
import parser.ParserException;

public class EditorContentAssistProcessor implements IContentAssistProcessor {

	private String lastError;

	@Override
	public ICompletionProposal[] computeCompletionProposals(
			ITextViewer textViewer, int documentOffset) {
		IDocument document = textViewer.getDocument();

		ArrayList<ICompletionProposal> proposals = new ArrayList<ICompletionProposal>();
		DocumentBufferStream dbs = new DocumentBufferStream();

		dbs.setDoc(document);
		
		Parser pp = new Parser(new Lexer(new PushbackReader(dbs)));
		try {
			Start st = pp.parse();
			AModule amod = (AModule) st.getPModule();
			LinkedList<PClassdef> ll = amod.getClassdefs();
			LinkedList<PImport> pi = amod.getImports();
			for (PImport Import : pi) {
				
				AStdImport CttImport = (AStdImport) Import;
				
				IEditorInput iEditor = PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow().getActivePage()
						.getActivePart().getSite().getPage()
						.getActiveEditor().getEditorInput();
				
				String editorName = iEditor.getName();
				
				String importUri = "";
				String separator = File.separator;
				
				if (iEditor instanceof IFileEditorInput){
					IFileEditorInput inp = (IFileEditorInput) iEditor;
					File fichier = inp.getFile().getRawLocation().toFile();
					String fileAbsoluteUri = fichier.getAbsolutePath();
					importUri = fileAbsoluteUri.replaceFirst(iEditor.getName(), CttImport.getName().toString().trim()+".nit");
				}
				
				Parser parser = new Parser(new Lexer(new PushbackReader(
						new BufferedReader(new FileReader(importUri)))));
				
				Start tempStr = parser.parse();
				
				AModule bmod = (AModule) tempStr.getPModule();
				
				LinkedList<PClassdef> classd = bmod.getClassdefs();
				
				for (PClassdef pclass : classd) {
					if(pclass instanceof AStdClassdef){
						AStdClassdef amc = (AStdClassdef) pclass;
						proposals.add(new CompletionProposal(amc.getId().getText(),
								documentOffset, 0, amc.getId().getText().length()));
					}else if(pclass instanceof ATopClassdef){
						ATopClassdef amc = (ATopClassdef) pclass;
						//proposals.add(new CompletionProposal(amc.,
						//		documentOffset, 0, amc.getId().getText().length()));
						int toto = 0;
					}
				}
			}
			for (PClassdef pclass : ll) {
				if(pclass instanceof AStdClassdef){
					AStdClassdef amc = (AStdClassdef) pclass;
					proposals.add(new CompletionProposal(amc.getId().getText(),
							documentOffset, 0, amc.getId().getText().length()));
				}else if(pclass instanceof ATopClassdef){
					ATopClassdef amc = (ATopClassdef) pclass;
					//proposals.add(new CompletionProposal(amc.,
					//		documentOffset, 0, amc.getId().getText().length()));
					int toto = 0;
				}
				
				
				// LinkedList<PPropdef> propd = amc.getPropdefs();
				// int toto = 0;
			}
		} catch (ParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (LexerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		ICompletionProposal[] suggestions = proposals
				.toArray(new ICompletionProposal[proposals.size()]);

		return suggestions;
	}

	private ICompletionProposal[] buildProposals(List<String> suggestions,
			String replacedWord, int offset) throws Exception {
		ICompletionProposal[] proposals = new ICompletionProposal[suggestions
				.size()];
		int index = 0;
		for (Iterator<String> i = suggestions.iterator(); i.hasNext();) {
			String currSuggestion = (String) i.next();
			CompletionProposal cp = new CompletionProposal(currSuggestion,
					offset, replacedWord.length(), currSuggestion.length(),
					null, currSuggestion, null, null);
			proposals[index] = cp;
			index++;
		}
		return proposals;
	}

	@Override
	public IContextInformation[] computeContextInformation(
			ITextViewer itextviewer, int i) {
		lastError = "No Context Information available";
		return null;
	}

	@Override
	public char[] getCompletionProposalAutoActivationCharacters() {
		return null;
	}

	@Override
	public char[] getContextInformationAutoActivationCharacters() {
		return null;
	}

	@Override
	public IContextInformationValidator getContextInformationValidator() {
		return null;
	}

	@Override
	public String getErrorMessage() {
		return lastError;
	}

}