package org.nitlanguage.ndt.core.debug;

public class NitDebugConstants {

	// Debug messages
	public static final String CONTINUE_MSG = "c";
	public static final String SUSPEND_MSG = "stop";
	public static final String PLACE_BREAKPOINT_MSG = "b ";
	public static final String REMOVE_BREAKPOINT_MSG = "r ";
	public static final String STEP_OVER_MSG = "n";
	public static final String STEP_OUT_MSG = "finish";
	public static final String STEP_IN_MSG = "s";
	public static final String PRINT_STACK_TRACE_MSG = "p stack";
	public static final String PRINT_ALL_MSG = "p *";
	public static final String PRINT_VAR_MSG = "print ";
	public static final String KILL_PROCESS = "kill";

	// Configuration
	public static final String MODEL_IDENTIFIER = "org.nitlanguage.ndt";
	public static final String DEBUGGING_OVER_RECV_MSG = "DBG DONE WORK ON SELF";
	public static final String DEBUGGING_OVER_ACK_MSG = "CLIENT DBG DONE ACK";
	public static final String BREAKPOINT_MARKER_TYPE = "org.nitlanguage.ndt.debug.markerType.lineBreakpoint";
}
