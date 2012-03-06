package editor;

import java.sql.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;

import node.AConcreteMethPropdef;
import node.ADeferredMethPropdef;
import node.AModule;
import node.AStdClassdef;
import node.PPropdef;
import node.Start;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;

import asthelpers.AstParserHelper;

public class EditorContentAssistProcessor implements IContentAssistProcessor {

	private String lastError;

	@Override
	public ICompletionProposal[] computeCompletionProposals(
			ITextViewer textViewer, int documentOffset) {

		// Resultsets
		ArrayList<AStdClassdef> classesToPropose = new ArrayList<AStdClassdef>();
		ArrayList<ICompletionProposal[]> methodsToPropose = new ArrayList<ICompletionProposal[]>();

		String startsWith = "";

		IDocument document = textViewer.getDocument();

		StringBuffer startBuf = new StringBuffer();
		StringBuffer endBuf = new StringBuffer();

		try {
			char tempChar;
			int currOffset = documentOffset - 1;

			// Bufferize from currOffset to start delimiter
			do {
				tempChar = document.getChar(currOffset);
				startBuf.append(tempChar);
				currOffset--;
			} while (tempChar != '.' && tempChar != ' ' && tempChar != '\n'
					&& tempChar != '\t');

			// Bufferize from currOffset to end delimiter
			currOffset = documentOffset;
			do {
				tempChar = document.getChar(currOffset);
				endBuf.append(tempChar);
				currOffset++;
			} while (tempChar != '.' && tempChar != ' ' && tempChar != '\n'
					&& tempChar != '\t');

			startsWith = startBuf.reverse().substring(1, startBuf.length())
					+ endBuf.substring(0, endBuf.length() - 1);
		} catch (Exception e) {
		}

		AstParserHelper aph = new AstParserHelper();

		/*
		 * IEditorInput iEditor = PlatformUI.getWorkbench()
		 * .getActiveWorkbenchWindow().getActivePage().getActivePart()
		 * .getSite().getPage().getActiveEditor().getEditorInput();
		 */

		Start st = null;

		st = aph.getAstForDocument(document);

		if (st != null) {

			AModule mod = aph.getModuleOfAST(st);
			/*
			 * if (mod != null) {
			 * 
			 * String fileAbsoluteUri = null;
			 * 
			 * 
			 * if (iEditor instanceof IFileEditorInput) { IFileEditorInput inp =
			 * (IFileEditorInput) iEditor; File fichier =
			 * inp.getFile().getRawLocation().toFile(); fileAbsoluteUri =
			 * fichier.getAbsolutePath(); } else if (iEditor instanceof
			 * FileStoreEditorInput) { FileStoreEditorInput fsei =
			 * (FileStoreEditorInput) iEditor; File fichier = new
			 * File(fsei.getURI()); fileAbsoluteUri = fichier.getAbsolutePath();
			 * }
			 */

			// ArrayList<AStdImport> imports = aph.getImports(mod);

			/*
			 * for (AStdImport imp : imports) {
			 * 
			 * String nameOfCurrentFile = iEditor.getName(); String nitName =
			 * imp.getName().toString().trim() + ".nit";
			 * 
			 * String importUri = fileAbsoluteUri.replaceFirst(
			 * nameOfCurrentFile, nitName);
			 * 
			 * File importFile = new File(importUri);
			 * 
			 * try { Start startNode = aph.getAstForFile(importFile);
			 * 
			 * AModule importModule = aph.getModuleOfAST(startNode);
			 * 
			 * ArrayList<AStdClassdef> classes = aph
			 * .getClassesOfModule(importModule);
			 * 
			 * classesToPropose.addAll(classes); } catch (FileNotFoundException
			 * e) { } catch (IOException e) { } catch (ParserException e) { }
			 * catch (LexerException e) { } }
			 * 
			 * }
			 */

			if (mod != null) {
				ArrayList<AStdClassdef> astdclass = aph.getClassesOfModule(mod);
				classesToPropose.addAll(astdclass);
				for (AStdClassdef claas : astdclass) {
					LinkedList<PPropdef> props = aph.getPropsOfClass(claas);
					methodsToPropose.add(this.buildMethProposals(
							aph.getConcreteMethsInPropList(props),
							aph.getDeferredMethsInPropList(props),
							documentOffset, startsWith));
				}
			}
		}

		ICompletionProposal[] classesSuggestions = buildClassProposals(
				classesToPropose, documentOffset, startsWith);

		int totalLength = classesSuggestions.length;

		for (ICompletionProposal[] comps : methodsToPropose) {
			totalLength += comps.length;
		}

		ICompletionProposal[] finalProposals = new ICompletionProposal[totalLength];

		int currLength = 0;

		for (ICompletionProposal comp : classesSuggestions) {
			finalProposals[currLength] = comp;
			currLength++;
		}

		for (ICompletionProposal[] comps : methodsToPropose) {
			if (comps.length > 0) {
				System.arraycopy(comps, 0, finalProposals, currLength,
						comps.length);
				currLength += comps.length;
			}
		}

		return finalProposals;
	}

	private ICompletionProposal[] buildClassProposals(
			ArrayList<AStdClassdef> classes, int fromOffset, String startsWith) {

		HashSet<ICompletionProposal> proposalsArrayList = new HashSet<ICompletionProposal>();
		HashSet<String> completionsHashSet = new HashSet<String>();

		for (AStdClassdef classDef : classes) {
			String toPropose = classDef.getId().getText().trim();
			if (toPropose.toLowerCase().startsWith(startsWith.toLowerCase())) {
				if (completionsHashSet.add(toPropose)) {
					proposalsArrayList.add(new CompletionProposal(toPropose,
							fromOffset - startsWith.length(), startsWith
									.length(), toPropose.length()));
				}
			}
		}

		return proposalsArrayList
				.toArray(new ICompletionProposal[proposalsArrayList.size()]);
	}

	private ICompletionProposal[] buildMethProposals(
			ArrayList<AConcreteMethPropdef> methsProp,
			ArrayList<ADeferredMethPropdef> defMeths, int fromOffset,
			String startsWith) {
		HashSet<ICompletionProposal> proposals = new HashSet<ICompletionProposal>();

		for (AConcreteMethPropdef cctMeth : methsProp) {
			String toReplace = cctMeth.getMethid().toString().trim();
			if (toReplace.toLowerCase().startsWith(startsWith.toLowerCase())) {
				proposals.add(new CompletionProposal(toReplace, fromOffset
						- startsWith.length(), startsWith.length(), toReplace
						.length()));
			}
		}

		for (ADeferredMethPropdef dfMeth : defMeths) {
			String toReplace = dfMeth.getMethid().toString().trim();
			if (toReplace.toLowerCase().startsWith(startsWith.toLowerCase())) {
				proposals.add(new CompletionProposal(toReplace, fromOffset
						- startsWith.length(), startsWith.length(), toReplace
						.length()));
			}
		}

		return proposals.toArray(new ICompletionProposal[proposals.size()]);
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