package builder;

import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.editors.text.TextFileDocumentProvider;
import org.eclipse.ui.texteditor.IDocumentProvider;

public class NitCompilerMessageInterpreter {

	/**
	 * Processes the raw messages sent by the compiler process for easy use
	 * 
	 * @param messages
	 *            Raw string, exact output of the compiler
	 * @return Array of formatted messages with the needed informations
	 */
	public NitCompilerMessage[] processMessagesOfCompiler(String messages) {
		String[] messagesReturned = messages.split("\\n");
		ArrayList<NitCompilerMessage> compMess = new ArrayList<NitCompilerMessage>();
		for (String mess : messagesReturned) {
			compMess.add(new NitCompilerMessage(mess));
		}
		return compMess.toArray(new NitCompilerMessage[compMess.size()]);
	}

	/**
	 * Adds the processed messages to the Problems view of Eclipse and adds
	 * markers on the problematic files, useful for debugging
	 * 
	 * @param mess
	 *            Messages to add as markers to the problems view and files
	 */
	public void addMessagesToProblems(NitCompilerMessage[] mess,
			IProject target, HashMap<String, IFile> nitFilesOfProject) {

		for (NitCompilerMessage currentMessage : mess) {
			if (nitFilesOfProject.containsKey(currentMessage.getFileName())) {
				IMarker m;
				// We'll get the absolute index for the line in the file, this
				// is necessary because the CHAR_START and CHAR_END attributes
				// of the marker are absolute while the positions sent by the
				// compiler are relative to the line
				IDocumentProvider provider = new TextFileDocumentProvider();
				IFile fichier = nitFilesOfProject.get(currentMessage
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
						m = target.createMarker(IMarker.PROBLEM);
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

}
