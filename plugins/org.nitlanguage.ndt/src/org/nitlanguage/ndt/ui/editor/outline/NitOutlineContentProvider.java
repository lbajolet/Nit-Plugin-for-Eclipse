package org.nitlanguage.ndt.ui.editor.outline;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.nitlanguage.gen.node.AModule;
import org.nitlanguage.gen.node.AStdClassdef;
import org.nitlanguage.gen.node.PPropdef;
import org.nitlanguage.gen.node.Start;
import org.nitlanguage.ndt.core.asthelpers.AstParserHelper;
import org.nitlanguage.ndt.ui.editor.NitEditor;

/**
 * Provides class related informations for an outline using a tree viewer.
 * @author nathan.heu
 */
public class NitOutlineContentProvider implements ITreeContentProvider {
	  private NitEditor editor;

	  AModule module = null;
	  //Result sets
	  List<AStdClassdef> astd_classes;
	  AstParserHelper aph = new AstParserHelper();
	  
	  public NitOutlineContentProvider(NitEditor editor) {
		  this.editor = editor;
		  this.aph = new AstParserHelper(); 
	  }
	  
	  private boolean updateContent()
	  {      
		  Start st = aph.getAstForDocument(editor.getCurrentFile());
		  if(st != null){
			  try {
				  	module = aph.getModuleOfAST(st);
			  } catch (Exception e) {
					e.printStackTrace();
					return false;
			  }     
			  astd_classes = aph.getAStdClassesOfModule(module);
			  return true;
		  } else {
			  System.out.println("Outline update impossible : file parsing has failed");
			  return false;
		  }

	  }
	  
	  public Object[] combine(Object[] a, Object[] b)
	  {
		  Object[] tmp = new Object[a.length + b.length];
		  for(int i=0; i < a.length; i++){
			  tmp[i] = a[i];
		  }
		  for(int i=a.length; i < b.length + a.length; i++){
			  tmp[i] = b[i - a.length];
		  }
		  return tmp;
	  }
	  
	  //Props = méthodes + attributs 
	  public Object[] getMethodsOfClass(AStdClassdef class_def){
		  LinkedList<PPropdef> props = aph.getPropsOfClass(class_def);
		  return combine(aph.getConcreteMethsInPropList(props).toArray(), 
				  		 aph.getDeferredMethsInPropList(props).toArray());
	  }
	  
	  public Object[] getAttrsOfClass(AStdClassdef class_def){
		  LinkedList<PPropdef> props = aph.getPropsOfClass(class_def);
		  return aph.getNonMethPropsInPropList(props).toArray();
	  }
	  
	  public Object[] getPropsOfClass(AStdClassdef class_def){
		  return aph.getPropsOfClass(class_def).toArray();
	  }
	  
	  public Object[] combine(Object a, Object[] b)
	  {
		  Object[] tmp = new Object[1 + b.length];
		  tmp[0] = a;
		  for(int i=1; i < b.length + 1; i++){
			  tmp[i] = b[i - 1];
		  }
		  return tmp;
	  }
	  
	  @Override
	  //Called just for the first-level objects.
	  public Object[] getElements(Object inputElement) {
		  if(updateContent()){
			  return combine(module, astd_classes.toArray());
		  } else {
			  return new Object[]{};
		  }

	  }
	
	  @Override
	  //Queried to load the children of a given node
	  public Object[] getChildren(Object parentElement) {
		  if (parentElement.getClass() == AStdClassdef.class){
			  return getPropsOfClass((AStdClassdef)parentElement);
		  }
		  else return null;
	  }
	  
	  @Override
	  public Object getParent(Object element) {
		  //methods and properties
		  if (element instanceof PPropdef) {
			  return ((PPropdef)element).parent();
		  }
		  else return null;
	  }
	
	  @Override
	  //Queried to know if the current node has children
	  public boolean hasChildren(Object element) {
		  if (element instanceof AModule) {
			  return false;
		  }
		  else if (element instanceof AStdClassdef) {
			  return ((AStdClassdef)element).getPropdefs().size() > 0;
		  }
		  else if (element instanceof PPropdef) {
			  return false;
		  }
		  else return false;
	  }
	 
	  @Override
	  public void dispose() {
			// TODO Auto-generated method stub
			
	  }
		
	  @Override
	  public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			// TODO Auto-generated method stub
	  }
}