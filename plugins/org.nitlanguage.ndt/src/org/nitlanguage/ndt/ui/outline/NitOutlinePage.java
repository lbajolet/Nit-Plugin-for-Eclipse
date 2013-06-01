package org.nitlanguage.ndt.ui.outline;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.debug.ui.actions.ToggleMethodBreakpointActionDelegate;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;
import org.nitlanguage.gen.node.AAttrPropdef;
import org.nitlanguage.gen.node.AConcreteInitPropdef;
import org.nitlanguage.gen.node.AConcreteMethPropdef;
import org.nitlanguage.gen.node.ADeferredMethPropdef;
import org.nitlanguage.gen.node.AIdMethid;
import org.nitlanguage.gen.node.AMethPropdef;
import org.nitlanguage.gen.node.AModule;
import org.nitlanguage.gen.node.AModuleName;
import org.nitlanguage.gen.node.AModuledecl;
import org.nitlanguage.gen.node.AStdClassdef;
import org.nitlanguage.gen.node.PMethid;
import org.nitlanguage.gen.node.PModuledecl;
import org.nitlanguage.gen.node.PPropdef;
import org.nitlanguage.gen.node.TAttrid;
import org.nitlanguage.gen.node.TClassid;
import org.nitlanguage.gen.node.TId;
import org.nitlanguage.gen.node.TKwif;
import org.nitlanguage.gen.node.TKwinit;
import org.nitlanguage.gen.node.TKwmeth;
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
