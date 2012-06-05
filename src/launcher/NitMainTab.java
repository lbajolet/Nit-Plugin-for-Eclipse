package launcher;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.internal.ui.DebugUIPlugin;
import org.eclipse.debug.internal.ui.SWTFactory;
import org.eclipse.debug.internal.ui.launchConfigurations.LaunchConfigurationsMessages;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.swt.SWT;
import org.eclipse.swt.accessibility.AccessibleAdapter;
import org.eclipse.swt.accessibility.AccessibleEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;
import org.eclipse.ui.dialogs.ResourceSelectionDialog;

/**
 * @author lucas The main tab, contains all the arguments and targets for
 *         compilation
 */
public class NitMainTab extends AbstractLaunchConfigurationTab {

	private final String SHARED_LAUNCH_CONFIGURATON_DIALOG = IDebugUIConstants.PLUGIN_ID
			+ ".NIT_COMPILER_OUTPUT_PATH_FOLDER";

	public static final String OUTPUT_PATH = "outputPath";

	public static final String TARGET_FILE_PATH = "targetPath";

	public static final String EXECUTION_ARGUMENTS = "executionArgs";

	public static final String COMPILATION_ARGUMENTS = "compilationArguments";

	private Button registerOutPath;
	private Text registerOutPathVisualization;

	private Text registerTargetPathVisualization;
	private Button registerTargetPath;

	private Text registerArgumentsForCompiling;

	private Text registerArgumentsForExecution;

	/**
	 * Modify listener that simply updates the owning launch configuration
	 * dialog.
	 */
	private ModifyListener fBasicModifyListener = new ModifyListener() {
		public void modifyText(ModifyEvent evt) {
			scheduleUpdateJob();
		}
	};

	/**
	 * Convenience method for getting the workspace root.
	 */
	private IWorkspaceRoot getWorkspaceRoot() {
		return ResourcesPlugin.getWorkspace().getRoot();
	}

	/**
	 * @return the {@link IDialogSettings} to pass into the
	 *         {@link ContainerSelectionDialog}
	 * @since 3.6
	 */
	IDialogSettings getDialogBoundsSettings(String id) {
		IDialogSettings settings = DebugUIPlugin.getDefault()
				.getDialogSettings();
		IDialogSettings section = settings.getSection(id);
		if (section == null) {
			section = settings.addNewSection(id);
		}
		return section;
	}

	/**
	 * Handles the shared location button being selected
	 */
	private void handleOutputPathLocationButtonSelected() {
		DirectoryDialog dialog = new DirectoryDialog(getShell());
		dialog.open();
		this.registerOutPathVisualization.setText(dialog.getFilterPath());

	}

	/**
	 * Handles the shared location button being selected
	 */
	private void handleTargetPathLocationButtonSelected() {
		ResourceSelectionDialog dialog = new ResourceSelectionDialog(
				getShell(), getWorkspaceRoot(),
				SHARED_LAUNCH_CONFIGURATON_DIALOG);
		dialog.open();
		Object[] results = dialog.getResult();
		for (Object res : results) {
			if (res instanceof IFile) {
				IFile fichier = (IFile) res;
				this.registerTargetPathVisualization.setText(fichier
						.getFullPath().toString());
			}
		}
	}

	@Override
	public void createControl(Composite parent) {
		Composite comp = new Composite(parent, SWT.NONE);
		setControl(comp);
		PlatformUI.getWorkbench().getHelpSystem()
				.setHelp(getControl(), getHelpContextId());
		comp.setLayout(new GridLayout(2, true));
		comp.setFont(parent.getFont());

		createOutFolderComposite(comp);
		createArgumentsFolder(comp);
	}

