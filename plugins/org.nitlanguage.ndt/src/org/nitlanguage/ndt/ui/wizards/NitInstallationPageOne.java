package org.nitlanguage.ndt.ui.wizards;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.nitlanguage.ndt.ui.UiMsg;
import org.nitlanguage.ndt.ui.preferences.NitInstallFieldEditor;

/**
 * @author nathan.heu
 */
public class NitInstallationPageOne extends WizardPage {

	NitInstallFieldEditor ctrl;
	
	/**
	 * Create the wizard.
	 */
	public NitInstallationPageOne() {
		super(UiMsg.NAME_NIT_INSTALL_WIZARD_PAGE);
		setTitle(UiMsg.LBL_NIT_INSTALL_PAGE);
		//setDescription(UiMsg.DESC_NIT_INSTALL_WIZARD_PAGE);
		setDescription("Link your plugn with a valid nit installation.");
	}

	@Override
	public void createControl(Composite parent) {
	    //Composite container = new Composite(parent, SWT.NULL);
	    //GridLayout layout = new GridLayout();
	    //container.setLayout(layout);
	    //layout.numColumns = 2;
		// Create the master field editor
	    ctrl = new NitInstallFieldEditor(parent);    
	    setControl(parent);
	}
	
	public void save(){
		ctrl.save();
	}
	
}
