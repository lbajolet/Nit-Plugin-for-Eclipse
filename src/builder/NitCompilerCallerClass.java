package builder;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import plugin.NitActivator;
import asthelpers.ProjectAutoParser;

/**
 * @author r4pass Helper to call the Nit Compiler
 */
public class NitCompilerCallerClass {

	/**
	 * Path in the FileSystem to the compiler
	 */
	private String path;

	/**
	 * The Eclipse Job, automatically uses the Eclipse API to compile in
	 * background and analyse the responses of the compiler to generate
	 * error/warning markers in the editor
	 */
	private NitCompileJob eclipseJob;

	/**
	 * The target file for the compiler, it is bound to the IFile defaultFile of
	 * the NitNature
	 */
	private IFile target;

	private String outputFolder;

	private String options;

	/**
	 * @author r4pass The Job used to compile a Nit Project using the default
	 *         file set by the user
	 */
	public class NitCompileJob extends Job {

		private String path;
		private Process compileProcess;
		private Boolean isBeingCalled;
		private String returnMessage;

		public void cancelCurrentProcess() {
			if (isBeingCalled == true) {
				try {
					compileProcess.exitValue();
				} catch (Exception e) {
					compileProcess.destroy();
				}
				isBeingCalled = false;
			}
		}

		public Process getCurrentCompileProcess() {
			return this.compileProcess;
		}

		public void setPath(String path) {
			this.path = path;
		}

		public String getReturnMessage() {
			return this.returnMessage;
		}

		public NitCompileJob(String name) {
			super(name);
			isBeingCalled = false;
		}

		@Override
		protected IStatus run(IProgressMonitor monitor) {
			try {
				((NitNature) (target.getProject()
						.getNature(NitNature.NATURE_ID)))
						.getProjectAutoParser().setProject(target.getProject());
			} catch (CoreException e1) {
				if (NitActivator.DEBUG_MODE)
					e1.printStackTrace();
			}
			this.setPriority(BUILD);
			monitor.beginTask("Compiling Nit", 100);
			cancelCurrentProcess();
			if (path == null) {
				this.returnMessage = "Error : No file path specified";
				isBeingCalled = false;
				monitor.setCanceled(true);
				return Status.CANCEL_STATUS;
			} else {
				try {
					compileProcess = Runtime.getRuntime().exec(path);
					monitor.worked(20);

					isBeingCalled = true;
					try {
						int endCode = compileProcess.waitFor();
						if (endCode == -1) {
							this.returnMessage = "Error : Compilation aborted";
							isBeingCalled = false;
							monitor.setCanceled(true);
							return Status.CANCEL_STATUS;
						}
					} catch (InterruptedException e) {
						this.returnMessage = "Error : Compilation aborted";
						isBeingCalled = false;
						return Status.OK_STATUS;
					}
					monitor.worked(50);
					InputStream inpt = compileProcess.getErrorStream();
					Reader in = new InputStreamReader(inpt, "UTF-8");
					StringBuilder result = new StringBuilder();
					char[] buf = new char[0x10000];
					int read = 0;
					do {
						read = in.read(buf, 0, buf.length);
						if (read > 0) {
							result.append(buf, 0, read);
						}
					} while (read >= 0);

					isBeingCalled = false;

					// Add markers to files returned as errors/warnings by
					// compiler call
					ProjectAutoParser pap2 = new ProjectAutoParser();
					NitCompilerMessageInterpreter ncmi = new NitCompilerMessageInterpreter();
					ncmi.addMessagesToProblems(
							ncmi.processMessagesOfCompiler(result.toString()),
							target,
							pap2.buildFilesInProjectRepo(target.getProject()));

					monitor.worked(20);

					monitor.worked(10);

					this.returnMessage = "All OK";

					monitor.done();
					return Status.OK_STATUS;
				} catch (IOException e) {
					this.returnMessage = "Error : Binary cannot be found at specified path";
					isBeingCalled = false;
					monitor.setCanceled(true);
					monitor.done();
					return Status.OK_STATUS;
				} catch (Exception e) {
					e.printStackTrace();
					monitor.setCanceled(true);
					monitor.done();
					return Status.OK_STATUS;
				}
			}
		}
	}

	/**
	 * 
	 */
	public NitCompilerCallerClass() {
		this.options = "";
	}

	public NitCompileJob getCompileJob() {
		return this.eclipseJob;
	}

	/**
	 * @param file
	 */
	public void setTarget(IFile file) {
		this.target = file;
	}

	/**
	 * @param newOpt
	 */
	public void setOptions(String newOpt) {
		this.options = newOpt;
	}

	/**
	 * @param newPath
	 */
	public void setPath(String newPath) {
		this.path = newPath;
	}

	public void setOutFolder(String folder) {
		this.outputFolder = folder;
	}

	public boolean checkIfPathToCompilerIsValid() {
		if (this.path != null) {
			File compiler = new File(this.path);
			if (compiler.exists() && compiler.canExecute() && compiler.isFile())
				return true;
		}
		NitActivator
				.getDefault()
				.getLog()
				.log(new Status(
						Status.ERROR,
						"Error with nit compiler",
						"Nit compiler cannot be found or cannot be run, are you sure the path you have set is valid ?"));
		return false;
	}

	/**
	 * 
	 */
	public void call() {
		if (!NitActivator.getDefault().getPreferenceStore()
				.getString(NitActivator.COMPILER_PATH_PREFERENCES_ID)
				.equals("")) {
			setPath(NitActivator.getDefault().getPreferenceStore()
					.getString(NitActivator.COMPILER_PATH_PREFERENCES_ID));
		}
		if (this.path != null && checkIfPathToCompilerIsValid()
				&& this.target != null) {
			// String completeCommand = "cd " +
			// this.target.getLocation().toString().substring(0,
			// this.target.getLocation().toString().lastIndexOf("/")) + "; ";
			String completeCommand = path;
			completeCommand += " "
					+ this.target.getLocation().toString()
					+ " "
					+ this.options.trim()
					+ " -o "
					+ this.outputFolder
					+ "/"
					+ this.target.getName().substring(0,
							this.target.getName().length() - 4);
			if (eclipseJob == null) {
				this.eclipseJob = new NitCompileJob("Nit Compiler");
			}
			eclipseJob.cancelCurrentProcess();
			eclipseJob.setPath(completeCommand);
			// NB : On le lancera en background job
			eclipseJob.schedule();
		}
	}
}
