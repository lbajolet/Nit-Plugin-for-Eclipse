package org.nitlanguage.ndt.ui.wizards;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.nitlanguage.ndt.ui.preferences.NitInstallFieldEditor;

/**
 * @author nathan.heu
 */
public class NitInstallationPageOne extends WizardPage {
	public static final String NAME_NIT_INSTALL_WIZARD_PAGE = "WizardNitInstallationPage";
	public static final String LBL_NIT_INSTALL_PAGE = "General Nit Developer Tools settings"; 
	public static final String DESC_NIT_INSTALL_WIZARD_PAGE = "This wizard appears because you have not yet defined a working nit installation"
						+ "\nPlease select a nit folder location to complete the installation.\n";
	
	/**
	 * Create the wizard.
	 */
	public NitInstallationPageOne() {
		super(NAME_NIT_INSTALL_WIZARD_PAGE);
		setTitle(LBL_NIT_INSTALL_PAGE);
		setDescription(DESC_NIT_INSTALL_WIZARD_PAGE);
	}

	@Override
	public void createControl(Composite parent) {
	    Composite container = new Composite(parent, SWT.NULL);
	    GridLayout layout = new GridLayout();
	    container.setLayout(layout);
	    layout.numColumns = 2;
		// Create the master field editor
	    new NitInstallFieldEditor(container);    
	    setControl(container);
	}
	
	
}
