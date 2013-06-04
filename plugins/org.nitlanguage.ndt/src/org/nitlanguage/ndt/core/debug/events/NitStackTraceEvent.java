package org.nitlanguage.ndt.core.debug.events;

import java.util.ArrayList;

public class NitStackTraceEvent implements NitDebugEvent {

	ArrayList<NitStackFrameEvent> stackTrace;

	public void addFrame(NitStackFrameEvent stackFrame) {
		stackTrace.add(stackFrame);
	}

	public NitStackFrameEvent[] getStackTrace(){
		return this.stackTrace.toArray(new NitStackFrameEvent[this.stackTrace.size()]);
	}

}
