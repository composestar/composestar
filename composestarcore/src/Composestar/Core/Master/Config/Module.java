package Composestar.Core.Master.Config;

import java.io.Serializable;
import java.util.Properties;

public class Module implements Serializable{

	private String name;
	private Properties properties;
	
	public Module() {
		properties = new Properties();
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void addProperty(String key, String value) {
		properties.setProperty(key, value);
	}
	
	public String getProperty(String key) {
		if(properties.containsKey(key))
			return properties.getProperty(key);
		return null;
	}
}

