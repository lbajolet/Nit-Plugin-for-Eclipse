package org.nitlanguage.ndt.core.debug;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.core.model.IVariable;

public class NitInt implements IValue {

	Integer value;
	NitVariable parentVariable;

	public NitInt(Integer value) {
		this.value = value;
	}

	public void setParent(NitVariable var) {
		this.parentVariable = var;
	}

	@Override
	public String getModelIdentifier() {
		return NitDebugConstants.MODEL_IDENTIFIER;
	}

	@Override
	public IDebugTarget getDebugTarget() {
		return parentVariable.getDebugTarget();
	}

	@Override
	public ILaunch getLaunch() {
		return parentVariable.getLaunch();
	}

	@Override
	public Object getAdapter(Class adapter) {
		return null;
	}

	@Override
	public String getReferenceTypeName() throws DebugException {
		return "Int";
	}

	@Override
	public String getValueString() throws DebugException {
		return value.toString();
	}

	@Override
	public boolean isAllocated() throws DebugException {
		return true;
	}

	@Override
	public IVariable[] getVariables() throws DebugException {
		return null;
	}

	@Override
	public boolean hasVariables() throws DebugException {
		return false;
	}

}
