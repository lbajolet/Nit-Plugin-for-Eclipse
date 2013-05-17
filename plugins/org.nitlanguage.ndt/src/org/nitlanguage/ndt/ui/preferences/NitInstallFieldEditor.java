package org.nitlanguage.ndt.ui.preferences;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.nitlanguage.ndt.core.plugin.NitActivator;
import org.nitlanguage.ndt.core.plugin.NitInstallation;
import org.nitlanguage.ndt.ui.UiMsg;

/**
 * DirectoryFieldEditor having a special treatment done when
 *         changing the value (auto-updates the values of the libFieldEditor
 *         and fileFieldEditor to the right values if valid
 * @author lucas.bajolet
 * @author nathan.heu
 */
public class NitInstallFieldEditor{

	List<NitInstallEntity> items = new ArrayList<NitInstallEntity>();
	NitInstallEntity root;
	NitInstallation infos;
	Composite parent;
	Label lblDescription;
	Tree trComponents;
	Button btnLocation;
	
	/**
	 * Control to select the root folder
	 */
	private RootFieldEditor rootEditor;
	
	/**
	 * Constructor
	 * @param parent
	 */
	public NitInstallFieldEditor(Composite parent) {
		//super("NitFolder", UiMsg.LBL_NITROOT_LOCATION, parent);
		this.parent = parent;
		initData();
		buildUI();
		initTree();
	}
	
	/**
	 * Loads informations from preference store
	 * Initializes baking fields
	 */
	public void initData(){
		//load infos from preferences store
		infos = NitActivator.getDefault().getNitInstallation();
		root = new NitInstallEntity(UiMsg.ID_ROOT, UiMsg.NAME_ROOT, null, infos.getRoot(), true);		
		items.add(new NitInstallEntity(UiMsg.ID_STDLIB, UiMsg.NAME_STDLIB , UiMsg.DESC_STDLIB, infos.getStdLib(), true));
		items.add(new NitInstallEntity(UiMsg.ID_COMPILER, UiMsg.NAME_COMPILER, UiMsg.DESC_COMPILER, infos.getCompiler(), false));
		items.add(new NitInstallEntity(UiMsg.ID_INTERPRETER, UiMsg.NAME_INTERPRETER, UiMsg.DESC_INTERPRETER, infos.getInterpreter(), false));
		items.add(new NitInstallEntity(UiMsg.ID_DEBUGGER, UiMsg.NAME_DEBUGGER, UiMsg.DESC_DEBUGGER, infos.getDebugger(), false));
	}
	
