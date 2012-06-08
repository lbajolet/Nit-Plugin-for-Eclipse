package launcher;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.pde.ui.launcher.EclipseLaunchShortcut;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.part.FileEditorInput;

import plugin.NitActivator;
import asthelpers.AstParserHelper;
import builder.NitNature;

/**
 * @author lucas Shortcut for a nit configuration TODO : Implement me.
 */
public class nitLaunchShortcut extends EclipseLaunchShortcut {

	@Override
	public void launch(ISelection selection, String mode) {
		int toto = 0;
		toto++;
	}

	@Override
	public void launch(IEditorPart editor, String mode) {
		IEditorInput inp = editor.getEditorInput();
		IFile fichier = null;
		if (inp instanceof FileEditorInput) {
			fichier = (((FileEditorInput) inp).getFile());
		}
		if (fichier != null) {
			IProject nitProj = fichier.getProject();
			// On vérifie qu'il s'agit d'un projet nit
			try {
				NitNature nnat = (NitNature) nitProj
						.getNature(NitNature.NATURE_ID);
//				si c'est le cas, on va chercher des candidats potentiels pour la compilation/exécution
//				IFile[] targets = nnat.getPotentialTargets();
				
				nnat.getAstReposit().getAST(nameOfAST);
			} catch (Exception e) {
				if (NitActivator.DEBUG_MODE)
					e.printStackTrace();
			}
		}
	}
}
