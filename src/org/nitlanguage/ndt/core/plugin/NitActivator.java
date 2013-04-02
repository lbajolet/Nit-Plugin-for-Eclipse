package org.nitlanguage.ndt.core.plugin;

import java.net.URL;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * Activator controls the lifecycle of the plug-in
 * Singleton
 * @author lucas.bajolet
 */
public class NitActivator extends AbstractUIPlugin {
	
	// The plug-in ID
	public static final String PLUGIN_ID = "org.nitlanguage.ndt.plugin"; //$NON-NLS-1$
	
	// Compiler path string
	public static final String COMPILER_PATH_PREFERENCES_ID = "compilerLocation";
	
	// Interpreter path string
	public static final String INTERPRETER_PATH_PREFERENCES_ID = "interpreterLocation";
	
	// Debugger path string
	public static final String DEBUGGER_PATH_PREFERENCES_ID = "debuggerLocation";
	
	// Compiler path string
	public static final String STDLIB_FOLDER_PREFERENCES_ID = "NitLibEditor";
	
	// Debug trigger
	public static final boolean DEBUG_MODE = false;

	// The shared instance
	private static NitActivator plugin;
	
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
	
	public static IPath getWorkspacePath(){
		//get object which represents the workspace  
		IWorkspace workspace = ResourcesPlugin.getWorkspace();  
		return workspace.getRoot().getLocation();
	}
	
    /*protected void initializeImageRegistry(ImageRegistry registry) {
        Bundle bundle = Platform.getBundle(PLUGIN_ID);
        IPath path = new Path("icons/test.gif");
        URL url = FileLocator.find(bundle, path, null);
        ImageDescriptor desc = ImageDescriptor.createFromURL(url);
        registry.put(IMAGE_ID, desc);
     }*/

}
