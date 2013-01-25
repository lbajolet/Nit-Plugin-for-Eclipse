package org.nitlanguage.ndt.ui.preferences;

import java.io.File;

import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import plugin.NitActivator;

/**
 * @author lucas The nit preferences page (Window > Preferences > Nit)
 */
public class NitPreferencePage extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {

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

				File nitComp = new File(dirName + fileSeparator + "bin"
						+ fileSeparator + "nitc");

				File dirLib = new File(dirName + fileSeparator + "lib");

				if (nitComp.exists() && nitComp.isFile()) {
					fileFieldEditor.setStringValue(dirName + fileSeparator
							+ "bin" + fileSeparator + "nitc");
					NitActivator
							.getDefault()
							.getPreferenceStore()
							.setValue(
									NitActivator.COMPILER_PATH_PREFERENCES_ID,
									dirName + fileSeparator + "bin"
											+ fileSeparator + "nitc");
				}

				if (dirLib.exists() && dirLib.isDirectory()) {
					libFieldEditor.setStringValue(dirName + fileSeparator
							+ "lib");
					NitActivator
							.getDefault()
							.getPreferenceStore()
							.setValue(
									NitActivator.STDLIB_FOLDER_PREFERENCES_ID,
									dirName + fileSeparator + "lib");
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
				"Nit Folder Location", getFieldEditorParent());
		addField(nitFieldEditor);

		libFieldEditor = new DirectoryFieldEditor(
				NitActivator.STDLIB_FOLDER_PREFERENCES_ID,
				"Nit StdLib Folder Location", getFieldEditorParent());
		addField(libFieldEditor);

		fileFieldEditor = new FileFieldEditor(
				NitActivator.COMPILER_PATH_PREFERENCES_ID, "Compiler Location",
				getFieldEditorParent());
		addField(fileFieldEditor);
	}

	/**
	 * Initialize the preference page.
	 */
	public void init(IWorkbench workbench) {
		setPreferenceStore(NitActivator.getDefault().getPreferenceStore());
		setDescription("General Nit Developer Tools settings");
	}

}
