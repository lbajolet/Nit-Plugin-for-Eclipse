package console;

import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

/**
 * @author lucas Console wrapper for the Nit plugin (Singleton)
 */
public class NitConsole {

	/**
	 * Instance of the console
	 */
	private static NitConsole instance;

	/**
	 * Id of the console
	 */
	public static String CONSOLE_NAME = "org.uqam.nit.ndt.console";

	private NitConsole() {
	}

	/**
	 * @return The singleton instance
	 */
	public static NitConsole getInstance() {
		if (instance == null) {
			instance = new NitConsole();
		}
		return instance;
	}

	/**
	 * Gets the console in Eclipse UI corresponding to Nit
	 * 
	 * @param name The name of the console
	 * @return The MessageConsole for nit
	 */
	private MessageConsole findConsole(String name) {
		ConsolePlugin plugin = ConsolePlugin.getDefault();
		IConsoleManager conMan = plugin.getConsoleManager();
		IConsole[] existing = conMan.getConsoles();
		for (int i = 0; i < existing.length; i++)
			if (name.equals(existing[i].getName()))
				return (MessageConsole) existing[i];
		// no console found, so create a new one
		MessageConsole myConsole = new MessageConsole(name, null);
		conMan.addConsoles(new IConsole[] { myConsole });
		return myConsole;
	}

	/**
	 * Writes a message in the Nit console
	 * 
	 * @param message
	 *            The message to write
	 */
	public void write(String message) {
		MessageConsole myConsole = findConsole(CONSOLE_NAME);
		MessageConsoleStream out = myConsole.newMessageStream();
		out.println(message);
	}

}
