package builder;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.osgi.service.prefs.Preferences;

import asthelpers.ProjectAutoParser;

public class NitNature implements IProjectNature {

	/**
	 * ID of this project nature
	 */
	public static final String NATURE_ID = "org.uqam.nit.ndt.nature";

	/**
	 * Project bound to a nature
	 */
	private IProject project;

	private IScopeContext projPref;

	/**
	 * File set as target
	 */
	private IFile defaultFile;

	/**
	 * The compiler caller object, used to call and monitor the compilation of
	 * the current project
	 */
	private NitCompilerCallerClass compilerCaller;

	public NitNature(){
		this.compilerCaller = new NitCompilerCallerClass();
		// Try to auto parse every file of the project
		ProjectAutoParser pap = new ProjectAutoParser();
		pap.setProject(this.project);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.resources.IProjectNature#configure()
	 */
	public void configure() throws CoreException {
		IProjectDescription desc = project.getDescription();
		ICommand[] commands = desc.getBuildSpec();

		for (int i = 0; i < commands.length; ++i) {
			if (commands[i].getBuilderName().equals(NitBuilder.BUILDER_ID)) {
				return;
			}
		}

		ICommand[] newCommands = new ICommand[commands.length + 1];
		System.arraycopy(commands, 0, newCommands, 0, commands.length);
		ICommand command = desc.newCommand();
		command.setBuilderName(NitBuilder.BUILDER_ID);
		newCommands[newCommands.length - 1] = command;
		desc.setBuildSpec(newCommands);

		// Add the nature
		String[] natures = desc.getNatureIds();
		String[] newNatures = new String[natures.length + 1];
		System.arraycopy(natures, 0, newNatures, 0, natures.length);
		newNatures[natures.length] = NitNature.NATURE_ID;
		desc.setNatureIds(newNatures);
		project.setDescription(desc, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.resources.IProjectNature#deconfigure()
	 */
	public void deconfigure() throws CoreException {
		IProjectDescription description = getProject().getDescription();
		ICommand[] commands = description.getBuildSpec();
		for (int i = 0; i < commands.length; ++i) {
			if (commands[i].getBuilderName().equals(NitBuilder.BUILDER_ID)) {
				ICommand[] newCommands = new ICommand[commands.length - 1];
				System.arraycopy(commands, 0, newCommands, 0, i);
				System.arraycopy(commands, i + 1, newCommands, i,
						commands.length - i - 1);
				description.setBuildSpec(newCommands);
				project.setDescription(description, null);
				return;
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.resources.IProjectNature#getProject()
	 */
	public IProject getProject() {
		return project;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.resources.IProjectNature#setProject(org.eclipse.core
	 * .resources.IProject)
	 */
	public void setProject(IProject project) {
		this.project = project;
		this.defaultFile = null;
	}

	/**
	 * Sets the file to be targeted by the compilation process
	 * 
	 * @param file
	 *            File of the project
	 */
	public void setDefaultFile(IFile file) {
		this.defaultFile = file;
		this.compilerCaller.setTarget(file);
	}

	/**
	 * Gets the default target file for compilation
	 * 
	 * @return The file set as target for compilation
	 */
	public IFile getDefaultFile() {
		return this.defaultFile;
	}

	/**
	 * Gets the compiler caller
	 * 
	 * @return The Compiler Caller object with the informations to compile the
	 *         current project
	 */
	public NitCompilerCallerClass getCompilerCaller() {
		return this.compilerCaller;
	}
}
