package org.nitlanguage.ndt.ui.outline;

import java.util.LinkedList;

import org.eclipse.jdt.ui.ISharedImages;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.ViewerCell;
import org.nitlanguage.gen.node.AAttrPropdef;
import org.nitlanguage.gen.node.ACharExpr;
import org.nitlanguage.gen.node.AConcreteInitPropdef;
import org.nitlanguage.gen.node.AConcreteMethPropdef;
import org.nitlanguage.gen.node.ADeferredMethPropdef;
import org.nitlanguage.gen.node.AFalseExpr;
import org.nitlanguage.gen.node.AFloatExpr;
import org.nitlanguage.gen.node.AFormaldef;
import org.nitlanguage.gen.node.AIntExpr;
import org.nitlanguage.gen.node.AInterfaceClasskind;
import org.nitlanguage.gen.node.AModule;
import org.nitlanguage.gen.node.AModuledecl;
import org.nitlanguage.gen.node.ANewExpr;
import org.nitlanguage.gen.node.AParam;
import org.nitlanguage.gen.node.APublicVisibility;
import org.nitlanguage.gen.node.ASignature;
import org.nitlanguage.gen.node.AStdClassdef;
import org.nitlanguage.gen.node.AStringExpr;
import org.nitlanguage.gen.node.ATrueExpr;
import org.nitlanguage.gen.node.AType;
import org.nitlanguage.gen.node.PClasskind;
import org.nitlanguage.gen.node.PFormaldef;
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
  private String nullableChar = "\u2205";
  
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
		if(mod_dec != null) {
	    	text.append(mod_dec.getName().toString());  
		} else {
	    	text.append(editor.getCurrentFile().getName().replace(".nit", ""));  
		}
 	
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

		  LinkedList<PFormaldef> listFormalParams = class_def.getFormaldefs();
		  if(listFormalParams.size() > 0){
				StringBuffer buf = new StringBuffer();
				buf.append("[");
				for(PFormaldef type : listFormalParams){
					buf.append(((AFormaldef)type).getId() + ", ");
				}
				buf.replace(buf.length()-3, buf.length()-1, "]");
				text.append(buf.toString(), StyledString.COUNTER_STYLER);
		  }
    }
    else if (element instanceof PPropdef) {
    	if (element instanceof AAttrPropdef){
    		AAttrPropdef attr_def = (AAttrPropdef)element;
    		PVisibility vis = attr_def.getVisibility();
    		if (vis instanceof APublicVisibility){
    			//ne devrait pas être possible - pas d'attributs publics en nit
    			cell.setImage(images.getImage(ISharedImages.IMG_FIELD_PUBLIC));
    		} else {
    			cell.setImage(images.getImage(ISharedImages.IMG_FIELD_PRIVATE));
    		}	 
    		//attribut name
    		String attr_name;
    		if(attr_def.getId2() != null) {
    			attr_name = attr_def.getId2().toString();
    		} else {
    			attr_name = attr_def.getId().toString();
    			cell.setImage(images.getImage(ISharedImages.IMG_FIELD_PRIVATE));
    		}
    		text.append(attr_name);
    		AType attr_type = (AType)attr_def.getType();
    		
    		//typage sans affectation de valeur (: Int, : Bool)
    		if(attr_type != null){
        		if(attr_type.getKwnullable() != null){
        			text.append(": " + nullableChar, StyledString.COUNTER_STYLER);    			
        		} else {
            		text.append(": ", StyledString.COUNTER_STYLER);
        		}
        		text.append(attr_type.getId().toString(), StyledString.COUNTER_STYLER);
        		LinkedList<PType> listTypes = attr_type.getTypes();
        		if(listTypes != null && listTypes.size() > 0){
        			StringBuffer buf = new StringBuffer();
        			buf.append("[");
            		for(PType type : listTypes){
            			buf.append(((AType)type).getId() + ", ");
            		}
            		buf.replace(buf.length()-3, buf.length()-1, "]");
            		text.append(buf.toString(), StyledString.COUNTER_STYLER);
        		}
    		}
    		//typage avec affectation de valeur ( = 1, = true, = new String)
    		else {
    			if(attr_def.getExpr() instanceof AFalseExpr
    					|| attr_def.getExpr() instanceof ATrueExpr) {
    				text.append(": Bool" , StyledString.COUNTER_STYLER);
    			} else if(attr_def.getExpr() instanceof AIntExpr) {
    				text.append(": Int" , StyledString.COUNTER_STYLER);
    			} else if(attr_def.getExpr() instanceof AFloatExpr) {
    				text.append(": Float" , StyledString.COUNTER_STYLER);
    			} else if(attr_def.getExpr() instanceof ACharExpr) {
    				text.append(": Char" , StyledString.COUNTER_STYLER);
    			} else if(attr_def.getExpr() instanceof AStringExpr) {
    				text.append(": String" , StyledString.COUNTER_STYLER);
    			} else if(attr_def.getExpr() instanceof ANewExpr) {
    				ANewExpr expr = (ANewExpr)attr_def.getExpr();
    				AType var_type = (AType)expr.getType();
    				text.append(": " + var_type.getId(), StyledString.COUNTER_STYLER);
            		LinkedList<PType> listTypes = var_type.getTypes();
            		if(listTypes != null && listTypes.size() > 0){
            			StringBuffer buf = new StringBuffer();
            			buf.append("[");
                		for(PType type : listTypes){
                			buf.append(((AType)type).getId() + ", ");
                		}
                		buf.replace(buf.length()-3, buf.length()-1, "]");
                		text.append(buf.toString(), StyledString.COUNTER_STYLER);
            		}
    			} else {
        			text.append(": " + "undefine inferrence");
    			}
    		}

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
			  AParam ainput = (AParam)input;
      		  if(((AType)ainput.getType()).getKwnullable() != null){
      			  buf.append(nullableChar);    			
				  /*AType atype = ((AType)ainput.getType());
				  LinkedList<PType> listformalTypes = atype.getTypes();    		
	      		  if(listformalTypes != null && listformalTypes.size() > 0){
	      			buf.append("[");
	          		for(PType type : listformalTypes){
	          			buf.append(((AType)type).getId() + ", ");
	          		}
	          		buf.replace(buf.length()-3, buf.length()-1, "]");
	      		  }      		  */
      		  }
      		  buf.append(((AParam)input).getType().toString() + ", ");	  
		  }
		  buf.replace(buf.length()-3, buf.length()-1, ")");
		  out.append(buf.toString());
	  } 
	  //method output param
	  PType output = signature.getType();
	  if(output != null){
		  evaluateType(output, out);
	  }
  }
  
  private void evaluateType(PType type, StyledString out){
	  out.append(" : ", StyledString.COUNTER_STYLER);
	  if(type instanceof AType && ((AType)type).getKwnullable() != null){
			  out.append(nullableChar, StyledString.COUNTER_STYLER);   
	  } 
	  out.append(((AType)type).getId().getText(), StyledString.COUNTER_STYLER);
  }
}
