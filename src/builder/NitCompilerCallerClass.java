package builder;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashSet;
import java.util.Iterator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

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
	 * Options to be added when compiling
	 */
	private HashSet<String> options;

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
	 * @author r4pass The Job used to compile a Nit Project using the default
	 *         file set by the user
	 */
	private class NitCompileJob extends Job {

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
			ProjectAutoParser pap = new ProjectAutoParser();
			pap.setProject(target.getProject());
			this.setPriority(BUILD);
			monitor.beginTask("Compiling Nit", 100);
			cancelCurrentProcess();
			if (path == null) {
				this.returnMessage = "Error : No file path specified";
				isBeingCalled = false;
				monitor.setCanceled(true);
				return null;
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
							return null;
						}
					} catch (InterruptedException e) {
						this.returnMessage = "Error : Compilation aborted";
						isBeingCalled = false;
						return null;
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
							target.getProject(),
							pap2.buildFilesInProjectRepo(target.getProject()));

					monitor.worked(20);

					monitor.worked(10);

					monitor.done();
					return Status.OK_STATUS;
				} catch (IOException e) {
					this.returnMessage = "Error : Binary cannot be found at specified path";
					isBeingCalled = false;
					monitor.setCanceled(true);
					return null;
				} catch (Exception e) {
					e.printStackTrace();
					monitor.setCanceled(true);
					return null;
				}
			}
		}
	}

	/**
	 * 
	 */
	public NitCompilerCallerClass() {
		this.options = new HashSet<String>();
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
	public void addOption(String newOpt) {
		if (!options.contains(newOpt)) {
			options.add(newOpt);
		}
	}

	/**
	 * @param newPath
	 */
	public void setPath(String newPath) {
		this.path = newPath;
	}

	/**
	 * 
	 */
	public void call() {
		if (this.path != null && this.target != null) {
			// String completeCommand = "cd " +
			// this.target.getLocation().toString().substring(0,
			// this.target.getLocation().toString().lastIndexOf("/")) + "; ";
			String completeCommand = path;
			Iterator<String> iter = options.iterator();
			String currOpt = "";
			while (iter.hasNext()) {
				currOpt = iter.next();
				completeCommand += " " + currOpt;
			}
			completeCommand += " " + this.target.getLocation().toString();
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
