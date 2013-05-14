package org.nitlanguage.ndt.core.launcher;

import org.eclipse.debug.ui.AbstractLaunchConfigurationTabGroup;
import org.eclipse.debug.ui.CommonTab;
import org.eclipse.debug.ui.EnvironmentTab;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;

/**
 * The tab group for Nit Configurations
 * @author lucas.bajolet
 * @author nathan.heu
 */
public class NitLauncherTabGroup extends AbstractLaunchConfigurationTabGroup {

	@Override
	public void createTabs(ILaunchConfigurationDialog dialog, String mode) {
		ILaunchConfigurationTab[] tabs = new ILaunchConfigurationTab[] { 
				new NitMainTab(),
				new EnvironmentTab(),
				new CommonTab()
		};

		setTabs(tabs);
	}
}