	/**
	 * Builds ui with baking fields informations
	 */
	public void buildUI(){	        
		//Layout which manages controls of the page : 2 columns, 4 lines
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		gridLayout.makeColumnsEqualWidth = true;
		parent.setLayout(gridLayout);
		
		//Line 1, Column 1 & 2 : wrRootEditor
		Composite wrRootEditor = new Composite(parent, SWT.NONE);
		rootEditor = new RootFieldEditor(this, root.getId(), root.getName(), wrRootEditor);		
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.horizontalSpan = 2;
		wrRootEditor.setLayoutData(gridData);
		
		//Line 2, Column 1 : lblComponent
		Label lblComponent = new Label(parent, SWT.NONE);
		lblComponent.setText("Component :");
		
		//Line 2, Column 2 : lblDetails
		Label lblDetails = new Label(parent, SWT.NONE);
		lblDetails.setText("Description :");
		
		//Line 3 et 4, Column 1 : trComponents
		trComponents = new Tree(parent, SWT.CHECK | SWT.BORDER);
		trComponents.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 2));
		
		trComponents.addListener(SWT.Selection, new Listener() {
 			public void handleEvent(Event event) {
 				if(event.item instanceof TreeItem){
 					TreeItem ti = (TreeItem)event.item;
 					if(ti.getData() != null){
 						if (event.detail == SWT.CHECK){
 							if(!((NitInstallEntity)ti.getData()).isPathValid()){
 	 	 		 				((TreeItem)event.item).setChecked(false);
 	 	 					}
 	 	 				} else {
 	 		 				updateDescription((NitInstallEntity)ti.getData());	
 	 	 				}
 					}
 				} 
			}
		});
		
		//Line 3, Column 2 : lblDescription
		lblDescription = new Label(parent, SWT.BORDER | SWT.LEFT  | SWT.WRAP );
		gridData = new GridData();
		gridData.horizontalAlignment = SWT.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.verticalAlignment = SWT.FILL;
		gridData.grabExcessVerticalSpace = true;
		lblDescription.setLayoutData(gridData);	
		
		//Line 4, Column 2 : btnLocation	
		btnLocation = new Button(parent, SWT.PUSH);
		btnLocation.setText("Edit location");
	    gridData = new GridData();
		gridData.horizontalAlignment = SWT.RIGHT;
		btnLocation.setLayoutData(gridData);
		btnLocation.addSelectionListener(new SelectionListener() {		
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(trComponents.getSelection().length > 0){
					TreeItem ti = trComponents.getSelection()[0];
					if(ti.getData() instanceof NitInstallEntity){
						NitInstallEntity entity = (NitInstallEntity) ti.getData();
						if(entity.isDirectory()){
							DirectoryDialog dlg = new DirectoryDialog(parent.getShell());
							dlg.setFilterPath(root.getPath());
							entity.setPath(dlg.open());
						} else {
							FileDialog flg = new FileDialog(parent.getShell());
							flg.setFilterPath(root.getPath());
							entity.setPath(flg.open());
						}
						updateTreeItem(ti);
						updateDescription(entity);
					}
				}
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub				
			}
		});
	}
	
	/**
	 * Deduces paths 
	 */
	public void resolveFields(String rootPath){
		root.generatePath(rootPath);
		for(NitInstallEntity item : items){
			item.generatePath(rootPath);
		}
		updateTree();
	}
	
	/**
	 * Initializes the tree items
	 */
	public void initTree(){
		if(root.isPathValid()){
			rootEditor.setStringValue(root.getPath());
		}
		boolean firstEntry = true;
		for(NitInstallEntity item : items){
			TreeItem ti = new TreeItem(trComponents, SWT.NONE);
			ti.setText(item.getName());
			ti.setData(item);
			if(item.isPathValid()){
				ti.setChecked(true);
			}
			if(firstEntry){
				trComponents.setSelection(ti);
				updateDescription(item);
				firstEntry = false;
			}
		}
	}
	
	/**
	 * Updates the state of the whole tree
	 */
	public void updateTree(){
		for(TreeItem ti : trComponents.getItems())
			updateTreeItem(ti);
		trComponents.setSelection(trComponents.getItem(0));
	}
	
	/**
	 * Updates the state of one item of the three
	 * @param ti
	 * @param entity
	 */
	public void updateTreeItem(TreeItem ti){
		if(ti.getData() instanceof NitInstallEntity){
			if(((NitInstallEntity)ti.getData()).isPathValid())
				ti.setChecked(true);
			else ti.setChecked(false);
		}
	}
	
	/**
	 * Update the details corresponding to the selected item in the tree
	 */
	private void updateDescription(NitInstallEntity item){
		StringBuilder bldr = new StringBuilder();
		bldr.append(item.getDescription());
		if(item.isPathValid()){		
			bldr.append("\n\nLocation : " + item.getPath());
			btnLocation.setToolTipText(item.getPath());
		} else {
			btnLocation.setToolTipText("");
		}
		lblDescription.setText(bldr.toString());
	}
	
	/**
	 * Clears ui elements and baking structures
	 */
	public void clear(){
		//preference store
		infos.clear();
		//baking fields
		items = new ArrayList<NitInstallEntity>();
		//ui
		trComponents.clearAll(true);
		lblDescription.setText("");
	}
	
	/**
	 * Propagates baking field changes in preference store
	 */
	public void save(){
		infos.setRoot(root.getPath());
		for(NitInstallEntity item : items){
			infos.set(item.getId(), item.getPath());
		}
		infos.save();	
	}
}

class RootFieldEditor extends DirectoryFieldEditor{
	private NitInstallFieldEditor fe;
	
	public RootFieldEditor(NitInstallFieldEditor fe, String name, String labelText, Composite parent){
		super(name, labelText, parent);
		this.fe = fe;
	}
	
	@Override
	/**
	 * Notifies that this field editor's change button has been pressed.
	 */
	protected String changePressed() {
		String dirName = super.changePressed();
		if(dirName != null && isValid()){
			fe.resolveFields(dirName);
		}
		return dirName;
	}
	
}

class NitInstallEntity {
	private String id;
	private String name;
	private String description;
	private boolean isDir;
	private String path;
	private boolean isValid = false;
	
	public NitInstallEntity(String id, String name, String description){
		this.id = id;
		this.name = name;
		this.description = description;	
	}
	
	public NitInstallEntity(String id, String name, String description, String path, boolean isDirectory){
		this(id, name, description);
		isDir = isDirectory;
		//avant de set, vérifier que le path renseigné correspond a ce qu'il devrai être
		//et accord isValid en		setPath(rootPath + id); conséquence.
		setPath(path);
	}
	
	public String getId(){
		return id;
	}
	
	public String getName(){
		return name;
	}
	
	public String getDescription(){
		return description;
	}
	
	public boolean isDirectory(){
		return isDir;
	}
	
	public String getPath(){
		return path;
	}
	
	public void setPath(String path){
		this.path = path;
		if(path!= null) this.isValid = true;
	}
	
	public void generatePath(String rootPath){
		String sep = NitActivator.getFileSeparator();
		StringBuilder bldr = new StringBuilder(rootPath + sep);
		
		if(id.equals(NitInstallation.ROOT_PATH_ID)){
			//bldr.append(sep);
		} else if(id.equals(NitInstallation.STDLIB_FOLDER_ID)){
			bldr.append("lib").append(sep).append("standard").append(sep);
		} else {
			bldr.append("bin").append(sep).append(id);
		}
		
		setPath(bldr.toString());
	}
	
	public boolean isPathValid(){
		return isValid;
	}
}
