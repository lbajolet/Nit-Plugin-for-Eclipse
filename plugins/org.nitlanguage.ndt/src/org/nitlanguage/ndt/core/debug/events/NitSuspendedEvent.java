package org.nitlanguage.ndt.core.debug.events;

public class NitSuspendedEvent implements NitDebugEvent {

	private int line;
	private String filePath;

	public NitSuspendedEvent(int line, String path) {
		this.line = line;
		this.filePath = path;
	}

	public int getLine() {
		return this.line;
	}

	public String getFilePath() {
		return this.filePath;
	}

}
