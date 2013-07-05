package org.nitlanguage.ndt.core.debug.network;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import org.eclipse.debug.core.DebugException;
import org.nitlanguage.ndt.core.debug.NitBool;
import org.nitlanguage.ndt.core.debug.NitChar;
import org.nitlanguage.ndt.core.debug.NitComplexObject;
import org.nitlanguage.ndt.core.debug.NitDebugConstants;
import org.nitlanguage.ndt.core.debug.NitDebugTarget;
import org.nitlanguage.ndt.core.debug.NitFloat;
import org.nitlanguage.ndt.core.debug.NitInt;
import org.nitlanguage.ndt.core.debug.NitNull;
import org.nitlanguage.ndt.core.debug.NitStackFrame;
import org.nitlanguage.ndt.core.debug.NitThread;
import org.nitlanguage.ndt.core.debug.NitVariable;
import org.nitlanguage.ndt.core.debug.events.DebugOverEvent;
import org.nitlanguage.ndt.core.debug.events.NitDebugEvent;
import org.nitlanguage.ndt.core.debug.events.NitSuspendedEvent;

public class NitMessageInterpreter extends Observable implements Observer {

	NitDebugTarget target;

	NitThread currentThread;

	boolean readStack = false;

	ArrayList<NitStackFrame> currentStackTrace;

	ArrayList<NitVariable> variableCollection;

	boolean readVars = false;

	boolean readInnerVar = false;

	String innerAttribute;

	public NitMessageInterpreter(NitDebugTarget target) {
		this.target = target;
		try {
			this.currentThread = (NitThread) target.getThreads()[0];
		} catch (DebugException e) {
			e.printStackTrace();
		}
	}

	public void observe(Observable debugReader) {
		debugReader.addObserver(this);
	}

	public void interpret(String message) {
		NitDebugEvent event;

		if (message.equals(NitDebugConstants.DEBUGGING_OVER_RECV_MSG)) {
			this.setChanged();
			this.notifyObservers(new DebugOverEvent());
			return;
		}

		if (readStack) {
			NitStackFrame currFrame = parseStackTraceElement(message);
			if (currFrame == null && !readStack) {
				setChanged();
				notifyObservers(currentStackTrace
						.toArray(new NitStackFrame[currentStackTrace.size()]));
				return;
			} else {
				currentStackTrace.add(currFrame);
				return;
			}
		}

		if (readVars) {
			if (reconVariableBlockEnd(message)) {
				readVars = false;
				setChanged();
				notifyObservers(this.variableCollection
						.toArray(new NitVariable[this.variableCollection.size()]));
				return;
			} else {
				NitVariable var = null;
				try {
					var = parseVarPrintElement(message);
				} catch (DebugException e) {
					e.printStackTrace();
				}
				if (var != null) {
					this.variableCollection.add(var);
				}
				return;
			}
		}

		if (readInnerVar) {
			if (reconVarInnardsBlockEnd(message)) {
				readInnerVar = false;
				setChanged();
				notifyObservers(this.variableCollection
						.toArray(new NitVariable[this.variableCollection.size()]));
				return;
			}
			NitVariable var = null;
			try {
				var = parseInnerVar(message);
			} catch (DebugException e) {
				e.printStackTrace();
			}
			if (var != null) {
				this.variableCollection.add(var);
			}
			return;
		}

		if ((event = reconStoppedMessage(message)) != null) {
			this.suspended((NitSuspendedEvent) event);
		} else if (reconStackFrameStart(message)) {
			readStack = true;
			this.currentStackTrace = new ArrayList<NitStackFrame>();
		} else if (reconVariableBlockStart(message)) {
			readVars = true;
			this.variableCollection = new ArrayList<NitVariable>();
		} else if (reconVarInnardsBlockStart(message)) {
			readInnerVar = true;
			this.variableCollection = new ArrayList<NitVariable>();
		}
	}

	public boolean reconVarInnardsBlockStart(String message) {
		if (message.equals("Printing innards of a variable")) {
			return true;
		}
		return false;
	}

	public NitVariable parseInnerVar(String message) throws DebugException {
		String[] innerMsgParts = message.split(":");
		if (innerMsgParts.length < 2)
			return null;
		if (innerMsgParts[0].trim().equals("Object")) {
			return null;
		} else if (innerMsgParts[0].trim().equals("Attribute")) {
			this.innerAttribute = rm_andAtfromVarName(innerMsgParts[1].trim());
			return null;
		} else if (innerMsgParts[0].trim().equals("Valeur")) {
			return getNitVarFromMsg(innerMsgParts[1].trim(),
					this.innerAttribute);
		}
		return null;
	}

	// Removes occurrences of _ or @ character at the beginning of a variable
	// name
	private String rm_andAtfromVarName(String varName) {
		if (varName.charAt(0) == '_' || varName.charAt(0) == '@') {
			varName = varName.substring(1);
			return rm_andAtfromVarName(varName);
		}
		return varName;
	}

	public boolean reconVarInnardsBlockEnd(String message) {
		if (message.equals("Stopping printing innards of a variable")) {
			return true;
		}
		return false;
	}

