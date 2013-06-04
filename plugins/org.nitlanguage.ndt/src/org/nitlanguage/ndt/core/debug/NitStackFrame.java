package org.nitlanguage.ndt.core.debug;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IRegisterGroup;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IThread;
import org.eclipse.debug.core.model.IVariable;

public class NitStackFrame implements IStackFrame {

	NitDebugTarget target;
	
	public NitStackFrame(NitDebugTarget debugTarget){
		this.target = debugTarget;
	}
	
	@Override
	public String getModelIdentifier() {
		return NitDebugConstants.MODEL_IDENTIFIER;
	}

	@Override
	public IDebugTarget getDebugTarget() {
		return this.target;
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
		return true;
	}

	@Override
	public boolean canStepOver() {
		return true;
	}

	@Override
	public boolean canStepReturn() {
		return true;
	}

	@Override
	public boolean isStepping() {
		return !this.target.isSuspended();
	}

	@Override
	public void stepInto() throws DebugException {
		//TODO
		//this.target.s
	}

	@Override
	public void stepOver() throws DebugException {
		// TODO Auto-generated method stub
	}

	@Override
	public void stepReturn() throws DebugException {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean canResume() {
		return true;
	}

	@Override
	public boolean canSuspend() {
		return true;
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
		return true;
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
		try {
			return this.target.getThreads()[0];
		} catch (DebugException e) {
			return null;
		}
	}

	@Override
	public IVariable[] getVariables() throws DebugException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasVariables() throws DebugException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getLineNumber() throws DebugException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getCharStart() throws DebugException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getCharEnd() throws DebugException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getName() throws DebugException {
		// TODO Auto-generated method stub
		return null;
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