	/**
	 * Creates the compiling properties component
	 * 
	 * @param parent
	 *            the parent composite to add this component to
	 * @since 3.2
	 */
	private void createOutFolderComposite(Composite parent) {
		Group group = SWTFactory.createGroup(parent, "Compiling Properties", 3,
				2, GridData.FILL_HORIZONTAL);
		Composite comp = SWTFactory.createComposite(group, parent.getFont(), 3,
				3, GridData.FILL_HORIZONTAL, 0, 0);
		Composite comp2 = SWTFactory.createComposite(group, parent.getFont(),
				3, 3, GridData.FILL_HORIZONTAL, 0, 0);
		GridData gd = new GridData();
		gd.horizontalSpan = 3;

		// SAVE TO BLOCK //

		Label saveToLabel = new Label(comp, 0);
		saveToLabel.setText("Save To");

		registerOutPathVisualization = SWTFactory.createSingleText(comp, 1);
		registerOutPathVisualization.getAccessible().addAccessibleListener(
				new AccessibleAdapter() {
					public void getName(AccessibleEvent e) {
						e.result = LaunchConfigurationsMessages.CommonTab_S_hared_4;
					}
				});
		registerOutPathVisualization.addModifyListener(fBasicModifyListener);

		registerOutPath = createPushButton(comp, "Browse...", null);
		registerOutPath.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent evt) {
				handleOutputPathLocationButtonSelected();
			}
		});

		// END SAVE TO BLOCK //

		// CHOOSE FILE TO TARGET //

		Label targetFile = new Label(comp2, 0);
		targetFile.setText("Target File");

		registerTargetPathVisualization = SWTFactory.createSingleText(comp2, 1);
		registerTargetPathVisualization.getAccessible().addAccessibleListener(
				new AccessibleAdapter() {
					public void getName(AccessibleEvent e) {
						e.result = LaunchConfigurationsMessages.CommonTab_S_hared_4;
					}
				});
		registerTargetPathVisualization.addModifyListener(fBasicModifyListener);

		registerTargetPath = createPushButton(comp2, "Browse...", null);
		registerTargetPath.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent evt) {
				handleTargetPathLocationButtonSelected();
			}
		});

		// END CHOOSE FILE TO TARGET //
	}

	private void createArgumentsFolder(Composite parent) {
		Group group = SWTFactory.createGroup(parent, "Arguments", 3, 2,
				GridData.FILL_HORIZONTAL);
		Composite comp = SWTFactory.createComposite(group, parent.getFont(), 2,
				3, GridData.FILL_HORIZONTAL, 0, 0);
		Composite comp2 = SWTFactory.createComposite(group, parent.getFont(),
				2, 3, GridData.FILL_HORIZONTAL, 0, 0);
		GridData gd = new GridData();
		gd.horizontalSpan = 2;

		Label registerArgsCompiling = new Label(comp, 0);
		registerArgsCompiling.setText("Arguments For Compiling");

		this.registerArgumentsForCompiling = SWTFactory.createSingleText(comp,
				0);
		this.registerArgumentsForCompiling
				.addModifyListener(fBasicModifyListener);

		Label registerArgsExec = new Label(comp2, 0);
		registerArgsExec.setText("Arguments For Execution");

		this.registerArgumentsForExecution = SWTFactory.createSingleText(comp2,
				0);
		this.registerArgumentsForExecution
				.addModifyListener(fBasicModifyListener);
	}

	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {

	}

	@Override
	public void initializeFrom(ILaunchConfiguration configuration) {
		try {
			if (!configuration.getAttribute(OUTPUT_PATH, "").equals("")) {
				this.registerOutPathVisualization.setText(configuration
						.getAttribute(OUTPUT_PATH, ""));
			}
			if (!configuration.getAttribute(TARGET_FILE_PATH, "").equals("")) {
				this.registerTargetPathVisualization.setText(configuration
						.getAttribute(TARGET_FILE_PATH, ""));
			}
			if (!configuration.getAttribute(COMPILATION_ARGUMENTS, "").equals(
					"")) {
				this.registerArgumentsForCompiling.setText(configuration
						.getAttribute(COMPILATION_ARGUMENTS, ""));
			}
			if (!configuration.getAttribute(EXECUTION_ARGUMENTS, "").equals("")) {
				this.registerArgumentsForExecution.setText(configuration
						.getAttribute(EXECUTION_ARGUMENTS, ""));
			}
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		if (!this.registerOutPathVisualization.getText().equals("")) {
			File outputDir = new File(
					this.registerOutPathVisualization.getText());
			if (outputDir.exists() && outputDir.isDirectory())
				configuration.setAttribute(OUTPUT_PATH,
						this.registerOutPathVisualization.getText());
		}
		if (!this.registerTargetPathVisualization.getText().equals("")) {
			configuration.setAttribute(TARGET_FILE_PATH,
					this.registerTargetPathVisualization.getText());
		}
		if (!this.registerArgumentsForCompiling.getText().equals("")) {
			configuration.setAttribute(COMPILATION_ARGUMENTS,
					this.registerArgumentsForCompiling.getText());
		}
		if (!this.registerArgumentsForExecution.getText().equals("")) {
			configuration.setAttribute(EXECUTION_ARGUMENTS,
					registerArgumentsForExecution.getText());
		}
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

}
