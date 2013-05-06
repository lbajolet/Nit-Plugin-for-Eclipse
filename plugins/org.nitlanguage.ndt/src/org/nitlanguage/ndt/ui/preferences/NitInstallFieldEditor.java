package org.nitlanguage.ndt.ui.preferences;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.swt.widgets.Composite;
import org.nitlanguage.ndt.core.plugin.NitActivator;
import org.nitlanguage.ndt.core.plugin.NitInstallation;
import org.nitlanguage.ndt.ui.UiMsg;

/**
 * DirectoryFieldEditor having a special treatment done when
 *         changing the value (auto-updates the values of the libFieldEditor
 *         and fileFieldEditor to the right values if valid
 * @author lucas.bajolet
 * @author nathan.heu
 */
public class NitInstallFieldEditor extends DirectoryFieldEditor {
	private NitInstallation infos;
	private List<FieldEditor> fields;
	private Composite parent;
	
	/**
	 * A field editor representing the stdlib folder
	 */
	private DirectoryFieldEditor libFieldEditor;
	/**
	 * A field editor representing the nitc binary
	 */
	private FileFieldEditor compilerFieldEditor;
	/**
	 * A field editor representing the nit(i) binary
	 */
	private FileFieldEditor interpreterFieldEditor;	
	/**
	 * A field editor representing the nitd binary
	 */
	private FileFieldEditor debuggerFieldEditor;
	
	/**
	 * Constructor
	 * @param parent
	 */
	public NitInstallFieldEditor(Composite parent) {
		super("NitFolder", UiMsg.LBL_NITROOT_LOCATION, parent);
		this.parent = parent;
		infos = NitActivator.getDefault().getNitInstallation();
		init();
		initValues();
	}

	public void init(){

		libFieldEditor = new DirectoryFieldEditor("stdlib",
				 UiMsg.LBL_STDLIB_LOCATION, parent);
		
		compilerFieldEditor = new FileFieldEditor("compiler",
				 UiMsg.LBL_COMPILER_LOCATION, parent);
		
		interpreterFieldEditor = new FileFieldEditor("interpreter", 
				 UiMsg.LBL_INTERPRETER_LOCATION, parent);
				
		debuggerFieldEditor = new FileFieldEditor("debugger",
				 UiMsg.LBL_DEBUGGER_LOCATION, parent);
		
		fields = new ArrayList<FieldEditor>();
		fields.add(this);
		fields.add(libFieldEditor);
		fields.add(compilerFieldEditor);
		fields.add(interpreterFieldEditor);
		fields.add(debuggerFieldEditor);
	}
	
	public void initValues(){
		this.setStringValue(infos.getRoot());
		libFieldEditor.setStringValue(infos.getStdLib());
		compilerFieldEditor.setStringValue(infos.getCompiler());
		interpreterFieldEditor.setStringValue(infos.getInterpreter());
		debuggerFieldEditor.setStringValue(infos.getDebugger());
	}
	
	public void saveChanges(){
		infos.save();
	}
	
	public void clear(){
		infos.clear();
		initValues();
	}
	
	public List<FieldEditor> getFields(){			
		return fields;
	}
	
	@Override
	/**
	 * Notifies that this field editor's change button has been pressed.
	 */
	protected String changePressed() {
		String dirName = super.changePressed();
		String fileSeparator = NitActivator.getFileSeparator();
		
		if (dirName != null) {
			String nitPath = dirName + fileSeparator;
			String binPath = nitPath + "bin" + fileSeparator;
			if(infos.setRoot(nitPath))
				this.setStringValue(infos.getRoot());
			if(infos.setCompiler(binPath + "nitc"))
				compilerFieldEditor.setStringValue(infos.getCompiler());
			if(infos.setInterpreter(binPath + "nit"))
				interpreterFieldEditor.setStringValue(infos.getInterpreter());
			if(infos.setDebugger(binPath + "nitd"))
				debuggerFieldEditor.setStringValue(infos.getDebugger());
			if(infos.setStdLib(nitPath + "lib"))
				libFieldEditor.setStringValue(infos.getStdLib());
		}
		return dirName;
	}
}