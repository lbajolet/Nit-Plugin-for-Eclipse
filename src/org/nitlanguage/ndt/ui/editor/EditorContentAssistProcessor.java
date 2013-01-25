package org.nitlanguage.ndt.ui.editor;

import java.util.ArrayList;
import java.util.LinkedList;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.nitlanguage.gen.node.AConcreteMethPropdef;
import org.nitlanguage.gen.node.ADeferredMethPropdef;
import org.nitlanguage.gen.node.AModule;
import org.nitlanguage.gen.node.AStdClassdef;
import org.nitlanguage.gen.node.AStdImport;
import org.nitlanguage.gen.node.ATopClassdef;
import org.nitlanguage.gen.node.PPropdef;
import org.nitlanguage.gen.node.Start;
import org.nitlanguage.ndt.core.asthelpers.AstParserHelper;
/**
 * @author R4PaSs
 * 
 *         Class charged to provide auto completion for the NitEditor
 */
public class EditorContentAssistProcessor implements IContentAssistProcessor {

	private String lastError;

	@Override
	public ICompletionProposal[] computeCompletionProposals(
			ITextViewer textViewer, int documentOffset) {

		// Result sets
		ArrayList<AStdClassdef> stdClassesToPropose = new ArrayList<AStdClassdef>();
		ArrayList<ATopClassdef> topClassesToPropose = new ArrayList<ATopClassdef>();
		ArrayList<AConcreteMethPropdef> concMethsToPropose = new ArrayList<AConcreteMethPropdef>();
		ArrayList<ADeferredMethPropdef> defMethsToPropose = new ArrayList<ADeferredMethPropdef>();

		String startsWith = "";
		IDocument document = textViewer.getDocument();

		StringBuffer startBuf = new StringBuffer();
		StringBuffer endBuf = new StringBuffer();

		WordProvider wp = new WordProvider();

		try {
			char tempChar = '\0';
			int currOffset = 0;
			if (documentOffset > 0)
				currOffset = documentOffset - 1;
			else
				currOffset = 0;

			// Bufferize from currOffset to start delimiter
			while (tempChar != '.' && tempChar != ' ' && tempChar != '\n'
					&& tempChar != '\t' && tempChar != '\r' && currOffset >= 0) {
				tempChar = document.getChar(currOffset);
				startBuf.append(tempChar);
				currOffset--;
			}

			// Bufferize from currOffset to end delimiter
			currOffset = documentOffset;
			tempChar = '\0';
			while (tempChar != '.' && tempChar != ' ' && tempChar != '\n'
					&& tempChar != '\t' && tempChar != '\r'
					&& currOffset < document.getLength()) {
				tempChar = document.getChar(currOffset);
				endBuf.append(tempChar);
				currOffset++;
			}

			String startStr = "";
			if (startBuf.length() > 0)
				startStr = startBuf.reverse().toString().trim();

			String endStr = "";
			if (endBuf.length() > 0)
				endStr = endBuf.toString().trim();

			startsWith = startStr + endStr;
		} catch (Exception e) {
		}

		AstParserHelper aph = new AstParserHelper();

		Start st = null;

		st = aph.getAstForDocument(document);

		if (st != null) {

			AModule mod = aph.getModuleOfAST(st);

			if (mod != null) {
				ArrayList<AStdClassdef> astdclass = aph
						.getAStdClassesOfModule(mod);
				stdClassesToPropose.addAll(astdclass);
				topClassesToPropose.addAll(aph.getATopClassesOfModule(mod));
				for (AStdClassdef claas : astdclass) {
					LinkedList<PPropdef> props = aph.getPropsOfClass(claas);
					concMethsToPropose.addAll(aph
							.getConcreteMethsInPropList(props));
					defMethsToPropose.addAll(aph
							.getDeferredMethsInPropList(props));
				}
				// Resolve imports and add their info to the candidates for
				// autocompletion
				ArrayList<AStdImport> imps = aph.getImports(mod);
				// Get IFile of current document
				IFile from = null;
				if (PlatformUI.getWorkbench().getActiveWorkbenchWindow()
						.getActivePage().getActiveEditor().getEditorInput() instanceof FileEditorInput)
					from = ((FileEditorInput) PlatformUI.getWorkbench()
							.getActiveWorkbenchWindow().getActivePage()
							.getActiveEditor().getEditorInput()).getFile();
				if (from != null)
					for (AStdImport imp : imps) {
						Start importStartNode = aph
								.getAstForDocument(imp, from);
						if (importStartNode != null) {
							AModule modImp = aph
									.getModuleOfAST(importStartNode);
							if (modImp != null) {
								ArrayList<AStdClassdef> astimpclass = aph
										.getAStdClassesOfModule(modImp);
								stdClassesToPropose.addAll(astimpclass);
								topClassesToPropose.addAll(aph
										.getATopClassesOfModule(modImp));
								for (AStdClassdef claas : astimpclass) {
									LinkedList<PPropdef> props = aph
											.getPropsOfClass(claas);
									concMethsToPropose.addAll(aph
											.getConcreteMethsInPropList(props));
									defMethsToPropose.addAll(aph
											.getDeferredMethsInPropList(props));
								}
							}
						}
					}
			}
		}

		ICompletionProposal[] methodsToPropose = wp.buildMethProposals(
				concMethsToPropose, defMethsToPropose, documentOffset,
				startsWith);

		ICompletionProposal[] classesSuggestions = wp.buildClassProposals(
				stdClassesToPropose, topClassesToPropose, documentOffset,
				startsWith);

		int totalLength = classesSuggestions.length + methodsToPropose.length;

		ICompletionProposal[] finalProposals = new ICompletionProposal[totalLength];

		int currLength = 0;

		if (classesSuggestions.length > 0) {
			System.arraycopy(classesSuggestions, 0, finalProposals, currLength,
					classesSuggestions.length);
			currLength += classesSuggestions.length;
		}

		if (methodsToPropose.length > 0) {
			System.arraycopy(methodsToPropose, 0, finalProposals, currLength,
					methodsToPropose.length);
		}

		return finalProposals;
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