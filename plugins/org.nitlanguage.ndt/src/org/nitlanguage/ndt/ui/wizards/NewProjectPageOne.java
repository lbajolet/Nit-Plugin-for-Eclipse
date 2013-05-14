package org.nitlanguage.ndt.ui.wizards;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;
import org.nitlanguage.ndt.ui.UiMsg;

/**
 * @author lucas.bajolet
 */
public class NewProjectPageOne extends WizardNewProjectCreationPage {
	/**
	 * Create the wizard.
	 */
	public NewProjectPageOne() {
		super(UiMsg.NAME_NEWPROJECT_WIZARD_PAGE);
		setTitle(UiMsg.TITLE_NEWPROJECT_WIZARD_PAGE);
		setDescription(UiMsg.DESC_NEWPROJECT_WIZARD_PAGE);
	}

	/**
	 * Create contents of the wizard.
	 * 
	 * @param parent
	 */
	public void createControl(Composite parent) {
		super.createControl(parent);
	}
}
