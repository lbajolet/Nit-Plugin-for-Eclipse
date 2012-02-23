package editor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

import lexer.LexerException;
import node.AModule;
import node.AStdClassdef;
import node.AStdImport;
import node.Start;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.FileStoreEditorInput;

import parser.ParserException;
import asthelpers.AstParserHelper;

public class EditorContentAssistProcessor implements IContentAssistProcessor {

	private String lastError;

	@Override
	public ICompletionProposal[] computeCompletionProposals(
			ITextViewer textViewer, int documentOffset) {

		// Resultsets
		ArrayList<AStdClassdef> classesToPropose = new ArrayList<AStdClassdef>();

		IDocument document = textViewer.getDocument();

		AstParserHelper aph = new AstParserHelper();

		IEditorInput iEditor = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage().getActivePart()
				.getSite().getPage().getActiveEditor().getEditorInput();

		Start st = null;
		try {
			st = aph.getAstForDocument(document);
		} catch (ParserException e) {
		} catch (LexerException e) {
		} catch (IOException e) {
		}

		if (st != null) {

			AModule mod = aph.getModuleOfAST(st);

			if (mod != null) {

				String fileAbsoluteUri = null;

				if (iEditor instanceof IFileEditorInput) {
					IFileEditorInput inp = (IFileEditorInput) iEditor;
					File fichier = inp.getFile().getRawLocation().toFile();
					fileAbsoluteUri = fichier.getAbsolutePath();
				} else if (iEditor instanceof FileStoreEditorInput) {
					FileStoreEditorInput fsei = (FileStoreEditorInput) iEditor;
					File fichier = new File(fsei.getURI());
					fileAbsoluteUri = fichier.getAbsolutePath();
				}

				ArrayList<AStdImport> imports = aph.getImports(mod);

				for (AStdImport imp : imports) {

					String nameOfCurrentFile = iEditor.getName();
					String nitName = imp.getName().toString().trim() + ".nit";

					String importUri = fileAbsoluteUri.replaceFirst(
							nameOfCurrentFile, nitName);

					File importFile = new File(importUri);

					try {
						Start startNode = aph.getAstForFile(importFile);

						AModule importModule = aph.getModuleOfAST(startNode);

						ArrayList<AStdClassdef> classes = aph
								.getClassesOfModule(importModule);

						classesToPropose.addAll(classes);
					} catch (FileNotFoundException e) {
					} catch (IOException e) {
					} catch (ParserException e) {
					} catch (LexerException e) {
					}
				}
			}

			if (mod != null) {
				ArrayList<AStdClassdef> astdclass = aph.getClassesOfModule(mod);
				classesToPropose.addAll(astdclass);

			}
		}

		ICompletionProposal[] classesSuggestions = buildClassProposals(
				classesToPropose, documentOffset);

		return classesSuggestions;
	}

	private ICompletionProposal[] buildClassProposals(
			ArrayList<AStdClassdef> classes, int fromOffset) {

		HashSet<ICompletionProposal> proposalsArrayList = new HashSet<ICompletionProposal>();

		for (AStdClassdef classDef : classes) {
			proposalsArrayList.add(new CompletionProposal(classDef.getId()
					.getText(), fromOffset,
					classDef.getId().getText().length(), 0));
		}

		return proposalsArrayList
				.toArray(new ICompletionProposal[proposalsArrayList.size()]);
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