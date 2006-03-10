package Composestar.Core.Master.Config;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Properties;

public class CompilerSettings implements Serializable{

	private Properties properties;
	private ArrayList compilerActions;
	private ArrayList compilerConverters;
	
	public CompilerSettings() {
			properties = new Properties();
			compilerActions = new ArrayList();
			compilerConverters = new ArrayList();
	}
	
	public void addCompilerAction(CompilerAction action) {
		compilerActions.add(action);
	}
	
	public ArrayList getCompilerActions() {
		return compilerActions;
	}
	
	public void addCompilerConverter(CompilerConverter action) {
		compilerConverters.add(action);
	}
	
	public ArrayList getCompilerConverters() {
		return compilerConverters;
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
