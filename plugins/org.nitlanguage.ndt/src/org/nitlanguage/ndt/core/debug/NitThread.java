package org.nitlanguage.ndt.core.debug;

import java.io.IOException;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IThread;

public class NitThread implements IThread {

	NitDebugTarget targetAssociated;

	public NitThread(NitDebugTarget target) {
		this.targetAssociated = target;
	}

	@Override
	public String getModelIdentifier() {
		return NitDebugConstants.MODEL_IDENTIFIER;
	}

	@Override
	public IDebugTarget getDebugTarget() {
		return targetAssociated;
	}

	@Override
	public ILaunch getLaunch() {
		return targetAssociated.getLaunch();
	}

	@Override
	public Object getAdapter(Class adapter) {
		return null;
	}

	@Override
	public boolean canResume() {
		return this.targetAssociated.canResume();
	}

	@Override
	public boolean canSuspend() {
		return this.targetAssociated.canSuspend();
	}

	@Override
	public boolean isSuspended() {
		return targetAssociated.isSuspended();
	}

	@Override
	public void resume() throws DebugException {
		targetAssociated.resume();
	}

	@Override
	public void suspend() throws DebugException {
		targetAssociated.suspend();
	}

	@Override
	public boolean canStepInto() {
		return targetAssociated.isSuspended();
	}

	@Override
	public boolean canStepOver() {
		return targetAssociated.isSuspended();
	}

	@Override
	public boolean canStepReturn() {
		return targetAssociated.isSuspended();
	}

	@Override
	public boolean isStepping() {
		return !this.targetAssociated.isSuspended();
	}

	@Override
	public void stepInto() throws DebugException {
		try {
			this.targetAssociated.stepInto();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void stepOver() throws DebugException {
		try {
			this.targetAssociated.stepOver();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void stepReturn() throws DebugException {
		try {
			this.targetAssociated.stepOut();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean canTerminate() {
		return this.targetAssociated.canTerminate();
	}

	@Override
	public boolean isTerminated() {
		return this.targetAssociated.isTerminated();
	}

	@Override
	public void terminate() throws DebugException {
		this.targetAssociated.terminate();
	}

	@Override
	public IStackFrame[] getStackFrames() throws DebugException {
		return this.targetAssociated.getStackTrace();
	}

	@Override
	public boolean hasStackFrames() throws DebugException {
		return this.isSuspended();
	}

	@Override
	public int getPriority() throws DebugException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public IStackFrame getTopStackFrame() throws DebugException {
		return this.targetAssociated.getStackTrace()[0];
	}

	@Override
	public String getName() throws DebugException {
		return "Main Thread";
	}

	@Override
	public IBreakpoint[] getBreakpoints() {
		// TODO Auto-generated method stub
		return null;
	}

}
