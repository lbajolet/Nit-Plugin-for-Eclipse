package org.nitlanguage.ndt.core.debug;

import java.util.Observable;
import java.util.Observer;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.core.model.IVariable;

public class NitComplexObject implements IValue, Observer {

	IVariable[] values;
	NitVariable parent;
	String valueString;

	boolean receivedVals = false;

	public NitComplexObject(String value) {
		this.valueString = value;
	}

	public void setParent(NitVariable parent) {
		this.parent = parent;
	}

	@Override
	public String getModelIdentifier() {
		return NitDebugConstants.MODEL_IDENTIFIER;
	}

	@Override
	public IDebugTarget getDebugTarget() {
		return parent.getDebugTarget();
	}

	@Override
	public ILaunch getLaunch() {
		return parent.getLaunch();
	}

	@Override
	public Object getAdapter(Class adapter) {
		return null;
	}

	@Override
	public String getReferenceTypeName() throws DebugException {
		return parent.getReferenceTypeName();
	}

	@Override
	public String getValueString() throws DebugException {
		return this.valueString;
	}

	@Override
	public boolean isAllocated() throws DebugException {
		return true;
	}

	@Override
	public IVariable[] getVariables() throws DebugException {
		if (this.values == null) {
			NitDebugTarget nitTarget = ((NitDebugTarget) this.getDebugTarget());
			nitTarget.getVariableWithName(parent.getName());
			nitTarget.addObserver(this);
			try {
				// Has to be blocking until the debugger sends the composition
				// of the variable
				// Eclipse considers a passive wait illegal.
				// We'll wait actively then. Reactivity principles, energy
				// saving... => Nah, don't care
				while (!receivedVals) {
					Thread.sleep(100);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return this.values;
	}

	@Override
	public boolean hasVariables() throws DebugException {
		return true;
	}

	@Override
	public void update(Observable o, Object arg) {
		if (arg != null && arg instanceof NitVariable[]) {
			this.values = (NitVariable[]) arg;
		}
		this.receivedVals = true;
		((NitDebugTarget) this.getDebugTarget()).deleteObserver(this);
	}
}
