package org.nitlanguage.ndt.core.debug.events;

public class NitStackFrameEvent implements NitDebugEvent{

	public NitStackFrameEvent(String module, String classe, String method, String path, int line) {
		this.nitModule = module;
		this.nitClass = classe;
		this.nitMethod = method;
		this.nitPath = path;
		this.nitLine = line;
	}
	
	private String nitModule;
	
	private String nitClass;
	
	private String nitMethod;
	
	private String nitPath;
	
	private int nitLine;
	
	public String getFrameModule(){
		return this.nitModule;
	}
	
	public String getFrameClass(){
		return this.nitClass;
	}
	
	public String getFrameMethod(){
		return this.nitMethod;
	}
	
	public String getFilePath(){
		return this.nitPath;
	}
	
	public int getStoppedLine(){
		return this.nitLine;
	}
}
