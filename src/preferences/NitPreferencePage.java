package preferences;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;

import plugin.NitActivator;

public class NitPreferencePage extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {

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
		
		FileFieldEditor fileFieldEditor = new FileFieldEditor("compilerLocation", "Compiler Location", getFieldEditorParent());
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
