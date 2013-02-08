package org.nitlanguage.ndt.ui.preferences;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.nitlanguage.ndt.core.plugin.NitActivator;

/**
 * Plug-in preferences page - define appearance related parameters.
 * @author nathan.heu
 */
public class NitAppearancePage extends FieldEditorPreferencePage implements
    IWorkbenchPreferencePage {

	/**
	 * Create the appearance preference page.
	 */
	public NitAppearancePage() {
		super(GRID);
	}

  public void createFieldEditors() {

  }

  @Override
  public void init(IWorkbench workbench) {
    setPreferenceStore(NitActivator.getDefault().getPreferenceStore());
    setDescription("Appearance of Nit elements in viewers");
  }
} 