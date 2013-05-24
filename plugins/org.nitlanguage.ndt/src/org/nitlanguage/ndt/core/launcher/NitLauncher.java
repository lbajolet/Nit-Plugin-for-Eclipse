package org.nitlanguage.ndt.core.launcher;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate;
import org.nitlanguage.ndt.core.BuildMsg;
import org.nitlanguage.ndt.core.PluginParams;
import org.nitlanguage.ndt.core.builder.NitNature;
import org.nitlanguage.ndt.core.plugin.NitActivator;
import org.nitlanguage.ndt.ui.console.NitConsole;

/**
 * The launcher class, auto-compiles and executes a nit program
 * with the selected configuration
 * @author lucas.bajolet 
 * @author nathan.heu 
 */
public class NitLauncher implements ILaunchConfigurationDelegate {
	/**
	 * The background job doing the compiling + Executing processes
	 * @author lucas.bajolet 
	 */
	private class ExecJob extends Job {

		/**
		 * @param name
		 *            Default constructor for a Job, only does the Super part
		 */
		public ExecJob(String name) {
			super(name);
		}

		/**
		 * Arguments for executing the compiled binaries
		 */
		private String argsForExec;
		/**
		 * Path to the file to compile
		 */
		private String pathToFile;
		/**
		 * NitNature, contains the compilerCallerClass
		 */
		private NitNature nnat;

		public void setArgsForExec(String args) {
			this.argsForExec = args;
		}

		public void setPathToFile(String path) {
			this.pathToFile = path;
		}

		public void setNature(NitNature nnat) {
			this.nnat = nnat;
		}

		@Override
		protected IStatus run(IProgressMonitor monitor) {
			monitor.beginTask(BuildMsg.EXECUTION_TASK, 100);
			// Get arguments for execution
			try {
				if (nnat != null && nnat.getCompilerCaller() != null
						&& nnat.getCompilerCaller().getCompileJob() != null) {

					while (!nnat.getCompilerCaller().getCompileJob().isOver()) {
						if (monitor.isCanceled()) {
							monitor.done();
							break;
						}
						Thread.sleep(100);
					}

					monitor.worked(50);

					File toExec = new File(pathToFile);

					int countBeforeLeaving = 0;

					if (toExec.exists() && toExec.canExecute()
							&& toExec.isFile()) {
						Process execute = Runtime.getRuntime().exec(
								pathToFile + argsForExec);
						boolean leaveOnNextIter = false;
						BufferedReader buf = new BufferedReader(
								new InputStreamReader(execute.getInputStream()));
						BufferedReader errBuf = new BufferedReader(
								new InputStreamReader(execute.getErrorStream()));
						// BufferedWriter inputBuf = new BufferedWriter(
						// new OutputStreamWriter(
						// execute.getOutputStream()));
						while (!leaveOnNextIter) {
							Thread.sleep(200);
							if (monitor.isCanceled()) {
								execute.destroy();
								monitor.done();
							}
							try {
								execute.exitValue();
								if (countBeforeLeaving >= 4)
									leaveOnNextIter = true;
								countBeforeLeaving++;
							} catch (Exception e) {
								if (NitActivator.DEBUG_MODE)
									e.printStackTrace();
							}
							char[] buffer = new char[2048];
							while (buf.ready()) {
								countBeforeLeaving = 0;
								buf.read(buffer);
								NitConsole.getInstance().write(
										String.copyValueOf(buffer));
							}
							while (errBuf.ready()) {
								countBeforeLeaving = 0;
								errBuf.read(buffer);
								NitConsole.getInstance().write(
										String.copyValueOf(buffer));
							}
						}
					}
				}
			} catch (IOException e) {
				if (NitActivator.DEBUG_MODE)
					e.printStackTrace();
			} catch (Exception e) {
				if (NitActivator.DEBUG_MODE)
					e.printStackTrace();
			}
			monitor.worked(50);
			monitor.done();

			return Status.OK_STATUS;
		}
	}

	@Override
	public void launch(ILaunchConfiguration configuration, String mode,
			ILaunch launch, IProgressMonitor monitor) throws CoreException {
		monitor.beginTask(BuildMsg.LAUNCHING_TASK, 100);

		if (mode.equals("run")) {
			// Get the file to compile
			String fileName = configuration.getAttribute(
					NitMainTab.TARGET_FILE_PATH, "");
			IFile fichier = null;
			if (!fileName.equals("")) {
				fichier = ResourcesPlugin.getWorkspace().getRoot()
						.getFile(new Path(fileName));
			}
			if (fichier != null) {

				// Get project
				IProject proj = fichier.getProject();

				// Then get the nature of the project
				NitNature nnat = (NitNature) proj
						.getNature(PluginParams.NATURE_ID);

				// Call the compiler with this information
				nnat.setDefaultFile(fichier);
				nnat.getCompilerCaller().setOutFolder(
						configuration.getAttribute(NitMainTab.OUTPUT_PATH, ""));
				monitor.worked(10);

				String[] file = configuration.getAttribute(
						NitMainTab.TARGET_FILE_PATH, "").split("/");
				String pathToFile = configuration.getAttribute(
						NitMainTab.OUTPUT_PATH, "")
						+ "/"
						+ file[file.length - 1].substring(0,
								file[file.length - 1].length() - 4);

				// add arguments for compiling
				String attributesComp = configuration.getAttribute(
						NitMainTab.COMPILATION_ARGUMENTS, "");

				nnat.getCompilerCaller().setOptions(attributesComp);

				nnat.getCompilerCaller().call();
				monitor.worked(40);

				ExecJob ej = new ExecJob(BuildMsg.EXECUTION_JOB);

				ej.setArgsForExec(" "
						+ configuration.getAttribute(
								NitMainTab.EXECUTION_ARGUMENTS, "").trim());
				ej.setPathToFile(pathToFile);
				ej.setNature(nnat);

				ej.schedule();

				monitor.worked(50);
			}
		}

		monitor.done();
	}
}
