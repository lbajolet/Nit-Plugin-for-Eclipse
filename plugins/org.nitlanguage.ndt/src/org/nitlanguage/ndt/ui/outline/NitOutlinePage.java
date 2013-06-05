package org.nitlanguage.ndt.ui.outline;
import java.io.Console;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;
import org.nitlanguage.gen.node.AAttrPropdef;
import org.nitlanguage.gen.node.AConcreteInitPropdef;
import org.nitlanguage.gen.node.AConcreteMethPropdef;
import org.nitlanguage.gen.node.ADeferredMethPropdef;
import org.nitlanguage.gen.node.AIdMethid;
import org.nitlanguage.gen.node.AModule;
import org.nitlanguage.gen.node.AModuleName;
import org.nitlanguage.gen.node.AModuledecl;
import org.nitlanguage.gen.node.AStdClassdef;
import org.nitlanguage.gen.node.Node;
import org.nitlanguage.gen.node.PMethid;
import org.nitlanguage.gen.node.PPropdef;
import org.nitlanguage.gen.node.TClassid;
import org.nitlanguage.gen.node.TId;
import org.nitlanguage.gen.node.TKwinit;
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
		//registers the listener(used here to obtain text selections)
		getSite().getWorkbenchWindow().getSelectionService().addSelectionListener(listener);
    }
    
	@Override
	public Object getAdapter(Class adapter) {
		// TODO Auto-generated method stub
		return null;
	}	
	
    /**
     * Selects the text in the editor corresponding to the newly selected tree item
     */
    public void selectionChanged(SelectionChangedEvent event) {   	
    	if(event.getSelectionProvider() == getTreeViewer()) return;
    	super.selectionChanged(event);
    	TreeSelection selection = (TreeSelection)event.getSelection();
    	Object item = selection.getFirstElement();
    	if(item instanceof Node){
        	NodeInformation infos = getNodeInformations((Node)item);
	    	if(infos.isValid()){
		    	editor.selectAndReveal(infos.getOffsetInEditor(editor), infos.getLenght());
	    	}
    	}
    }
    
    /**
     * Forces the refresh of the tree
     * Knows problem - The state of the tree is not maintained
     * @param isUpdate
     */
	public void refresh(Boolean isUpdate) {
		if(contentViewer == null) return;
		//Object[] expandedElements = contentViewer.getExpandedElements();
		//TreePath[] expandedTreePaths = contentViewer.getExpandedTreePaths();   
	    contentViewer.setInput(isUpdate);
	    //contentViewer.setExpandedElements(expandedElements);
	    //contentViewer.setExpandedTreePaths(expandedTreePaths);
		contentViewer.expandAll();
	}
	
	/**
	 * Listener registered for the selection service
	 * Known problem - event raised multiple times 
	 */
	private ISelectionListener listener = new ISelectionListener() {
		
		public void selectionChanged(IWorkbenchPart sourcepart, ISelection selection) {
			//ignore TreeViewer selections
			if (sourcepart != getTreeViewer() && selection instanceof TextSelection) {
				TextSelection sel = (TextSelection)selection;		
				if(sel.getLength() == 0) return;
				visit(getTreeViewer().getTree().getItems(), sel);			
			}
		}
	};
	
	/**
	 * Visits an array of tree items
	 * @param items
	 * @param sel
	 * @return
	 */
	protected boolean visit(TreeItem[] items, TextSelection sel){
		for(TreeItem it : items) {
			if(visit(it, sel)) return true;
		}
		return false;
	}
	
	/**
	 * Visits a tree item
	 * @param item
	 * @param sel
	 * @return
	 */
	protected boolean visit(TreeItem item, TextSelection sel){
		if(!(item.getData() instanceof Node)) return false;
		NodeInformation infos = getNodeInformations((Node)item.getData());
		if(!infos.equals(sel, editor)) return visit(item.getItems(), sel);
		setSelection(new TreeSelection(new TreePath(new Object[]{item.getData()})));
		return true;
	}  
	
    /**
     * Extracts parsed document informations from an AST node
     * @param item
     * @return
     */
    public NodeInformation getNodeInformations(Node item){
    	if(item instanceof AModule){
    		AModuledecl modDec = (AModuledecl)((AModule)item).getModuledecl();
    		TId moduleId = ((AModuleName)modDec.getName()).getId();
    		return new NodeInformation(moduleId.getLine(),
    									moduleId.getPos(),
    									moduleId.getText().length());
    	} else if(item instanceof AStdClassdef){
    		TClassid classId = ((AStdClassdef)item).getId(); 
    		return new NodeInformation(classId.getLine(),
    									classId.getPos(),
    									classId.getText().length());
    	} else if(item instanceof PPropdef){
    		if(item instanceof AAttrPropdef){
    			AAttrPropdef attrDef = (AAttrPropdef)item;
    			if(attrDef.getId2() != null) {
            		return new NodeInformation(attrDef.getId2().getLine(),
            									attrDef.getId2().getPos(),
            									attrDef.getId2().getText().length());
    			} else {
    				return new NodeInformation(attrDef.getId().getLine(),
    											attrDef.getId().getPos(),
    											attrDef.getId().getText().length());
    			}
    		} else if(item instanceof AConcreteInitPropdef){
    			TKwinit initId = ((AConcreteInitPropdef)item).getKwinit();
    			return new NodeInformation(initId.getLine(),
    										initId.getPos(),
    										initId.getText().length());
    		} else if(item instanceof AConcreteMethPropdef){
    			PMethid methId = ((AConcreteMethPropdef)item).getMethid();
    			TId id = ((AIdMethid)methId).getId();
    			return new NodeInformation(id.getLine(),
    										id.getPos(),
    										id.getText().length());
    		} else if(item instanceof ADeferredMethPropdef){
      			PMethid methId = ((ADeferredMethPropdef)item).getMethid();
    			TId id = ((AIdMethid)methId).getId();
    			return new NodeInformation(id.getLine(),
    										id.getPos(),
    										id.getText().length());
    		}   		
    	}
    	return null;
    }
}
