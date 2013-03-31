package org.nitlanguage.ndt.ui.editor.outline;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jdt.ui.ISharedImages;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.graphics.Image;
import org.nitlanguage.gen.node.AAttrPropdef;
import org.nitlanguage.gen.node.AConcreteInitPropdef;
import org.nitlanguage.gen.node.AConcreteMethPropdef;
import org.nitlanguage.gen.node.ADeferredMethPropdef;
import org.nitlanguage.gen.node.AInterfaceClasskind;
import org.nitlanguage.gen.node.AModule;
import org.nitlanguage.gen.node.AModuledecl;
import org.nitlanguage.gen.node.AParam;
import org.nitlanguage.gen.node.APublicVisibility;
import org.nitlanguage.gen.node.ASignature;
import org.nitlanguage.gen.node.AStdClassdef;
import org.nitlanguage.gen.node.PClasskind;
import org.nitlanguage.gen.node.PMethid;
import org.nitlanguage.gen.node.PParam;
import org.nitlanguage.gen.node.PPropdef;
import org.nitlanguage.gen.node.PType;
import org.nitlanguage.gen.node.PVisibility;
import org.nitlanguage.ndt.ui.editor.NitEditor;

/**
 * Provides label for elements returned by the associated content provider
 * A label consists of an image and a char sequence
 * @author nathan.heu
 */
public class NitStyledLabelProvider extends StyledCellLabelProvider {
  private NitEditor editor;

  public NitStyledLabelProvider(NitEditor editor) {
	  this.editor = editor;
  }

  @Override
  /**
   * Returns the image and the rich label associated with the element
   * This cell will be shown in the TreeViewer of the Outline
   * Combine work of getImage(Object element) and getText(Object element) 
   * Difference is that text returned can be rich (color,...)
   */
  public void update(ViewerCell cell) {
    Object element = cell.getElement();
    StyledString text = new StyledString();
	ISharedImages images = JavaUI.getSharedImages();
	  
    if (element instanceof AModule) {
    	cell.setImage(images.getImage(ISharedImages.IMG_OBJS_PACKAGE));
		AModuledecl mod_dec = (AModuledecl)((AModule)element).getModuledecl();
    	text.append(mod_dec.getName().toString());   	
    }
    else if (element instanceof AStdClassdef) {
    	  AStdClassdef class_def = (AStdClassdef)element;
		  PClasskind kind = class_def.getClasskind();
		  if(kind.getClass() == AInterfaceClasskind.class){
			  cell.setImage(images.getImage(ISharedImages.IMG_OBJS_INTERFACE));
		  } else {
			  cell.setImage(images.getImage(ISharedImages.IMG_OBJS_CLASS));
		  }
		  text.append(class_def.getId().getText());
    }
    else if (element instanceof PPropdef) {
    	if (element instanceof AAttrPropdef){
    		AAttrPropdef attr_def = (AAttrPropdef)element;
    		PVisibility vis = attr_def.getVisibility();
    		if (vis instanceof APublicVisibility){
    			cell.setImage(images.getImage(ISharedImages.IMG_FIELD_PUBLIC));
    		} else {
    			cell.setImage(images.getImage(ISharedImages.IMG_FIELD_PRIVATE));
    		}	 
    		text.append(attr_def.getId2().toString());
    		text.append(": " + attr_def.getType().toString(), StyledString.COUNTER_STYLER);
    	}
    	else if (element instanceof AConcreteInitPropdef){
    		AConcreteInitPropdef init_def = (AConcreteInitPropdef)element;
    		PVisibility vis = init_def.getVisibility();
    		if (vis instanceof APublicVisibility){
    			cell.setImage(images.getImage(ISharedImages.IMG_OBJS_INNER_CLASS_DEFAULT));
    		} else {
    			cell.setImage(images.getImage(ISharedImages.IMG_OBJS_INNER_CLASS_PRIVATE));
    		}
    		if(init_def.getMethid() == null){
    			text.append("init");
    		} else {
    			ASignature sign = (ASignature)init_def.getSignature();
        		printSignature(text, "init." + init_def.getMethid().toString(), sign);	       	
    		}
    	}    	
    	else if (element instanceof AConcreteMethPropdef){
    		AConcreteMethPropdef meth_def = (AConcreteMethPropdef)element;
    		PVisibility vis = meth_def.getVisibility();
    		if (vis instanceof APublicVisibility){
    			cell.setImage(images.getImage(ISharedImages.IMG_OBJS_PUBLIC));
    		} else {
    			cell.setImage(images.getImage(ISharedImages.IMG_OBJS_PRIVATE));
    		}
    		ASignature sign = (ASignature)meth_def.getSignature();
    		printSignature(text, meth_def.getMethid().toString(), sign);		    	  
    	}
    	else if (element instanceof ADeferredMethPropdef){
    		ADeferredMethPropdef meth_def = (ADeferredMethPropdef)element;
    		PVisibility vis = meth_def.getVisibility();
    		if (vis instanceof APublicVisibility){
    			cell.setImage(images.getImage(ISharedImages.IMG_OBJS_PUBLIC));
    		} else {
        		cell.setImage(images.getImage(ISharedImages.IMG_OBJS_PRIVATE));
    		}
  		  ASignature sign = (ASignature)meth_def.getSignature();
  		  printSignature(text, meth_def.getMethid().toString(), sign);	
    	}    	
    }
    cell.setText(text.toString());
    cell.setStyleRanges(text.getStyleRanges());
    super.update(cell);
  }
  
  public void printSignature(StyledString out, String name, ASignature signature){
	  out.append(name);
	  //method entry params
	  LinkedList<PParam> inputs = signature.getParams();
	  if(!inputs.isEmpty()){
		  StringBuffer buf = new StringBuffer();
		  buf.append('(');
		  for(PParam input : inputs)
		  {
			  buf.append(((AParam)input).getType().toString() + ", ");
		  }
		  buf.replace(buf.length()-3, buf.length()-1, ")");
		  out.append(buf.toString());
	  } 
	  //method output param
	  PType output = signature.getType();
	  if(output != null) out.append(" : " + output.toString(), StyledString.COUNTER_STYLER);
  }
}