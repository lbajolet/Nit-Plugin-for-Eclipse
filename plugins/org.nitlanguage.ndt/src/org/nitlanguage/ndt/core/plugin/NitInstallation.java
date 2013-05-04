package org.nitlanguage.ndt.core.plugin;

import java.io.File;
import java.nio.file.Paths;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.IPreferenceStore;
import org.osgi.service.prefs.BackingStoreException;

import com.sun.xml.internal.ws.util.StringUtils;

public class NitInstallation {
	// The shared nit installation instance
	private static NitInstallation installation;
	
	// Root path string
	public static final String ROOT_PATH_ID = "rootLocation";	
	// Compiler path string
	public static final String COMPILER_PATH_ID = "compilerLocation";	
	// Interpreter path string
	public static final String INTERPRETER_PATHS_ID = "interpreterLocation";
	// Debugger path string
	public static final String DEBUGGER_PATH_ID = "debuggerLocation";
	// Compiler path string
	public static final String STDLIB_FOLDER_ID = "NitLibEditor";
	
	private String root = null;
	private String compiler = null;
	private String debugger = null;
	private String interpreter = null;
	private String stdlib = null;
	private IPreferenceStore store = null;
	IEclipsePreferences prefs;
	
	public static NitInstallation getInstance(IPreferenceStore store) {
		if(installation == null){
			installation = new NitInstallation(store);
		}
		return installation;
	}
	
	public NitInstallation(IPreferenceStore store){	
		this.store = store;
		init();
	}
	
	public boolean save(){
		  try {
			  //prefs are automatically flushed during a plugin's "super.stop()".
			  prefs.flush();
			  return true;
		  } catch(BackingStoreException e) {
			  //TODO write a real exception handler.
			  e.printStackTrace();
			  return false;
		  }
	}
	public boolean clear(){
		root = compiler = debugger = interpreter = stdlib = "";
		prefs.remove(ROOT_PATH_ID);
		prefs.remove(COMPILER_PATH_ID);
		prefs.remove(DEBUGGER_PATH_ID);
		prefs.remove(INTERPRETER_PATHS_ID);
		prefs.remove(STDLIB_FOLDER_ID);
		return save();
	}
	
	private void init(){
		prefs = InstanceScope.INSTANCE.getNode(NitActivator.PLUGIN_ID); 
		root = prefs.get(ROOT_PATH_ID, null);
		compiler = prefs.get(COMPILER_PATH_ID, null);
		debugger = prefs.get(DEBUGGER_PATH_ID, null);
		interpreter = prefs.get(INTERPRETER_PATHS_ID, null);
		stdlib = prefs.get(STDLIB_FOLDER_ID, null);
		//binaries = setCompiler(store.getString(NIT_BINARIES_DIR));
		//compiler = store.getString(COMPILER_PATH_ID);
		//debugger = store.getString(DEBUGGER_PATH_ID);   
		//interpreter = store.getString(INTERPRETER_PATHS_ID);
		//stdlib = store.getString(STDLIB_FOLDER_ID);
	}
	
	public String getRoot(){
		return root;
	}
	
	public boolean setRoot(String pathname){
		if(setParam(ROOT_PATH_ID, pathname, true)){
			root = pathname;
			return true;
		}
		return false;
	}
	
	public String getCompiler(){
		return compiler;
	}
	
	public boolean setCompiler(String pathname){
		if(setParam(COMPILER_PATH_ID, pathname, false)){
			compiler = pathname;
			return true;
		}
		return false;
	}
	
	public String getDebugger(){
		return debugger;
	}
	
	public boolean setDebugger(String pathname){
		if(setParam(DEBUGGER_PATH_ID, pathname, false)){
			debugger = pathname;
			return true;
		}
		return false;
	}
	
	public String getInterpreter(){
		return interpreter;
	}
	
	public boolean setInterpreter(String pathname){
		if(setParam(INTERPRETER_PATHS_ID, pathname, false)){
			interpreter = pathname;
			return true;
		}
		return false;
	}
	
	public String getStdLib(){
		return stdlib;
	}
	
	public boolean setStdLib(String pathname){
		if(setParam(STDLIB_FOLDER_ID, pathname, true)){
			stdlib = pathname;
			return true;
		}
		return false;
	}
	
	public boolean isFunctional(){
		if(compiler == null || stdlib == null) return false;
		if(compiler.equals("") || stdlib.equals("")) return false;
		else return true;
	}
	
	private boolean setParam(String param_id, String pathname, boolean isDir) {
		if(pathname == null) return false;
		File temp = new File(pathname);
		if(!temp.exists()) return false;
		if(!isDir && temp.isFile()){
			//store.setValue(param_id, pathname);
			prefs.put(param_id, pathname);
			return true;
		} else if(isDir && temp.isDirectory()){
			//store.setValue(param_id, pathname);
			prefs.put(param_id, pathname);
			return true;
		}
		return false;
	}
}
