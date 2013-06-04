package org.nitlanguage.ndt.core.builder;

import java.util.ArrayList;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;
import org.nitlanguage.ndt.core.PluginParams;
import org.nitlanguage.ndt.core.asthelpers.AstReposit;
import org.nitlanguage.ndt.core.asthelpers.ProjectAutoParser;
import org.nitlanguage.ndt.core.plugin.NitActivator;
import org.nitlanguage.ndt.core.plugin.NitInstallation;
import org.nitlanguage.ndt.core.plugin.ProjectPropertiesHelper;

/**
 * 
 * @author lucas.bajolet
 */
public class NitNature implements IProjectNature {
	/**
	 * Project bound to a nature
	 */
	private IProject project;

	/**
	 * Custom class to help reading and writing in properties for this project
	 */
	private ProjectPropertiesHelper pph;

	/**
	 * File set as target
	 */
	private IFile defaultFile;

	/**
	 * The compiler caller object, used to call and monitor the compilation of
	 * the current project
	 */
	private NitCompilerCallerClass compilerCaller;

	/**
	 * Reposit of all the files of the project
	 */
	private AstReposit repo;

	/**
	 * Project Auto Parser, used to parse the files of a project and keep the
	 * AST Reposit up to date
	 */
	private ProjectAutoParser pap;

	/**
	 * Default constructor
	 */
	public NitNature() {
		this.compilerCaller = new NitCompilerCallerClass();
		this.repo = new AstReposit();
	}

	/**
	 * Gets the ASTReposit for this project nature
	 * 
	 * @return ASTReposit
	 */
	public AstReposit getAstReposit() {
		return repo;
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
			if (commands[i].getBuilderName().equals(PluginParams.BUILDER_ID)) {
				return;
			}
		}

		ICommand[] newCommands = new ICommand[commands.length + 1];
		System.arraycopy(commands, 0, newCommands, 0, commands.length);
		ICommand command = desc.newCommand();
		command.setBuilderName(PluginParams.BUILDER_ID);
		newCommands[newCommands.length - 1] = command;
		desc.setBuildSpec(newCommands);

		// Add the nature
		String[] natures = desc.getNatureIds();
		String[] newNatures = new String[natures.length + 1];
		System.arraycopy(natures, 0, newNatures, 0, natures.length);
		newNatures[natures.length] = PluginParams.NATURE_ID;
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
			if (commands[i].getBuilderName().equals(PluginParams.BUILDER_ID)) {
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
		// Try to auto parse every file of the project
		this.pap = new ProjectAutoParser();
		pap.setProject(this.project);
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
	 * Gets the properties helper object for this nature
	 * 
	 * @return ProjectPropertiesHelper
	 */
	public ProjectPropertiesHelper getPropertiesHelper() {
		return this.pph;
	}

	/**
	 * Gets the project auto parser helper for this nature
	 * 
	 * @return ProjectAutoParser
	 */
	public ProjectAutoParser getProjectAutoParser() {
		return this.pap;
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

	public String[] buildDebuggerCommand(String targetPath) {
		ArrayList<String> command = new ArrayList<String>();
		NitInstallation infos = NitActivator.getDefault().getNitInstallation();
		command.add(infos.getDebugger());
		command.add(targetPath);
		return command.toArray(new String[command.size()]);
	}
}
