package org.nitlanguage.ndt.ui.editor.outline;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jdt.ui.ISharedImages;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.viewers.LabelProvider;
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
public class NitOutlineLabelProvider extends LabelProvider {
  private NitEditor editor;

  public NitOutlineLabelProvider(NitEditor editor) {
	  this.editor = editor;
  }

  /**
   * Returns the image associated with the element
   * This image will be shown in the TreeViewer of the Outline
   */
  public Image getImage(Object element) {
	  ISharedImages images = JavaUI.getSharedImages();
	  
	  if (element instanceof AModule) 
	  {
		  return images.getImage(ISharedImages.IMG_OBJS_PACKAGE);
	  }
	  else if (element instanceof AStdClassdef) 
	  {
		  PClasskind kind = ((AStdClassdef)element).getClasskind();
		  if(kind.getClass() == AInterfaceClasskind.class){
			  return images.getImage(ISharedImages.IMG_OBJS_INTERFACE);
		  }
		  return images.getImage(ISharedImages.IMG_OBJS_CLASS);
	  } 
	  else if (element instanceof AConcreteInitPropdef)
	  {
		  PVisibility vis = ((AConcreteInitPropdef)element).getVisibility();
		  if (vis instanceof APublicVisibility){
			  return images.getImage(ISharedImages.IMG_OBJS_PUBLIC);
		  }
		  return images.getImage(ISharedImages.IMG_OBJS_PRIVATE);
	  } 
	  else if (element instanceof AConcreteMethPropdef){
		  PVisibility vis = ((AConcreteMethPropdef)element).getVisibility();
		  if (vis instanceof APublicVisibility){
			  return images.getImage(ISharedImages.IMG_OBJS_PUBLIC);
		  }
		  return images.getImage(ISharedImages.IMG_OBJS_PRIVATE);
	  } 
	  else if (element instanceof ADeferredMethPropdef){
		  PVisibility vis = ((ADeferredMethPropdef)element).getVisibility();
		  if (vis instanceof APublicVisibility){
			  return images.getImage(ISharedImages.IMG_OBJS_PUBLIC);
		  }
		  return images.getImage(ISharedImages.IMG_OBJS_PRIVATE);
	  } 
	  else if (element instanceof AAttrPropdef) {
		  PVisibility vis = ((AAttrPropdef)element).getVisibility();
		  if (vis instanceof APublicVisibility){
			  return images.getImage(ISharedImages.IMG_FIELD_PUBLIC);
		  }
		  return images.getImage(ISharedImages.IMG_FIELD_PRIVATE);
	  } 
	  else return null;
  }

  /**
   * Returns the label associated with the element
   * This text will be printed in the TreeViewer of the Outline, to the right of the image
   */
  public String getText(Object element) {
	  if (element instanceof AModule){
		  AModuledecl mod_dec = (AModuledecl)((AModule)element).getModuledecl();
		  return mod_dec.getName().toString();
	  }
	  else if (element instanceof AStdClassdef){
		  return ((AStdClassdef)element).getId().getText();
	  }
	  else if (element instanceof AConcreteInitPropdef){
		  AConcreteInitPropdef init_def = (AConcreteInitPropdef)element;
		  if(init_def == null || init_def.getMethid() == null) return "init";
		  ASignature sign = (ASignature)init_def.getSignature();
		  return printSignature(init_def.getMethid().toString(), sign);	
	  }
	  else if (element instanceof AConcreteMethPropdef){
		  AConcreteMethPropdef method_def = (AConcreteMethPropdef)element;
		  ASignature sign = (ASignature)method_def.getSignature();
		  return printSignature(method_def.getMethid().toString(), sign);		  
	  }
	  else if (element instanceof ADeferredMethPropdef){
		  ADeferredMethPropdef method_def = (ADeferredMethPropdef)element;
		  ASignature sign = (ASignature)method_def.getSignature();
		  return printSignature(method_def.getMethid().toString(), sign);		
	  }
	  else if (element instanceof AAttrPropdef){
		  return ((AAttrPropdef)element).getId2().toString();
	  }
	  return "";
  }
  
  public String printSignature(String name, ASignature signature){
	  StringBuffer buf = new StringBuffer();
	  buf.append(name);
	  //method entry params
	  LinkedList<PParam> inputs = signature.getParams();
	  if(!inputs.isEmpty()){
		  buf.append('(');
		  for(PParam input : inputs)
		  {
			  buf.append(((AParam)input).getType().toString() + ", ");
		  }
		  buf.replace(buf.length()-3, buf.length()-1, ")");
	  } 
	  //method output param
	  PType output = signature.getType();
	  if(output != null) buf.append(" : " + output.toString());
	  return buf.toString();
  }
}