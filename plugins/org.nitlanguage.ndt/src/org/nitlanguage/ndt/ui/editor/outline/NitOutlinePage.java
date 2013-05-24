package org.nitlanguage.ndt.ui.editor.outline;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;
import org.nitlanguage.ndt.ui.editor.NitEditor;

/**
 * An outline page contains structural informations about a file being edited.
 * Internally, it consists related elements organized in a tree manner.
 * @author nathan.heu
 */
public class NitOutlinePage extends ContentOutlinePage implements IAdaptable {
	private NitEditor editor;
	private TreeViewer contentViewer;
	  
	public NitOutlinePage(NitEditor nitEditor) {
		editor = nitEditor;
	}

    public void createControl(Composite parent) {
		super.createControl(parent);
		contentViewer = getTreeViewer();
		contentViewer.addSelectionChangedListener(this);
		contentViewer.setContentProvider(new NitOutlineContentProvider(editor));
		contentViewer.setLabelProvider(new NitStyledLabelProvider(editor));
		refresh(false);
    }
    
    public void selectionChanged(SelectionChangedEvent event) {
    	super.selectionChanged(event);
    	editor.selectAndReveal(0, 20);
    }
    
	public void refresh(Boolean isUpdate) {
		if(contentViewer == null) return;
		//Object[] expandedElements = contentViewer.getExpandedElements();
		//TreePath[] expandedTreePaths = contentViewer.getExpandedTreePaths();   
	    contentViewer.setInput(isUpdate);
	    //ontentViewer.setExpandedElements(expandedElements);
	    //contentViewer.setExpandedTreePaths(expandedTreePaths);
		contentViewer.expandAll();
	}

	@Override
	public Object getAdapter(Class adapter) {
		return null;
	}
	  
}
