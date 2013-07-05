package org.nitlanguage.ndt.core.debug;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.core.model.IVariable;

public class NitVariable implements IVariable {

	private String name;
	private String type;
	private IValue value;

	private NitStackFrame target;

	public NitVariable(String name, String type, NitStackFrame parent) {
		this.target = parent;
		this.name = name;
		this.type = type;
	}

	@Override
	public String getModelIdentifier() {
		return NitDebugConstants.MODEL_IDENTIFIER;
	}

	@Override
	public IDebugTarget getDebugTarget() {
		return target.getDebugTarget();
	}

	@Override
	public ILaunch getLaunch() {
		return target.getLaunch();
	}

	@Override
	public Object getAdapter(Class adapter) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getReferenceTypeName() throws DebugException {
		return type;
	}

	@Override
	public void setValue(String expression) throws DebugException {
	}

	@Override
	public void setValue(IValue value) throws DebugException {
		this.value = value;
	}

	@Override
	public boolean supportsValueModification() {
		return false;
	}

	@Override
	public boolean verifyValue(String expression) throws DebugException {
		return false;
	}

	@Override
	public boolean verifyValue(IValue value) throws DebugException {
		return false;
	}

	@Override
	public IValue getValue() throws DebugException {
		return this.value;
	}

	@Override
	public String getName() throws DebugException {
		return this.name;
	}

	@Override
	public boolean hasValueChanged() throws DebugException {
		return false;
	}

}
