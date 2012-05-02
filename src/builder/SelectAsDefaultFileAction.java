package builder;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

public class SelectAsDefaultFileAction implements IObjectActionDelegate {

	private ISelection selection;

	@Override
	public void run(IAction action) {
		if (selection instanceof IStructuredSelection) {
			for (Iterator it = ((IStructuredSelection) selection).iterator(); it
					.hasNext();) {
				Object element = it.next();
				IFile file = null;
				if (element instanceof IFile) {
					IFile tempFile = (IFile) element;
					try {
						NitNature nat = (NitNature) tempFile.getProject()
								.getNature(NitNature.NATURE_ID);
						if (tempFile.getName().endsWith(".nit") && nat != null) {
							file = tempFile;
							if (nat.getPropertiesHelper() != null) {
								nat.getPropertiesHelper().write("defaultFile",
										file.getName());
								try {
									nat.getPropertiesHelper().save();
								} catch (FileNotFoundException e) {
									e.printStackTrace();
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						}
					} catch (CoreException ce) {
					}
				}
				if (file != null) {
					setAsDefault(file);
				}
			}
		}
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		this.selection = selection;
	}

	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
	}

	/**
	 * @param file
	 *            File in the Project Manager to set as target for compilation
	 */
	private void setAsDefault(IFile file) {
		try {
			IProject proj = file.getProject();

			NitNature nn = (NitNature) proj
					.getNature("org.uqam.nit.ndt.nature");

			nn.setDefaultFile(file);

		} catch (CoreException ce) {
		}
	}
}
