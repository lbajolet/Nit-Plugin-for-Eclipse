package builder;

import java.util.Iterator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.internal.ObjectPluginAction;

public class SelectAsDefaultFileAction implements IObjectActionDelegate {

	private ISelection selection;
	
	@Override
	public void run(IAction action) {
		// TODO Auto-generated method stub
		if (selection instanceof IStructuredSelection) {
			for (Iterator it = ((IStructuredSelection) selection).iterator(); it
					.hasNext();) {
				Object element = it.next();
				IFile file = null;
				if (element instanceof IFile) {
					IFile tempFile = (IFile) element;
					try{
						if(tempFile.getName().endsWith(".nit") && tempFile.getProject().getNature("org.uqam.nit.ndt.nature") != null)
						{
							file = tempFile;
						}
					} catch (CoreException ce){}
				}
				if (file != null) {
					setAsDefault(file);
				}
			}
		}
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		// TODO Auto-generated method stub
		this.selection = selection;
	}

	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		int toto = 0;
	}
	
	private void setAsDefault(IFile file)
	{
		try{
			IProject proj = file.getProject();
			
			NitNature nn = (NitNature) proj.getNature("org.uqam.nit.ndt.nature");
			
			nn.setDefaultFile(file);
		}catch (CoreException ce){}
	}
}
