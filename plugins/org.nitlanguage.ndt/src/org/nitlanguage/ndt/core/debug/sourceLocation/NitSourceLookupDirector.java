package org.nitlanguage.ndt.core.debug.sourceLocation;

import org.eclipse.debug.core.sourcelookup.AbstractSourceLookupDirector;
import org.eclipse.debug.core.sourcelookup.ISourceLookupParticipant;

public class NitSourceLookupDirector extends AbstractSourceLookupDirector {

	@Override
	public void initializeParticipants() {
		addParticipants(new ISourceLookupParticipant[] { new NitSourceLookupParticipant() });
	}
}
