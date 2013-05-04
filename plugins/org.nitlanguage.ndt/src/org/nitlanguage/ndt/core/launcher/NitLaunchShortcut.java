package org.nitlanguage.ndt.core.launcher;

import java.io.FileNotFoundException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.ILaunchShortcut;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;

/**
 * Shortcut for a nit configuration
 * Responsible for interpreting active editor or selection and
 * launching a Nit application according to the context.
 * If there is no existing nor reusable run configuration, launch shortcut creates one silently.
 * @author lucas.bajolet
 * @author nathan.heu 
 */
public class NitLaunchShortcut implements ILaunchShortcut {
	public static final String NIT_LAUNCH_CONFIG = "org.nitlanguage.ndt.nitLauncher";
	
	@Override
	public void launch(ISelection selection, String mode) {
		if(selection instanceof IStructuredSelection) {
			TreeSelection tree_sel = (TreeSelection)selection;
			TreePath tree_path = tree_sel.getPaths()[0];
			IPath project_path = null, file_path = null;
			switch(tree_path.getSegmentCount())
			{
				case 2:
					project_path = ((IProject)tree_path.getFirstSegment()).getFullPath();
					file_path = ((IFile)tree_path.getLastSegment()).getFullPath();
					break;
			}
			
			try {
				launchDefault(project_path, file_path, mode);
			} catch(CoreException ex){
				
			}
		}
	}

	public IFile getEditedFiled(IEditorPart editor) {
		return (IFile) editor.getEditorInput().getAdapter(IFile.class);
	}
	
	@Override
	public void launch(IEditorPart editor, String mode) {
		IFile target_file = getEditedFiled(editor);
		IProject target_project = target_file.getProject();
		try {
			launchDefault(target_project.getFullPath(), target_file.getFullPath(), mode);
		} catch(CoreException ex){
			
		}
	}
	
	private void launchDefault(IPath project, IPath target_file, String mode) throws CoreException
	{
		ILaunchConfiguration configuration = null;
		ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
		ILaunchConfigurationType type =
		      manager.getLaunchConfigurationType(NIT_LAUNCH_CONFIG);
		ILaunchConfiguration[] configurations =
		      manager.getLaunchConfigurations(type);
		   for (int i = 0; i < configurations.length; i++) {
		      configuration = configurations[i];
		      if (configuration != null /*&& configuration.getName().equals("Default")*/) {
		    	  // run == ILaunchManager.RUN_MODE
		    	  DebugUITools.launch(configuration, mode);
		    	 //configuration.delete();
		         return;
		      }
		   }
		 
		ILaunchConfigurationWorkingCopy workingCopy =
		      type.newInstance(null, "Default");
		workingCopy.setAttribute(NitMainTab.OUTPUT_PATH, getWorkspaceDir().toString() + project.toString());
		workingCopy.setAttribute(NitMainTab.TARGET_FILE_PATH, target_file.toString());
		configuration = workingCopy.doSave();
		DebugUITools.launch(configuration, mode);
	}
	
	public Path getActiveFilePath() throws FileNotFoundException{
		// Get the currently selected file from the editor
		IWorkbenchPart workbenchPart = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActivePart(); 
		IFile file = (IFile) workbenchPart.getSite().getPage().getActiveEditor().getEditorInput().getAdapter(IFile.class);
		if (file == null) return null;
		else return (Path)file.getFullPath();
	}
	
	public java.io.File getWorkspaceDir(){
		//get object which represents the workspace  
		IWorkspace workspace = ResourcesPlugin.getWorkspace();       
		//get location of workspace (java.io.File)  
		return workspace.getRoot().getLocation().toFile();
	}
}
