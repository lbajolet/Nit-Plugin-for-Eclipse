package org.nitlanguage.ndt.core.plugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

/**
 * @author lucas A class to help reading and writing in the properties of a
 *         project
 */
public class ProjectPropertiesHelper {

	/**
	 * Properties
	 */
	Properties props;

	/**
	 * Path to the properties of the project
	 */
	String pathForProps;

	/**
	 * Default constructor, automatically loads the values from the project
	 * properties file
	 * 
	 * @param path
	 *            Properties file path
	 */
	public ProjectPropertiesHelper(String path) {
		this.props = new Properties();
		try {
			// Load the properties file at the specified path
			// If the file does not exist, no values will be read
			loadProperties(path);
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		}
	}

	/**
	 * Loads the properties of a project
	 * 
	 * @param path
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private void loadProperties(String path) throws FileNotFoundException,
			IOException {
		String[] pathSplitted = path.split("\\.");
		if (pathSplitted[pathSplitted.length - 1].compareTo("properties") != 0) {
			path += ".properties";
		}
		this.pathForProps = path;
		props.load(new FileInputStream(path));
	}

	/**
	 * Writes the value in the key of a Properties object, ready for persistance
	 * 
	 * @param key
	 *            Key to save value at
	 * @param value
	 *            Value to save
	 */
	public void write(String key, String value) {
		props.setProperty(key, value);
	}

	/**
	 * Reads a value from a properties object at key
	 * 
	 * @param key
	 *            Key to read from
	 * @return The value corresponding to the key specified if existing or null
	 *         otherwise
	 */
	public String read(String key) {
		if (props.containsKey(key)) {
			return props.getProperty(key);
		}
		return null;
	}

	/**
	 * Saves the whole Properties object in the corresponding file
	 * (automatically creates the file if it does not exist)
	 * 
	 * @throws IOException
	 *             If the store operation failed
	 * @throws FileNotFoundException
	 *             If the file could not be found and could not be automatically
	 *             created either
	 */
	public void save() throws IOException, FileNotFoundException {
		OutputStream out = null;
		try {
			out = new FileOutputStream(pathForProps);
		} catch (FileNotFoundException e) {
			// If not file has been found, create it and recreate the
			// OutputStream
			File fichier = new File(pathForProps);
			if (!fichier.exists()) {
				fichier.createNewFile();
			}
			if (fichier.exists()) {
				out = new FileOutputStream(pathForProps);
			}
		}
		props.store(out, "");
		out.flush();
		out.close();
	}
}
