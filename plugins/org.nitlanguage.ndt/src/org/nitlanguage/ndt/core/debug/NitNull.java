package org.nitlanguage.ndt.core.debug;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.core.model.IVariable;

public class NitNull implements IValue {

	NitVariable parent;

	public NitNull(NitVariable var) {
		this.parent = var;
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getReferenceTypeName() throws DebugException {
		return "null";
	}

	@Override
	public String getValueString() throws DebugException {
		return "null";
	}

	@Override
	public boolean isAllocated() throws DebugException {
		return false;
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
