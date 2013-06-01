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
    
    /**
     * Selects the text in the editor corresponding to the newly selected tree item
     */
    public void selectionChanged(SelectionChangedEvent event) {
    	super.selectionChanged(event);
    	TreeSelection selection = (TreeSelection)event.getSelection();
    	Object item = selection.getFirstElement();
    	int line = -1, position = -1, length = -1;
    	if(item instanceof AModule){
    		AModuledecl modDec = (AModuledecl)((AModule)item).getModuledecl();
    		TId moduleId = ((AModuleName)modDec.getName()).getId();
    		line = moduleId.getLine();
    		position = moduleId.getPos();
    		length = moduleId.getText().length();
    	} else if(item instanceof AStdClassdef){
    		TClassid classId = ((AStdClassdef)item).getId(); 
    		line = classId.getLine();
    		position = classId.getPos();
    		length = classId.getText().length();
    	} else if(item instanceof PPropdef){
    		if(item instanceof AAttrPropdef){
    			AAttrPropdef attrDef = (AAttrPropdef)item;
    			if(attrDef.getId2() != null) {
            		line = attrDef.getId2().getLine();
            		position = attrDef.getId2().getPos();
            		length = attrDef.getId2().getText().length();
    			} else {
            		line = attrDef.getId().getLine();
            		position = attrDef.getId().getPos();
            		length = attrDef.getId().getText().length();
    			}
    		} else if(item instanceof AConcreteInitPropdef){
    			TKwinit initId = ((AConcreteInitPropdef)item).getKwinit();
    			line = initId.getLine();
    			position = initId.getPos();
    			length = initId.getText().length();
    		} else if(item instanceof AConcreteMethPropdef){
    			PMethid methId = ((AConcreteMethPropdef)item).getMethid();
    			TId id = ((AIdMethid)methId).getId();
    			line = id.getLine();
    			position = id.getPos();
    			length = id.getText().length();
    		} else if(item instanceof ADeferredMethPropdef){
      			PMethid methId = ((ADeferredMethPropdef)item).getMethid();
    			TId id = ((AIdMethid)methId).getId();
    			line = id.getLine();
    			position = id.getPos();
    			length = id.getText().length();
    		}   		
    	} 
    	if(line != -1 && position != -1 && length != -1){
    		try {
				int offset = editor.getDocument().getLineOffset(line - 1);
	    		editor.selectAndReveal(offset + position - 1, length);
			} catch (BadLocationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
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
