package org.nitlanguage.ndt.ui.perspective;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.console.IConsoleConstants;

/**
 * The nit Perspective - Defines initial page layout 
 * @author lucas.bajolet
 */
public class NitPerspective implements IPerspectiveFactory {

	/**
	 * Creates the initial layout for a page.
	 */
	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		addFastViews(layout);
		addViewShortcuts(layout);
		addPerspectiveShortcuts(layout);
		
		IFolderLayout leftFolder = layout.createFolder("left", IPageLayout.LEFT, 0.32f, editorArea);
		leftFolder.addView(IPageLayout.ID_PROJECT_EXPLORER);
		//folderLayout.addView("org.eclipse.ui.views.ContentOutline");

		IFolderLayout bottomFolder = layout.createFolder("bottom", IPageLayout.BOTTOM, 0.79f, editorArea);
		bottomFolder.addView(IConsoleConstants.ID_CONSOLE_VIEW);
		bottomFolder.addView("org.eclipse.pde.runtime.LogView");
		bottomFolder.addView(IPageLayout.ID_PROGRESS_VIEW);
		bottomFolder.addView("org.eclipse.ui.views.AllMarkersView");

		IFolderLayout folderLayout = layout.createFolder("right", IPageLayout.RIGHT, 0.6f, editorArea);
		folderLayout.addView(IPageLayout.ID_OUTLINE);

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
