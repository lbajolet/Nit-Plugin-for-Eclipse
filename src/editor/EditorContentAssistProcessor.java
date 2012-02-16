package editor;

import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;

public class EditorContentAssistProcessor implements IContentAssistProcessor {

	EditorContentAssistProcessor() {
		this.wordProvider = new WordProvider();
	}

	private WordProvider wordProvider;
	private String lastError;

	@Override
	public ICompletionProposal[] computeCompletionProposals(
			ITextViewer textViewer, int documentOffset) {
		IDocument document = textViewer.getDocument();
		int currOffset = documentOffset - 1; // position du cusreur dans le texte
		String currWord = "";
		char currChar;

		boolean isMethod = false;
		
		// tant que l'on ne rencontre pas de séparateur (espace ou point
		// virgule), on construit le mot et on recule dans le flux
		try {
			currChar = document.getChar(currOffset);
			while (currOffset > 0 && !(Character.isWhitespace(currChar) || currChar == ';')) {
				System.out.println(currChar);
				
				if(currChar == '.') {
					isMethod = true;
					break;
				}
				
				currWord = currChar + currWord;
				currOffset--;
				currChar = document.getChar(currOffset);
			}
		} catch (BadLocationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		// Calcul de la liste de proposition
		ICompletionProposal[] proposals = null;
		List<String> suggestions;

		System.out.println(isMethod);
		if (isMethod) {
			suggestions = wordProvider.suggestMethods(currWord);
		} else {
			suggestions = wordProvider.suggestClasses(currWord);
		}

		if (suggestions.size() > 0) {
			try {
				proposals = buildProposals(suggestions, currWord,
						documentOffset - currWord.length());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return proposals;
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