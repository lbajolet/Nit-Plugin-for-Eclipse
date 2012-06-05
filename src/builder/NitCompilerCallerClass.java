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

	/**
	 * The folder to save the compiled binaries into
	 */
	private String outputFolder;

	/**
	 * The options for compilation
	 */
	private String options;

	/**
	 * @author r4pass The Job used to compile a Nit Project using the default
	 *         file set by the user
	 */
	public class NitCompileJob extends Job {

		/**
		 * Path to the compiler + target and options
		 */
		private String path;
		/**
		 * The process called to compile some nit sources
		 */
		private Process compileProcess;
		/**
		 * If the process is being called, true
		 */
		private Boolean isBeingCalled;
		/**
		 * The return message of the process
		 */
		private String returnMessage;
		/**
		 * Boolean to set if the compiler finished its work or not
		 */
		private boolean isOver;

		/**
		 * Cancels the process running
		 */
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

		/**
		 * Returns boolean saying of the process finished its work or not
		 * 
		 * @return true if over, false if not
		 */
		public boolean isOver() {
			return this.isOver;
		}

		/**
		 * Returns the compilation process
		 * 
		 * @return Process representing an instance of nitc
		 */
		public Process getCurrentCompileProcess() {
			return this.compileProcess;
		}

		/**
		 * Sets the path of the compiler / target file and options
		 * 
		 * @param path
		 *            Path + arguments for compilation
		 */
		public void setPath(String path) {
			this.path = path;
		}

		/**
		 * Returns the message from the compiler process
		 * 
		 * @return Message from the compiler
		 */
		public String getReturnMessage() {
			return this.returnMessage;
		}

		/**
		 * Default constructor
		 * 
		 * @param name
		 *            Required by super constructor
		 */
		public NitCompileJob(String name) {
			super(name);
			isBeingCalled = false;
		}

		@Override
		protected IStatus run(IProgressMonitor monitor) {
			isOver = false;
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
				isOver = true;
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
							isOver = true;
							return Status.CANCEL_STATUS;
						}
					} catch (InterruptedException e) {
						this.returnMessage = "Error : Compilation aborted";
						isBeingCalled = false;
						isOver = true;
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
					isOver = true;
					return Status.OK_STATUS;
				} catch (IOException e) {
					this.returnMessage = "Error : Binary cannot be found at specified path";
					isBeingCalled = false;
					monitor.setCanceled(true);
					monitor.done();
					isOver = true;
					return Status.OK_STATUS;
				} catch (Exception e) {
					e.printStackTrace();
					monitor.setCanceled(true);
					monitor.done();
					isOver = true;
					return Status.OK_STATUS;
				}
			}
		}
	}

	/**
	 * The default constructor
	 */
	public NitCompilerCallerClass() {
		this.options = "";
	}

	/**
	 * Gets the compilation eclipse job
	 * 
	 * @return The Nit compile job containing the process
	 */
	public NitCompileJob getCompileJob() {
		return this.eclipseJob;
	}

	/**
	 * Sets the target for compilation
	 * 
	 * @param file
	 */
	public void setTarget(IFile file) {
		this.target = file;
	}

	/**
	 * Set the options for the compilation
	 * 
	 * @param newOpt
	 *            Series of options in one string
	 */
	public void setOptions(String newOpt) {
		this.options = newOpt;
	}

	/**
	 * The path for the compiler
	 * 
	 * @param newPath
	 *            Path
	 */
	public void setPath(String newPath) {
		this.path = newPath;
	}

	/**
	 * The output folder for the binaries
	 * 
	 * @param folder
	 */
	public void setOutFolder(String folder) {
		this.outputFolder = folder;
	}

	/**
	 * Checks if the compiler binary exists where specified, adds an error log
	 * entry otherwise
	 * 
	 * @return true if exists, false otherwise
	 */
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
	 * Calls the compiler on the set target with the set options
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
