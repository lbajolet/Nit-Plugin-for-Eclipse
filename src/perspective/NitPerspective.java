package perspective;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.IFolderLayout;

public class NitPerspective implements IPerspectiveFactory {

	/**
	 * Creates the initial layout for a page.
	 */
	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		addFastViews(layout);
		addViewShortcuts(layout);
		addPerspectiveShortcuts(layout);
		{
			IFolderLayout folderLayout = layout.createFolder("folder", IPageLayout.LEFT, 0.32f, IPageLayout.ID_EDITOR_AREA);
			folderLayout.addView("org.eclipse.ui.navigator.ProjectExplorer");
			folderLayout.addView("org.eclipse.ui.views.ContentOutline");
		}
		{
			IFolderLayout folderLayout = layout.createFolder("folder_1", IPageLayout.BOTTOM, 0.79f, IPageLayout.ID_EDITOR_AREA);
			folderLayout.addView("org.eclipse.pde.runtime.LogView");
			folderLayout.addView("org.eclipse.ui.views.ProgressView");
			folderLayout.addView("org.eclipse.ui.views.AllMarkersView");
			folderLayout.addView("org.eclipse.ui.console.ConsoleView");
		}
	}

	/**
	 * Add fast views to the perspective.
	 */
	private void addFastViews(IPageLayout layout) {
	}

	/**
	 * Add view shortcuts to the perspective.
	 */
	private void addViewShortcuts(IPageLayout layout) {
	}

	/**
	 * Add perspective shortcuts to the perspective.
	 */
	private void addPerspectiveShortcuts(IPageLayout layout) {
	}

}
