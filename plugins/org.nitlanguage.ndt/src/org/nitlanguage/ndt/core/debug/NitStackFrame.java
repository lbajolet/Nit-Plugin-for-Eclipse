package org.nitlanguage.ndt.core.debug;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IRegisterGroup;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IThread;
import org.eclipse.debug.core.model.IVariable;
import org.nitlanguage.ndt.core.debug.events.NitDebugEvent;

public class NitStackFrame implements IStackFrame, NitDebugEvent {

	NitThread target;

	private String nitModule;
	private String nitClass;
	private String nitMethod;
	private String nitPath;
	private int nitLine;

	// First stack trace available only
	private NitVariable[] vars;

	public NitStackFrame(NitThread debugTarget, String module, String classe,
			String method, String path, int line) {
		this.nitModule = module;
		this.nitClass = classe;
		this.nitMethod = method;
		this.nitPath = path;
		this.nitLine = line;
		this.target = debugTarget;
	}

	public String getFrameModule() {
		return this.nitModule;
	}

	public String getFrameClass() {
		return this.nitClass;
	}

	public String getFrameMethod() {
		return this.nitMethod;
	}

	public String getFilePath() {
		return this.nitPath;
	}

	public int getStoppedLine() {
		return this.nitLine;
	}

	@Override
	public String getModelIdentifier() {
		return NitDebugConstants.MODEL_IDENTIFIER;
	}

	@Override
	public IDebugTarget getDebugTarget() {
		return this.target.getDebugTarget();
	}

	@Override
	public ILaunch getLaunch() {
		return this.target.getLaunch();
	}

	@Override
	public Object getAdapter(Class adapter) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean canStepInto() {
		return this.target.canStepInto();
	}

	@Override
	public boolean canStepOver() {
		return this.target.canStepOver();
	}

	@Override
	public boolean canStepReturn() {
		return this.target.canStepReturn();
	}

	@Override
	public boolean isStepping() {
		return this.target.isStepping();
	}

	@Override
	public void stepInto() throws DebugException {
		this.target.stepInto();
	}

	@Override
	public void stepOver() throws DebugException {
		this.target.stepOver();
	}

	@Override
	public void stepReturn() throws DebugException {
		this.target.stepReturn();
	}

	@Override
	public boolean canResume() {
		return this.target.canResume();
	}

	@Override
	public boolean canSuspend() {
		return this.target.canSuspend();
	}

	@Override
	public boolean isSuspended() {
		return this.target.isSuspended();
	}

	@Override
	public void resume() throws DebugException {
		this.target.resume();
	}

	@Override
	public void suspend() throws DebugException {
		this.target.suspend();
	}

	@Override
	public boolean canTerminate() {
		return this.target.canTerminate();
	}

	@Override
	public boolean isTerminated() {
		return this.target.isTerminated();
	}

	@Override
	public void terminate() throws DebugException {
		this.target.terminate();
	}

	@Override
	public IThread getThread() {
		return this.target;
	}

	public void setNitVars(NitVariable[] vars) {
		this.vars = vars;
	}

	@Override
	public IVariable[] getVariables() throws DebugException {
		// Since the debugger can only get variables from the upper stack
		// We redirect the call to the first stack to get its variables
		if (this.target.getStackFrames()[0] == this) {
			return this.vars;
		} else {
			return target.getStackFrames()[0].getVariables();
		}
	}

	@Override
	public boolean hasVariables() throws DebugException {
		// TODO
		if (this.target.isSuspended()) {
			if (target.getStackFrames()[0].getVariables().length != 0)
				return true;
			return false;
		}
		return false;
	}

	@Override
	public int getLineNumber() throws DebugException {
		return this.nitLine;
	}

	@Override
	public int getCharStart() throws DebugException {
		return -1;
	}

	@Override
	public int getCharEnd() throws DebugException {
		return -1;
	}

	@Override
	public String getName() throws DebugException {
		return this.nitModule + " - " + this.nitClass;
	}

	@Override
	public IRegisterGroup[] getRegisterGroups() throws DebugException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasRegisterGroups() throws DebugException {
		// TODO Auto-generated method stub
		return false;
	}

}
