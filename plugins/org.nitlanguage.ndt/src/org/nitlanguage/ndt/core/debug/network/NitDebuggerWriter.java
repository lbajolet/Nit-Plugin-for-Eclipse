package org.nitlanguage.ndt.core.debug.network;

import java.io.IOException;

import org.nitlanguage.ndt.core.debug.NitDebugConstants;

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
		this.socket.sendMessage(NitDebugConstants.PLACE_BREAKPOINT_MSG + file
				+ " " + line);
	}

	public void remove_breakpoint(String file, int line) throws IOException {
		this.socket.sendMessage(NitDebugConstants.REMOVE_BREAKPOINT_MSG + file
				+ " " + line);
	}

	public void abort() throws IOException {
		this.socket.sendMessage(NitDebugConstants.KILL_PROCESS);
	}

	public void printStack() throws IOException {
		this.socket.sendMessage(NitDebugConstants.PRINT_STACK_TRACE_MSG);
	}

	public void printVariables() throws IOException {
		this.socket.sendMessage(NitDebugConstants.PRINT_ALL_MSG);
	}

	public void getVariable(String name) throws IOException {
		this.socket.sendMessage(NitDebugConstants.PRINT_VAR_MSG + name);
	}

	public void stepInto() throws IOException {
		this.socket.sendMessage(NitDebugConstants.STEP_IN_MSG);
	}

	public void stepOver() throws IOException {
		this.socket.sendMessage(NitDebugConstants.STEP_OVER_MSG);
	}

	public void stepOut() throws IOException {
		this.socket.sendMessage(NitDebugConstants.STEP_OUT_MSG);
	}

	public void debugOverAck() throws IOException {
		this.socket.sendMessage(NitDebugConstants.DEBUGGING_OVER_ACK_MSG);
	}
}
