package org.nitlanguage.ndt.ui.preferences;

import java.io.File;

import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.nitlanguage.ndt.core.plugin.NitActivator;

/**
 * Main plug-in preferences page (Window > Preferences > Nit).
 * Give access to child preferences pages.
 * @author lucas.bajolet
 */
public class NitPreferencePage extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {
	public static final String NIT_BINARIES_DIR = "bin";
	public static final String NIT_STDLIB_DIR = "lib";
	
	public static final String NIT_COMPILER_NAME = "nitc";
	public static final String NIT_INTERPRETER_NAME = "nit";
	public static final String NIT_DEBUGGER_NAME = "nit";
	
	public static final String LBL_PREFERENCE_PAGE = "General Nit Developer Tools settings"; 
	public static final String LBL_NITROOT_LOCATION = "Nit Folder Location";
	public static final String LBL_STDLIB_LOCATION = "Nit StdLib Folder Location";
	public static final String LBL_COMPILER_LOCATION = "Compiler Location";
	public static final String LBL_INTERPRETER_LOCATION = "Interpreter Location";
	public static final String LBL_DEBUGGER_LOCATION = "Debugger Location";
	/**
	 * A simple field editor with a special action when changing its value
	 * Represents the location of the nit global folder
	 */
	NitMainDirFieldEditor nitFieldEditor;
	/**
	 * A field editor representing the stdlib folder
	 */
	DirectoryFieldEditor libFieldEditor;
	/**
	 * A field editor representing the nitc binary
	 */
	FileFieldEditor fileFieldEditor;
	/**
	 * A field editor representing the nit(i) binary
	 */
	FileFieldEditor interpreterFieldEditor;
	
	/**
	 * A field editor representing the nitd binary
	 */
	FileFieldEditor debuggerFieldEditor;

	/**
	 * @author lucas DirectoryFieldEditor having a special treatment done when
	 *         changing the value (auto-updates the values of the libFieldEditor
	 *         and fileFieldEditor to the right values if valid
	 */
	private class NitMainDirFieldEditor extends DirectoryFieldEditor {

		public NitMainDirFieldEditor(String name, String labelText,
				Composite parent) {
			super(name, labelText, parent);
		}

		protected String changePressed() {
			String dirName = super.changePressed();

			if (dirName != null) {
				String fileSeparator = System.getProperty("file.separator");
				if (!fileSeparator.equals("/")) {
					fileSeparator = "\\";
				}

				File nitComp = new File(dirName + fileSeparator + NIT_BINARIES_DIR
						+ fileSeparator + NIT_COMPILER_NAME);
				
				File nitInterpreter = new File(dirName + fileSeparator + NIT_BINARIES_DIR
						+ fileSeparator + NIT_INTERPRETER_NAME);
				
				File nitDebugger = new File(dirName + fileSeparator + NIT_BINARIES_DIR
						+ fileSeparator + NIT_DEBUGGER_NAME);

				File dirLib = new File(dirName + fileSeparator + NIT_STDLIB_DIR);

				if (nitComp.exists() && nitComp.isFile()) {
					fileFieldEditor.setStringValue(dirName + fileSeparator
							+ NIT_BINARIES_DIR + fileSeparator + NIT_COMPILER_NAME);
					NitActivator
							.getDefault()
							.getPreferenceStore()
							.setValue(
									NitActivator.COMPILER_PATH_PREFERENCES_ID,
									dirName + fileSeparator + NIT_BINARIES_DIR
											+ fileSeparator + NIT_COMPILER_NAME);
				}
				
				if (nitInterpreter.exists() && nitInterpreter.isFile()) {
					interpreterFieldEditor.setStringValue(dirName + fileSeparator
							+ NIT_BINARIES_DIR + fileSeparator + NIT_INTERPRETER_NAME);
					NitActivator
							.getDefault()
							.getPreferenceStore()
							.setValue(
									NitActivator.INTERPRETER_PATH_PREFERENCES_ID,
									dirName + fileSeparator + NIT_BINARIES_DIR
											+ fileSeparator + NIT_INTERPRETER_NAME);
				}

				if (nitDebugger.exists() && nitDebugger.isFile()) {
					debuggerFieldEditor.setStringValue(dirName + fileSeparator
							+ NIT_BINARIES_DIR + fileSeparator + NIT_DEBUGGER_NAME);
					NitActivator
							.getDefault()
							.getPreferenceStore()
							.setValue(
									NitActivator.DEBUGGER_PATH_PREFERENCES_ID,
									dirName + fileSeparator + NIT_BINARIES_DIR
											+ fileSeparator + NIT_DEBUGGER_NAME);
				}
				
				if (dirLib.exists() && dirLib.isDirectory()) {
					libFieldEditor.setStringValue(dirName + fileSeparator
							+ NIT_STDLIB_DIR);
					NitActivator
							.getDefault()
							.getPreferenceStore()
							.setValue(
									NitActivator.STDLIB_FOLDER_PREFERENCES_ID,
									dirName + fileSeparator + NIT_STDLIB_DIR);
				}
			}

			return dirName;
		}
	}

	/**
	 * Create the preference page.
	 */
	public NitPreferencePage() {
		super(GRID);
	}

	/**
	 * Create contents of the preference page.
	 */
	@Override
	protected void createFieldEditors() {
		// Create the field editors

		nitFieldEditor = new NitMainDirFieldEditor("NitFolder",
				LBL_NITROOT_LOCATION, getFieldEditorParent());
		addField(nitFieldEditor);

		libFieldEditor = new DirectoryFieldEditor(
				NitActivator.STDLIB_FOLDER_PREFERENCES_ID,
				LBL_STDLIB_LOCATION, getFieldEditorParent());
		addField(libFieldEditor);

		fileFieldEditor = new FileFieldEditor(
				NitActivator.COMPILER_PATH_PREFERENCES_ID, LBL_COMPILER_LOCATION,
				getFieldEditorParent());
		addField(fileFieldEditor);
		
		interpreterFieldEditor = new FileFieldEditor(
				NitActivator.INTERPRETER_PATH_PREFERENCES_ID, LBL_INTERPRETER_LOCATION,
				getFieldEditorParent());
		addField(interpreterFieldEditor);
		
		debuggerFieldEditor = new FileFieldEditor(
				NitActivator.DEBUGGER_PATH_PREFERENCES_ID, LBL_DEBUGGER_LOCATION,
				getFieldEditorParent());
		addField(debuggerFieldEditor);
	}

	/**
	 * Initialize the preference page.
	 */
	public void init(IWorkbench workbench) {
		setPreferenceStore(NitActivator.getDefault().getPreferenceStore());
		setDescription(LBL_PREFERENCE_PAGE);
	}

}
