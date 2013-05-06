package org.nitlanguage.ndt.core.plugin;

import java.net.URL;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.wizards.IWizardDescriptor;
import org.eclipse.ui.wizards.IWizardRegistry;
import org.nitlanguage.ndt.ui.preferences.NitPreferencePage;
import org.nitlanguage.ndt.ui.wizards.NewProjectPageOne;
import org.nitlanguage.ndt.ui.wizards.NewProjectWizard;
import org.nitlanguage.ndt.ui.wizards.NitInstallationWizard;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * Activator controls the lifecycle of the plug-in
 * Singleton
 * @author lucas.bajolet
 */
public class NitActivator extends AbstractUIPlugin {
	
	// Debug trigger
	public static final boolean DEBUG_MODE = false;

	// The shared instance
	private static NitActivator plugin;
	
	// The shared nit installation instance
	private NitInstallation nit_installation;
	
	/**
	 * The constructor
	 */
	public NitActivator() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		checkNitInstallation();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static NitActivator getDefault() {
		if(plugin == null){
			plugin = new NitActivator();
		}
		return plugin;
	}
	
	/**
	 * Returns parametrized nit installation informations
	 * @return
	 */
	public NitInstallation getNitInstallation() {
		if(nit_installation == null){
			nit_installation = NitInstallation.getInstance(
					getDefault().getPreferenceStore());
		}
		return nit_installation;
	}
	
	/**
	 * Returns workspace path
	 * @return
	 */
	public static IPath getWorkspacePath(){
		//get object which represents the workspace  
		IWorkspace workspace = ResourcesPlugin.getWorkspace();  
		return workspace.getRoot().getLocation();
	}
	
	/**
	 * Check nit installation configuration
	 */
	public void checkNitInstallation() {
		if(!getNitInstallation().isFunctional())
		{
			PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {	
				@Override
				public void run() {
					{
						System.out.println("Nit launch checking : fail");
						WizardDialog dialog = new WizardDialog(PlatformUI.getWorkbench().getDisplay().getActiveShell(), 
								new NitInstallationWizard());
						dialog.open(); 
					} 
				}
			});
		}
		else {
			System.out.println("Nit launch checking : success");
		}
	}
	
	/**
	 * Returns current platform separator
	 * @return
	 */
	public static String getFileSeparator(){
		String fileSeparator = System.getProperty("file.separator");
		if (!fileSeparator.equals("/")) fileSeparator = "\\";
		return fileSeparator;
	}
	
    /*protected void initializeImageRegistry(ImageRegistry registry) {
        Bundle bundle = Platform.getBundle(PLUGIN_ID);
        IPath path = new Path("icons/test.gif");
        URL url = FileLocator.find(bundle, path, null);
        ImageDescriptor desc = ImageDescriptor.createFromURL(url);
        registry.put(IMAGE_ID, desc);
     }*/

}
