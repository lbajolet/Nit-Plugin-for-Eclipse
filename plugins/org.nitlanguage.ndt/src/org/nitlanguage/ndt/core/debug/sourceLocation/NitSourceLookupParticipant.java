package org.nitlanguage.ndt.core.debug.sourceLocation;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.sourcelookup.AbstractSourceLookupParticipant;
import org.nitlanguage.ndt.core.debug.NitStackFrame;

public class NitSourceLookupParticipant extends AbstractSourceLookupParticipant {

	@Override
	public String getSourceName(Object object) throws CoreException {
		if (object instanceof NitStackFrame) {
			return ((NitStackFrame) object).getFilePath();
		}
		return null;
	}

}
