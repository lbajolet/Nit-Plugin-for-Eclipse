package org.nitlanguage.ndt.core.debug;

import java.util.Observable;
import java.util.Observer;

import org.nitlanguage.ndt.core.debug.events.NitDebugEvent;
import org.nitlanguage.ndt.core.debug.events.NitStackFrameEvent;
import org.nitlanguage.ndt.core.debug.events.NitStackTraceEvent;
import org.nitlanguage.ndt.core.debug.events.NitSuspendedEvent;

public class NitMessageInterpreter extends Observable implements Observer {

	boolean readStack = false;

	NitStackTraceEvent currentStackTrace;

	public void observe(Observable debugReader) {
		debugReader.addObserver(this);
	}

	public void interpret(String message) {
		// Check if the message is a suspend message
		NitDebugEvent event;
		if(readStack){
			NitStackFrameEvent currFrame = parseStackTraceElement(message);
			if(currFrame == null && !readStack) notifyObservers(currentStackTrace);
		}
		if ((event = reconStoppedMessage(message)) != null) {
			this.suspended((NitSuspendedEvent) event);
		} else if (reconStackFrameStart(message)){
			readStack = true;
		}
	}

	public NitSuspendedEvent reconStoppedMessage(String message) {
		String[] commandParts = message.split(":");
		if (commandParts.length != 4)
			return null;
		// Parsing the second command part for the line number
		int i = 0;
		int linenumber = 0;
		while (commandParts[1].charAt(i) >= '0'
				&& commandParts[1].charAt(i) <= '9') {
			i++;
		}
		if(i > 0 && commandParts[1].charAt(i) == ','){
			String lineStr = commandParts[1].substring(0,i);
			linenumber = Integer.parseInt(lineStr);
		}else{
			return null;
		}
		return new NitSuspendedEvent(linenumber, commandParts[0].trim());
	}

	public boolean reconStackFrameStart(String message) {
		if(message.trim().equals(",---- Stack trace -- - -  -")) return true;
		return false;
	}

	public NitStackFrameEvent parseStackTraceElement(String message) {
		if(reconStackFrameEnd(message)){
			this.readStack = false;
			return null;
		}
		String realMessage = message.substring(2, message.length()-1).trim();
		String[] partsOfCommand = realMessage.split(" ");
		if(partsOfCommand.length != 2) return null;
		// Parse module/class/method
		String[] tupleModClassMethod = partsOfCommand[0].split("#");
		String nitModule = tupleModClassMethod[0];
		String nitClass = tupleModClassMethod[1];
		String nitMethod = tupleModClassMethod[2];
		// Parse file path and line number
		String pathAndLinePart = partsOfCommand[1].substring(1,partsOfCommand[1].length()-2);
		String[] pathLineParts = pathAndLinePart.split(":");
		return new NitStackFrameEvent(nitModule, nitClass, nitMethod, "", 0);
	}

	public boolean reconStackFrameEnd(String message){
		if(message.trim().equals("`------------------- - -  -")) return true;
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
