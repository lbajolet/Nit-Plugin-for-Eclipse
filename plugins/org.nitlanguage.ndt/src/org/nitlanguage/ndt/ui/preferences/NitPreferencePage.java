package org.nitlanguage.ndt.ui.preferences;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.nitlanguage.ndt.ui.UiMsg;

/**
 * Main plug-in preferences page (Window > Preferences > Nit).
 * Give access to child preferences pages.
 * @author lucas.bajolet
 * @author nathan.heu
 */
public class NitPreferencePage extends PreferencePage implements
		IWorkbenchPreferencePage {

	/**
	 * A simple field editor with a special action when changing its value
	 * Represents the location of the nit global folder
	 */
	private NitInstallFieldEditor nitFieldEditor;
	
	/**
	 * Default constructor
	 */
	public NitPreferencePage() {
		super(UiMsg.NAME_NIT_INSTALL_WIZARD_PAGE);
	}
	
	@Override
	public void init(IWorkbench workbench) {
		// TODO Auto-generated method stub
		setTitle(UiMsg.LBL_NIT_INSTALL_PAGE);
		setDescription(UiMsg.LBL_PREFERENCE_PAGE);
	}
	
	@Override
	public boolean performOk() {
		nitFieldEditor.save();
		return true;
	}
	
	@Override
	protected void performDefaults() {
		super.performDefaults();
		nitFieldEditor.clear();
	}
	
	/**
	 * Create contents of the preference page.
	 */
	@Override
	protected Control createContents(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
	    container.setLayout(layout);
	    layout.numColumns = 2;
		// Create the master field editor
	    nitFieldEditor = new NitInstallFieldEditor(container);    
	    //setControl(container);
	    return container;
	}
}
	
