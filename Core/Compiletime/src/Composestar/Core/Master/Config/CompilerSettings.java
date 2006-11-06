package Composestar.Core.Master.Config;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Properties;

import Composestar.Core.COMP.LangCompiler;
import Composestar.Core.Exception.ModuleException;

public class CompilerSettings implements Serializable
{
	private Properties properties;

	private HashMap compilerActions;

	private HashMap compilerConverters;

	public CompilerSettings()
	{
		properties = new Properties();
		compilerActions = new HashMap();
		compilerConverters = new HashMap();
	}

	public void addProperty(String key, String value)
	{
		properties.setProperty(key, value);
	}

	public String getProperty(String key)
	{
		return properties.getProperty(key);
	}

	public void addCompilerAction(CompilerAction action)
	{
		compilerActions.put(action.getName(), action);
	}

	public CompilerAction getCompilerAction(String key)
	{
		return (CompilerAction) compilerActions.get(key);
	}

	public void addCompilerConverter(CompilerConverter converter)
	{
		compilerConverters.put(converter.getName(), converter);
	}

	public CompilerConverter getCompilerConverter(String key)
	{
		return (CompilerConverter) compilerConverters.get(key);
	}

	public LangCompiler getCompiler() throws ModuleException
	{
		try
		{
			Class myclass = Class.forName(this.getProperty("implementedBy"));
			return (LangCompiler) myclass.newInstance();
		}
		catch (Exception e)
		{
			throw new ModuleException("Error while creating compiler..", "Master");
		}
	}

}
