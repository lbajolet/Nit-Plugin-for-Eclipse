package launcher;

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

import plugin.NitActivator;
import builder.NitNature;
import console.NitConsole;

public class NitLauncher implements ILaunchConfigurationDelegate {

	private class ExecJob extends Job {

		public ExecJob(String name) {
			super(name);
		}

		private String argsForExec;
		private String pathToFile;
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
			// Get arguments for execution
			try {
				if (nnat.getCompilerCaller().getCompileJob() != null) {

					while(!nnat.getCompilerCaller().getCompileJob().isOver()){
						Thread.sleep(100);
					}
					
					File toExec = new File(pathToFile);
					if (toExec.exists() && toExec.canExecute()
							&& toExec.isFile()) {
						Process execute = Runtime.getRuntime().exec(
								pathToFile + argsForExec);
						boolean leaveOnNextIter = false;
						BufferedReader buf = new BufferedReader(
								new InputStreamReader(execute.getInputStream()));
						BufferedReader errBuf = new BufferedReader(
								new InputStreamReader(execute.getErrorStream()));
						while (!leaveOnNextIter) {
							try {
								execute.exitValue();
								leaveOnNextIter = true;
							} catch (Exception e) {
								if (NitActivator.DEBUG_MODE)
									e.printStackTrace();
							}
							String readLine = null;
							String readError = null;
							while ((readLine = buf.readLine()) != null
									|| (readError = errBuf.readLine()) != null) {
								if (readLine != null)
									NitConsole.getInstance().write(readLine);
								if (readError != null)
									NitConsole.getInstance().write(readError);
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

			return Status.OK_STATUS;
		}
	}

	@Override
	public void launch(ILaunchConfiguration configuration, String mode,
			ILaunch launch, IProgressMonitor monitor) throws CoreException {
		monitor.beginTask("Compile and Run Nit", 100);

		if (mode.equals("run")) {
			// Get file for this twat
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
						.getNature(NitNature.NATURE_ID);

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

				ExecJob ej = new ExecJob("Execution");

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
