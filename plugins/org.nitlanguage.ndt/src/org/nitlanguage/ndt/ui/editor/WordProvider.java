package org.nitlanguage.ndt.ui.editor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.nitlanguage.gen.node.AConcreteMethPropdef;
import org.nitlanguage.gen.node.ADeferredMethPropdef;
import org.nitlanguage.gen.node.AStdClassdef;
import org.nitlanguage.gen.node.ATopClassdef;

/**
 * Provides the methods needed to compute the words to propose via
 * AutoComplete for the NitEditor
 * @author lucas.bajolet
 */
public class WordProvider {

	/**
	 * @param classes
	 *            The definition of Classes as returned by the actual parser
	 * @param fromOffset
	 *            Current Offset in the IDocument
	 * @param startsWith
	 *            String being typed by the user, used to compute intelligent
	 *            proposals
	 * @return An array of classes proposals
	 */
	public ICompletionProposal[] buildClassProposals(
			ArrayList<AStdClassdef> classes,
			ArrayList<ATopClassdef> topClasses, int fromOffset,
			String startsWith) {

		ICompletionProposal[] proposalsArrayList;
		HashSet<String> completionsHashSet = new HashSet<String>();

		for (AStdClassdef classDef : classes) {
			String toPropose = classDef.getId().getText().trim();
			if (toPropose.toLowerCase().startsWith(startsWith.toLowerCase()))
				completionsHashSet.add(toPropose);
		}

		for (ATopClassdef classDef : topClasses) {
			String[] toProposeExploded = classDef.toString().split(" ");
			boolean takeNext = false;
			String toPropose = null;
			for (int i = 0; i < toProposeExploded.length; i++) {
				if (takeNext) {
					toPropose = toProposeExploded[i].trim();
					break;
				}
				if (toProposeExploded[i].equals("fun")) {
					takeNext = true;
				}
			}
			if (toPropose != null
					&& toPropose.toLowerCase().startsWith(
							startsWith.toLowerCase())) {
				completionsHashSet.add(toPropose);
			}
		}

		String[] arrayOfClassesStr = new String[completionsHashSet.size()];

		completionsHashSet.toArray(arrayOfClassesStr);

		Arrays.sort(arrayOfClassesStr, String.CASE_INSENSITIVE_ORDER);

		proposalsArrayList = new ICompletionProposal[arrayOfClassesStr.length];

		int count = 0;

		for (String prop : arrayOfClassesStr) {
			if (prop != null) {
				proposalsArrayList[count] = new CompletionProposal(prop,
						fromOffset - startsWith.length(), startsWith.length(),
						prop.length());
				count++;
			}
		}

		return proposalsArrayList;
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
		ICompletionProposal proposals[];
		HashSet<String> stringProposals = new HashSet<String>();

		for (AConcreteMethPropdef cctMeth : methsProp) {
			String toReplace = cctMeth.getMethid().toString().trim();
			if (toReplace.toLowerCase().startsWith(startsWith.toLowerCase()))
				stringProposals.add(toReplace);
		}

		for (ADeferredMethPropdef dfMeth : defMeths) {
			String toReplace = dfMeth.getMethid().toString().trim();
			if (toReplace.toLowerCase().startsWith(startsWith.toLowerCase()))
				stringProposals.add(toReplace);
		}

		String[] arrayOfStrProps = new String[stringProposals.size()];

		stringProposals.toArray(arrayOfStrProps);

		Arrays.sort(arrayOfStrProps, String.CASE_INSENSITIVE_ORDER);

		proposals = new ICompletionProposal[arrayOfStrProps.length];

		int count = 0;

		for (String prop : arrayOfStrProps) {
			if (prop != null) {
				proposals[count] = new CompletionProposal(prop, fromOffset - startsWith.length(),
						startsWith.length(), prop.length());
				count++;
			}
		}

		return proposals;
	}

	/**
	 * @param fromOffset
	 *            String being typed by the user, used to compute intelligent
	 *            proposals
	 * @param startsWith
	 *            Current offset in IDocument
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
