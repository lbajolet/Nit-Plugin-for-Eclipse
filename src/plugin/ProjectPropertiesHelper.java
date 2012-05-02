package plugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

public class ProjectPropertiesHelper {

	Properties props;

	String pathForProps;

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

	private void loadProperties(String path) throws FileNotFoundException,
			IOException {
		String[] pathSplitted = path.split("\\.");
		if (pathSplitted[pathSplitted.length - 1].compareTo("properties") != 0) {
			path += ".properties";
		}
		this.pathForProps = path;
		props.load(new FileInputStream(path));
	}

	public void write(String key, String value) {
		props.setProperty(key, value);
	}

	public String read(String key) {
		if (props.containsKey(key)) {
			return props.getProperty(key);
		}
		return null;
	}

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
