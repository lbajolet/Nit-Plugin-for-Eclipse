package org.nitlanguage.ndt.ui.wizards;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;

/**
 * @author lucas.bajolet
 */
public class NewProjectPageOne extends WizardNewProjectCreationPage {
	public static final String NAME_NEWPROJECT_WIZARD_PAGE = "WizardNewProjectCreationPage";
	public static final String TITLE_NEWPROJECT_WIZARD_PAGE = "New Nit Project";
	public static final String DESC_NEWPROJECT_WIZARD_PAGE = "Helps create a new Nit Project";
	
	/**
	 * Create the wizard.
	 */
	public NewProjectPageOne() {
		super(NAME_NEWPROJECT_WIZARD_PAGE);
		setTitle(TITLE_NEWPROJECT_WIZARD_PAGE);
		setDescription(DESC_NEWPROJECT_WIZARD_PAGE);
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
