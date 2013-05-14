package org.nitlanguage.ndt.ui.preferences;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.nitlanguage.ndt.core.plugin.NitActivator;

/**
 * Plug-in preferences page - define code-style related parameters.
 * @author nathan.heu
 */
public class NitCodeStylePage extends FieldEditorPreferencePage implements
    IWorkbenchPreferencePage {

	/**
	 * Create the code style preference page.
	 */
	public NitCodeStylePage() {
		super(GRID);
	}

  public void createFieldEditors() {

  }

  @Override
  public void init(IWorkbench workbench) {
    setPreferenceStore(NitActivator.getDefault().getPreferenceStore());
  }
} 