package org.nitlanguage.ndt.ui.preferences;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.nitlanguage.ndt.core.plugin.NitActivator;

/**
 * Plug-in preferences page - define build path related parameters.
 * @author nathan.heu
 */
public class NitBuildPathPage extends FieldEditorPreferencePage implements
    IWorkbenchPreferencePage {

	/**
	 * Create the build path preference page.
	 */
	public NitBuildPathPage() {
		super(GRID);
	}

  public void createFieldEditors() {
  }

  @Override
  public void init(IWorkbench workbench) {
    setPreferenceStore(NitActivator.getDefault().getPreferenceStore());
    setDescription("Specify the build path entries used as default by the New Nit Project creation wizard");
  }
} 