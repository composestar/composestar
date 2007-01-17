/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2006 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */
package Composestar.Core.Master.Config;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import Composestar.Core.COMP.LangCompiler;
import Composestar.Core.Exception.ModuleException;

public class CompilerSettings implements Serializable
{
	private static final long serialVersionUID = 1834476176562717483L;

	private Properties properties;

	private Map<String, CompilerAction> compilerActions;

	private Map<String, CompilerConverter> compilerConverters;

	public CompilerSettings()
	{
		properties = new Properties();
		compilerActions = new HashMap<String, CompilerAction>();
		compilerConverters = new HashMap<String, CompilerConverter>();
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
		return compilerActions.get(key);
	}

	public void addCompilerConverter(CompilerConverter converter)
	{
		compilerConverters.put(converter.getName(), converter);
	}

	public CompilerConverter getCompilerConverter(String key)
	{
		return compilerConverters.get(key);
	}

	public LangCompiler getCompiler() throws ModuleException
	{
		try
		{
			Class myclass = Class.forName(getProperty("implementedBy"));
			return (LangCompiler) myclass.newInstance();
		}
		catch (Exception e)
		{
			throw new ModuleException("Error while creating compiler defined by class "+getProperty("implementedBy"), "Master");
		}
	}

}