	public boolean reconVariableBlockStart(String message) {
		if (message.equals("Variables collection : "))
			return true;
		return false;
	}

	public NitVariable parseVarPrintElement(String message)
			throws DebugException {
		if (message.trim().equals(""))
			return null;
		String name;
		int i = message.indexOf(" ");
		if (i == -1) {
			return null;
		}
		if (!message.substring(0, i).equals("Variable")) {
			return null;
		}
		i++;
		StringBuffer nameBuf = new StringBuffer();
		while (message.charAt(i) != ',') {
			nameBuf.append(message.charAt(i));
			i++;
		}
		name = nameBuf.toString();
		if (!message.substring(i, i + 11).equals(", Instance ")) {
			return null;
		}
		String typeVal = message.substring(i + 11);
		return getNitVarFromMsg(typeVal, name);
	}

	public NitVariable getNitVarFromMsg(String message, String name)
			throws DebugException {
		String type;
		String value;
		if (message.contains("#")) {
			type = message.split("#")[0];
			int indexFrom = message.indexOf("(");
			int indexTo = message.lastIndexOf(")");
			if (indexTo == -1) {
				value = message.substring(indexFrom + 1);
			} else {
				value = message.substring(indexFrom + 1, indexTo);
			}
			NitVariable nitVar = new NitVariable(name, type,
					this.currentStackTrace.get(0));
			if (type.equals("Int")) {
				NitInt var = new NitInt(Integer.parseInt(value));
				var.setParent(nitVar);
				nitVar.setValue(var);
			} else if (type.equals("Float")) {
				NitFloat var = new NitFloat(Float.parseFloat(value));
				var.setParent(nitVar);
				nitVar.setValue(var);
			} else if (type.equals("Bool")) {
				NitBool var = new NitBool(Boolean.parseBoolean(value));
				var.setParent(nitVar);
				nitVar.setValue(var);
			} else if (type.equals("Char")) {
				NitChar var = new NitChar(value.charAt(0));
				var.setParent(nitVar);
				nitVar.setValue(var);
			} else {
				NitComplexObject var = new NitComplexObject(value);
				var.setParent(nitVar);
				nitVar.setValue(var);
			}
			return nitVar;
		} else {
			type = message;
			if (type.equals("null")) {
				NitVariable nitVar = new NitVariable(name, type,
						this.currentStackTrace.get(0));
				NitNull var = new NitNull(nitVar);
				nitVar.setValue(var);
				return nitVar;
			}
			NitVariable nitVar = new NitVariable(name, type,
					this.currentStackTrace.get(0));
			NitComplexObject var = new NitComplexObject("Instance " + type);
			var.setParent(nitVar);
			nitVar.setValue(var);
			return nitVar;
		}
	}

	public boolean reconVariableBlockEnd(String message) {
		if (message.equals("End of current instruction "))
			return true;
		return false;
	}

	public NitSuspendedEvent reconStoppedMessage(String message) {
		String[] commandParts = message.split(":");
		if (commandParts.length < 2)
			return null;
		// Parsing the second command part for the line number
		int i = 0;
		int linenumber = 0;
		while (commandParts[1].charAt(i) >= '0'
				&& commandParts[1].charAt(i) <= '9') {
			i++;
		}
		if (i > 0 && commandParts[1].charAt(i) == ',') {
			String lineStr = commandParts[1].substring(0, i);
			linenumber = Integer.parseInt(lineStr);
		} else {
			return null;
		}
		return new NitSuspendedEvent(linenumber, commandParts[0].trim());
	}

	public boolean reconStackFrameStart(String message) {
		if (message.trim().equals(",---- Stack trace -- - -  -"))
			return true;
		return false;
	}

	public NitStackFrame parseStackTraceElement(String message) {
		if (reconStackFrameEnd(message)) {
			this.readStack = false;
			return null;
		}
		String realMessage = message.substring(2, message.length() - 1).trim();
		String[] partsOfCommand = realMessage.split(" ");
		if (partsOfCommand.length != 2)
			return null;
		// Parse module/class/method
		String[] tupleModClassMethod = partsOfCommand[0].split("#");
		String nitModule = tupleModClassMethod[0];
		String nitClass = tupleModClassMethod[1];
		String nitMethod = tupleModClassMethod[2];
		// Parse file path and line number
		String pathAndLinePart = partsOfCommand[1].substring(1,
				partsOfCommand[1].length() - 2);
		String[] pathLineParts = pathAndLinePart.split(":");
		String path = pathLineParts[0];
		int line = Integer.parseInt(pathLineParts[1].split(",")[0]);
		return new NitStackFrame(currentThread, nitModule, nitClass, nitMethod,
				path, line);
	}

	public boolean reconStackFrameEnd(String message) {
		if (message.trim().equals("`------------------- - -  -"))
			return true;
		return false;
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		if (arg1 instanceof String) {
			interpret(arg1.toString());
		}
	}

	public void suspended(NitSuspendedEvent event) {
		this.setChanged();
		this.notifyObservers(event);
	}

}
