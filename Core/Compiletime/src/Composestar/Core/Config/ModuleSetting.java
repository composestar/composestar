/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2006 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */

package Composestar.Core.Config;

import java.io.Serializable;

import Composestar.Core.Exception.ConfigurationException;

/**
 * Specifies a single module setting
 * 
 * @author Michiel Hendriks
 */
public class ModuleSetting implements Serializable
{
	private static final long serialVersionUID = -1912794100834316135L;

	/**
	 * The config variable
	 */
	protected String id;

	/**
	 * Human readable name for the setting
	 */
	protected String name;

	/**
	 * Type of the setting
	 */
	protected Class<?> type = String.class;

	/**
	 * Current value
	 */
	protected Object value;

	/**
	 * The default value
	 */
	protected Object defaultValue;

	/**
	 * Constructor to add settings at runtime
	 * 
	 * @param inId
	 * @param inDefault
	 * @throws ConfigurationException
	 */
	public ModuleSetting(String inId, Class<?> inType) throws ConfigurationException
	{
		this(inId, inType, null);
	}

	/**
	 * Constructor to add settings at runtime
	 * 
	 * @param inId
	 * @param inDefault
	 * @throws ConfigurationException
	 */
	public ModuleSetting(String inId, Object inDefault) throws ConfigurationException
	{
		this(inId, inDefault.getClass(), inDefault);
	}

	/**
	 * Constructor to add settings at runtime
	 * 
	 * @param inId
	 * @param inType
	 * @param inDefault
	 * @throws ConfigurationException
	 */
	public ModuleSetting(String inId, Class<?> inType, Object inDefault) throws ConfigurationException
	{
		id = inId;
		name = id;
		type = inType;
		setValue(inDefault);
		defaultValue = value;
	}

	public ModuleSetting(String inId, String inName, Object inDefault) throws ConfigurationException
	{
		this(inId, inName, inDefault.getClass(), inDefault);
	}

	public ModuleSetting(String inId, String inName, Class<?> inType, Object inDefault) throws ConfigurationException
	{
		this(inId, inType, inDefault);
		name = inName;
	}

	public String getId()
	{
		return id;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String newName)
	{
		if (newName == null)
		{
			throw new IllegalArgumentException("Name can not be null");
		}
		name = newName;
	}

	public void setDefault(Object defVal)
	{
		try
		{
			setValue(defVal);
			defaultValue = value;
		}
		catch (ConfigurationException e)
		{
			defaultValue = null;
		}
	}

	public void setDefault(String defVal)
	{
		try
		{
			setValue(defVal);
			defaultValue = value;
		}
		catch (ConfigurationException e)
		{
			defaultValue = null;
		}
	}

	public Object getValue()
	{
		return value;
	}

	/**
	 * Sets the newvalue
	 * 
	 * @param newValue
	 * @throws ConfigurationException
	 */
	public void setValue(Object newValue) throws ConfigurationException
	{
		if (newValue == null)
		{
			value = defaultValue;
		}
		else if (newValue.getClass().equals(type))
		{
			value = newValue;
		}
		else
		{
			throw new ConfigurationException("Invalid type of new value. Received " + newValue.getClass()
					+ " expected " + type);
		}
	}

	/**
	 * Sets a new value. Strings will automatically be converted to the
	 * appropiate type.
	 * 
	 * @param newValue
	 * @throws ConfigurationException
	 */
	public void setValue(String newValue) throws ConfigurationException
	{
		if (newValue == null)
		{
			value = defaultValue;
		}
		else if (type == Integer.class)
		{
			if (newValue.equals(""))
			{
				value = defaultValue;
			}
			else
			{
				try
				{
					setValue(Integer.parseInt(newValue));
				}
				catch (NumberFormatException e)
				{
					throw new ConfigurationException("Number Format Exception: " + e.getMessage());
				}
			}
		}
		else if (type == Boolean.class)
		{
			setValue(Boolean.valueOf(newValue));
		}
		else if (type == Float.class)
		{
			if (newValue.equals(""))
			{
				value = defaultValue;
			}
			else
			{
				try
				{
					setValue(Float.parseFloat(newValue));
				}
				catch (NumberFormatException e)
				{
					throw new ConfigurationException("Number Format Exception: " + e.getMessage());
				}
			}
		}
		else if (type == String.class)
		{
			value = newValue;
		}
		else
		{
			throw new ConfigurationException("Invalid type of new value. Received " + newValue.getClass()
					+ " expected " + type);
		}
	}

	/**
	 * Shorthand for setting boolean values
	 * 
	 * @param newValue
	 * @throws ConfigurationException
	 */
	public void setValue(boolean newValue) throws ConfigurationException
	{
		setValue(Boolean.valueOf(newValue));
	}

	/**
	 * Shorthand for setting integer values
	 * 
	 * @param newValue
	 * @throws ConfigurationException
	 */
	public void setValue(int newValue) throws ConfigurationException
	{
		setValue(Integer.valueOf(newValue));
	}

	/**
	 * @param newValue
	 * @throws ConfigurationException
	 */
	public void setValue(float newValue) throws ConfigurationException
	{
		setValue(Float.valueOf(newValue));
	}

	public String getStringValue()
	{
		return (String) value;
	}

	public int getIntValue()
	{
		return (Integer) value;
	}

	public boolean getBooleanValue()
	{
		return (Boolean) value;
	}

	public float getFloatValue()
	{
		return (Float) value;
	}

	public Object getDefaultValue()
	{
		return defaultValue;
	}

	public Class<?> getType()
	{
		return type;
	}

	/**
	 * Reset the value of this setting to the default
	 */
	public void reset()
	{
		value = defaultValue;
	}
}
