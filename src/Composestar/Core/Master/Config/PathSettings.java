package Composestar.Core.Master.Config;

import java.io.Serializable;
import java.util.Properties;

public class PathSettings implements Serializable{

	private Properties paths;
	
	public PathSettings() {
		paths = new Properties();
	}

	public void addPath(String key, String value) {
		paths.setProperty(key, value);
	}
	
	public String getPath(String key) {
		if(paths.containsKey(key))
			return paths.getProperty(key);
		return null;
	}
}

