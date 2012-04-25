package editor;

import java.util.ArrayList;
import java.util.HashSet;

import node.AConcreteMethPropdef;
import node.ADeferredMethPropdef;
import node.AStdClassdef;

import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;

/**
 * @author R4PaSs
 *
 * Provides the methods needed to compute the words to propose via AutoComplete for the NitEditor
 */
public class WordProvider {

	/**
	 * @param classes The definition of Classes as returned by the actual parser
	 * @param fromOffset Current Offset in the IDocument
	 * @param startsWith String being typed by the user, used to compute intelligent proposals
	 * @return An array of classes proposals
	 */
	public ICompletionProposal[] buildClassProposals(
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

	/**
	 * @param methsProp Definition of methods proposals (In classes)
	 * @param defMeths Definition of methods proposals (Top Level)
	 * @param fromOffset Current offset in IDocument
	 * @param startsWith String being typed by the user, used to compute intelligent proposals
	 * @return An array of methods proposals
	 */
	public ICompletionProposal[] buildMethProposals(
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

	/**
	 * @param fromOffset String being typed by the user, used to compute intelligent proposals
	 * @param startsWith Current offset in IDocument
	 * @return An array of Keywords
	 */
	public ICompletionProposal[] buildKeywordsProposals(int fromOffset,
			String startsWith) {

		ArrayList<ICompletionProposal> proposals = new ArrayList<ICompletionProposal>();
		String[] kwds = NitColorReposit.getInstance().getKeywords();

		for (String kw : kwds) {
			if (kw.toLowerCase().startsWith(startsWith.toLowerCase())) {
				proposals
						.add(new CompletionProposal(kw, fromOffset
								- startsWith.length(), startsWith.length(), kw
								.length()));
			}
		}

		return proposals.toArray(new ICompletionProposal[proposals.size()]);
	}
}
