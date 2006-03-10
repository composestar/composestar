package Composestar.Core.Master.Config;

import java.util.Properties;

public class Module {

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
		System.out.println(""+this.name+" Module property "+key+" added with value "+value);
	}
	
	public String getProperty(String key) {
		if(properties.containsKey(key))
			return properties.getProperty(key);
		return null;
	}
}
