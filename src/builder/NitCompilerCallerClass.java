package builder;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.editors.text.TextFileDocumentProvider;
import org.eclipse.ui.texteditor.IDocumentProvider;

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
	 * Reposit of all files in the current project, rebuilt on every compilation
	 */
	private HashMap<String, IFile> nitFilesOfProject;

	/**
	 * @author r4pass Visitor used for two tasks, rebuild the Files reposit for
	 *         the Project and remove the Markers created when compilation has
	 *         finished
	 */
	private class NitProjectVisitor implements IResourceVisitor {
		@Override
		public boolean visit(IResource resource) throws CoreException {
			if (resource instanceof IFile) {
				IFile fichier = (IFile) resource;
				if (fichier.getFileExtension().equals("nit")) {
					nitFilesOfProject.put(fichier.getName(), fichier);
					fichier.deleteMarkers(IMarker.PROBLEM, true,
							IResource.DEPTH_INFINITE);
				}
			}
			return true;
		}
	}

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
					monitor.worked(70);
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

					String resStr = result.toString();

					addMessagesToProblems(processMessagesOfCompiler(resStr));

					monitor.worked(10);

					Thread.sleep(1000);

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

	private NitCompilerMessage[] processMessagesOfCompiler(String messages) {
		// String newResponse = cleanCompilerResponse(messages);
		String[] messagesReturned = messages.split("\\n");
		ArrayList<NitCompilerMessage> compMess = new ArrayList<NitCompilerMessage>();
		for (String mess : messagesReturned) {
			compMess.add(new NitCompilerMessage(mess));
		}
		return compMess.toArray(new NitCompilerMessage[compMess.size()]);
	}

	// private String cleanCompilerResponse(String response){
	// response = response.replace("", "");
	// response = response.replace("[0;33m", "");
	// response = response.replace("[1;31m", "");
	// response = response.replace("[0m", "");
	// return response;
	// }

	private void addMessagesToProblems(NitCompilerMessage[] mess) {

		generateHashOfFilesForProject();

		for (NitCompilerMessage currentMessage : mess) {
			if (this.nitFilesOfProject
					.containsKey(currentMessage.getFileName())) {
				IMarker m;
				// We'll get the absolute index for the line in the file, this
				// is necessary because the CHAR_START and CHAR_END attributes
				// of the marker are absolute while the positions sent by the
				// compiler are relative to the line
				IDocumentProvider provider = new TextFileDocumentProvider();
				IFile fichier = this.nitFilesOfProject.get(currentMessage
						.getFileName());
				int startOffset = 0;
				try {
					provider.connect(fichier);
					IDocument doc = provider.getDocument(fichier);
					startOffset = doc.getLineInformation(
							currentMessage.getLine() - 1).getOffset();
				} catch (Exception e) {
					e.printStackTrace();
				}
				try {
					switch (currentMessage.getType()) {
					case 1:
						m = fichier.createMarker(IMarker.PROBLEM);
						m.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_HIGH);
						m.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
						m.setAttribute(IMarker.CHAR_START,
								currentMessage.getStartIndex() - 1
										+ startOffset);
						m.setAttribute(IMarker.CHAR_END,
								currentMessage.getEndIndex() + startOffset);
						m.setAttribute(IMarker.LINE_NUMBER,
								currentMessage.getLine());
						m.setAttribute(IMarker.MESSAGE,
								currentMessage.getRealMessage());
						break;
					case 2:
						m = fichier.createMarker(IMarker.PROBLEM);
						m.setAttribute(IMarker.PRIORITY,
								IMarker.PRIORITY_NORMAL);
						m.setAttribute(IMarker.SEVERITY,
								IMarker.SEVERITY_WARNING);
						m.setAttribute(IMarker.CHAR_START,
								currentMessage.getStartIndex() - 1
										+ startOffset);
						m.setAttribute(IMarker.CHAR_END,
								currentMessage.getEndIndex() + startOffset);
						m.setAttribute(IMarker.LINE_NUMBER,
								currentMessage.getLine());
						m.setAttribute(IMarker.MESSAGE,
								currentMessage.getRealMessage());
						break;
					case 3:
						m = fichier.createMarker(IMarker.PROBLEM);
						m.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_LOW);
						m.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_INFO);
						m.setAttribute(IMarker.CHAR_START,
								currentMessage.getStartIndex() - 1
										+ startOffset);
						m.setAttribute(IMarker.CHAR_END,
								currentMessage.getEndIndex() + startOffset);
						m.setAttribute(IMarker.LINE_NUMBER,
								currentMessage.getLine());
						m.setAttribute(IMarker.MESSAGE,
								currentMessage.getRealMessage());
						break;
					default:
						m = fichier.createMarker(IMarker.PROBLEM);
						m.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_LOW);
						m.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_INFO);
						m.setAttribute(IMarker.CHAR_START,
								currentMessage.getStartIndex() - 1
										+ startOffset);
						m.setAttribute(IMarker.CHAR_END,
								currentMessage.getEndIndex() + startOffset);
						m.setAttribute(IMarker.LINE_NUMBER,
								currentMessage.getLine());
						m.setAttribute(IMarker.MESSAGE,
								currentMessage.getRealMessage());
						break;
					}
				} catch (CoreException e) {
					e.printStackTrace();
				}
			} else {
				IMarker m;
				try {
					if (!currentMessage.getRawMessage().equals("")) {
						m = this.target.createMarker(IMarker.PROBLEM);
						m.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_HIGH);
						m.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
						m.setAttribute(IMarker.MESSAGE,
								currentMessage.getRawMessage());
						m.setAttribute(IMarker.LINE_NUMBER, 1);
					}
				} catch (CoreException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void generateHashOfFilesForProject() {
		this.nitFilesOfProject.clear();

		try {
			this.target.getProject().accept(new NitProjectVisitor());
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	public NitCompilerCallerClass() {
		this.options = new HashSet<String>();
		this.nitFilesOfProject = new HashMap<String, IFile>();
	}

	public void setTarget(IFile file) {
		this.target = file;
	}

	public void addOption(String newOpt) {
		if (!options.contains(newOpt)) {
			options.add(newOpt);
		}
	}

	public void setPath(String newPath) {
		this.path = newPath;
	}

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
