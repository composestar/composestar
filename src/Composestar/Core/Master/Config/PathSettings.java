package Composestar.Core.Master.Config;

import java.util.Properties;

public class PathSettings {

	private Properties paths;
	
	public PathSettings() {
		paths = new Properties();
	}

	public void addPath(String key, String value) {
		paths.setProperty(key, value);
		System.out.println("Path "+key+" added with value "+value);
	}
	
	public String getPath(String key) {
		if(paths.containsKey(key))
			return paths.getProperty(key);
		return null;
	}
}
