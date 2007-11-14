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
public class ModuleSetting<T extends Serializable> implements Serializable
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

	protected Class<T> type;

	/**
	 * Current value
	 */
	protected T value;

	/**
	 * The default value
	 */
	protected T defaultValue;

	public ModuleSetting(String inId, Class<T> isType)
	{
		id = inId;
		name = id;
		type = isType;
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

	public void setDefault(T defVal)
	{
		setValue(defVal);
		defaultValue = value;
	}

	public void setDefaultFromString(String defVal) throws ConfigurationException
	{
		setValueFromString(defVal);
		defaultValue = value;
	}

	public T getValue()
	{
		return value;
	}

	/**
	 * Sets the newvalue
	 * 
	 * @param newValue
	 * @throws ConfigurationException
	 */
	public void setValue(T newValue)
	{
		if (newValue instanceof String)
		{
			try
			{
				setValueFromString((String) newValue);
			}
			catch (ConfigurationException e)
			{
				value = defaultValue;
			}
			return;
		}
		if (newValue == null)
		{
			value = defaultValue;
		}
		else
		{
			value = newValue;
		}
	}

	/**
	 * Sets a new value. Strings will automatically be converted to the
	 * appropiate type.
	 * 
	 * @param newValue
	 * @throws ConfigurationException
	 */
	@SuppressWarnings("unchecked")
	public void setValueFromString(String newValue) throws ConfigurationException
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
					value = (T) Integer.valueOf(newValue);
				}
				catch (NumberFormatException e)
				{
					throw new ConfigurationException("Number Format Exception: " + e.getMessage());
				}
			}
		}
		else if (type == Boolean.class)
		{
			value = (T) Boolean.valueOf(newValue);
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
					value = (T) Float.valueOf(newValue);
				}
				catch (NumberFormatException e)
				{
					throw new ConfigurationException("Number Format Exception: " + e.getMessage());
				}
			}
		}
		else if (type == String.class)
		{
			value = (T) newValue;
		}
		else if (Enum.class.isAssignableFrom(type))
		{
			try
			{
				value = (T) Enum.valueOf((Class<Enum>) type, newValue.trim());
			}
			catch (IllegalArgumentException e)
			{
				value = defaultValue;
				throw new ConfigurationException(String.format("Unable to cast %s to an enum of type %s", newValue,
						type.getName()));
			}
		}
		else
		{
			throw new ConfigurationException("Invalid type of new value. Received " + newValue.getClass()
					+ " expected " + type);
		}
	}

	public T getDefaultValue()
	{
		return defaultValue;
	}

	/**
	 * Reset the value of this setting to the default
	 */
	public void reset()
	{
		value = defaultValue;
	}
}
