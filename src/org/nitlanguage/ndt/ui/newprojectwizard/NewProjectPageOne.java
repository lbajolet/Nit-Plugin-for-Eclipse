package org.nitlanguage.ndt.ui.newprojectwizard;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;

/**
 * @author lucas.bajolet
 */
public class NewProjectPageOne extends WizardNewProjectCreationPage {

	/**
	 * Create the wizard.
	 */
	public NewProjectPageOne() {
		super("WizardNewProjectCreationPage");
		setTitle("New Nit Project");
		setDescription("Helps create a new Nit Project");
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
