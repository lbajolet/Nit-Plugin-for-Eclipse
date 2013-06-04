package org.nitlanguage.ndt.core.debug;

import java.io.IOException;

public class NitDebuggerWriter {

	private NitDebuggerConnector socket;

	public NitDebuggerWriter(NitDebuggerConnector sc) {
		this.socket = sc;
	}

	public void continue_exec() throws IOException {
		this.socket.sendMessage(NitDebugConstants.CONTINUE_MSG);
	}

	public void suspend_exec() throws IOException {
		this.socket.sendMessage(NitDebugConstants.SUSPEND_MSG);
	}

	public void place_breakpoint(String file, int line) throws IOException {
		this.socket.sendMessage(NitDebugConstants.PLACE_BREAKPOINT_MSG + file + " " + line);
	}
	
	public void abort() throws IOException{
		this.socket.sendMessage(NitDebugConstants.KILL_PROCESS);
	}
	
	public void printStack() throws IOException {
		this.socket.sendMessage(NitDebugConstants.PRINT_STACK_TRACE_MSG);
	}
}
