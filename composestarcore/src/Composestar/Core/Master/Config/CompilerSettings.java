package Composestar.Core.Master.Config;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Properties;

import Composestar.Core.COMP.LangCompiler;
import Composestar.Core.Exception.ModuleException;

public class CompilerSettings implements Serializable{

	private Properties properties;
	private HashMap compilerActions;
	private HashMap compilerConverters;
		
	public CompilerSettings() {
			properties = new Properties();
			compilerActions = new HashMap();
			compilerConverters = new HashMap();
	}
	
	public void addCompilerAction(CompilerAction action) {
		compilerActions.put(action.getName(),action);
	}
	
	public CompilerAction getCompilerAction(String key) {
		if(compilerActions.containsKey(key))
			return (CompilerAction)compilerActions.get(key);
		return null;
	}
	
	public void addCompilerConverter(CompilerConverter converter) {
		compilerConverters.put(converter.getName(),converter);
	}
	
	public CompilerConverter getCompilerConverter(String key) {
		if(compilerConverters.containsKey(key))
			return (CompilerConverter)compilerConverters.get(key);
		return null;
	}
	
	public void addProperty(String key, String value) {
		properties.setProperty(key, value);
	}
	
	public String getProperty(String key) {
		if(properties.containsKey(key))
			return properties.getProperty(key);
		return null;
	}
	
	public LangCompiler getCompiler() throws ModuleException {
		try {
			Class myclass = Class.forName(this.getProperty("implementedBy"));
			LangCompiler comp = (LangCompiler)myclass.newInstance();
			//LangCompiler comp = new Composestar.DotNET.
			return comp;
		}
		catch(Exception e) {
			throw new ModuleException("Error while creating compiler..");
		}
	}
	
}
