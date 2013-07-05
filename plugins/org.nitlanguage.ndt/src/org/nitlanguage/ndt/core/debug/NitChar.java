package org.nitlanguage.ndt.core.debug;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.core.model.IVariable;

public class NitChar implements IValue {

	Character value;
	NitVariable parent;

	public NitChar(Character val) {
		this.value = val;
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
		return "Char";
	}

	@Override
	public String getValueString() throws DebugException {
		return this.value.toString();
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
