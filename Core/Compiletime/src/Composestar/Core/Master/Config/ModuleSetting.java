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

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

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
	protected Class type = String.class;

	/**
	 * Current value
	 */
	protected Object value;

	/**
	 * The default value
	 */
	protected Object defaultValue;

	protected transient DefaultHandler SAXHandler;

	/**
	 * Constructor to use with the SAX parser
	 * 
	 * @param inReader
	 * @param inReturnHandler
	 * @throws ConfigurationException
	 */
	public ModuleSetting(XMLReader inReader, ContentHandler inReturnHandler) throws ConfigurationException
	{
		getSAXHandler(inReader, inReturnHandler);
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
	public ModuleSetting(String inId, Class inType, Object inDefault) throws ConfigurationException
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

	public ModuleSetting(String inId, String inName, Class inType, Object inDefault) throws ConfigurationException
	{
		this(inId, inType, inDefault);
		name = inName;
	}

	public String getId()
	{
		return id;
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
			setValue(Integer.parseInt(newValue));
		}
		else if (type == Boolean.class)
		{
			setValue(Boolean.valueOf(newValue));
		}
		else if (type == Float.class)
		{
			setValue(Float.parseFloat(newValue));
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
		// TODO: when switching to Java 1.5 use Integer.valueOf(...)
		setValue(new Integer(newValue));
	}

	/**
	 * @param newValue
	 * @throws ConfigurationException
	 */
	public void setValue(float newValue) throws ConfigurationException
	{
		setValue(new Float(newValue));
	}

	public String getStringValue()
	{
		return (String) value;
	}

	public int getIntValue()
	{
		return ((Integer) value).intValue();
	}

	public boolean getBooleanValue()
	{
		return ((Boolean) value).booleanValue();
	}

	public float getFloatValue()
	{
		return ((Float) value).floatValue();
	}

	public Object getDefaultValue()
	{
		return defaultValue;
	}

	public Class getType()
	{
		return type;
	}

	/**
	 * Constructs the SAX Handler
	 * 
	 * @param reader
	 * @param inReturnHandler
	 * @return
	 */
	protected DefaultHandler getSAXHandler(XMLReader reader, ContentHandler inReturnHandler)
	{
		if (SAXHandler == null)
		{
			SAXHandler = new ModuleSettingHandler(this, reader, inReturnHandler);
		}
		return SAXHandler;
	}

	/**
	 * Returns the SAX handler. Will only have a value when this instance was
	 * created using the SAX constructor.
	 * 
	 * @return
	 */
	public DefaultHandler getSAXHandler()
	{
		return SAXHandler;
	}

	/**
	 * SAX Handler for the ModuleSetting
	 * 
	 * @author Composer
	 */
	static class ModuleSettingHandler extends DefaultHandler
	{
		protected ModuleSetting ms;

		protected XMLReader reader;

		protected ContentHandler returnHandler;

		protected byte state;

		protected static final byte STATE_INIT = 0;

		protected static final byte STATE_SETTING = 1;

		protected static final byte STATE_NAME = 2;

		protected static final byte STATE_DEFAULT = 3;

		protected String defValue;

		public ModuleSettingHandler(ModuleSetting inMs, XMLReader inReader)
		{
			ms = inMs;
			reader = inReader;
		}

		public ModuleSettingHandler(ModuleSetting inMs, XMLReader inReader, ContentHandler inReturnHandler)
		{
			this(inMs, inReader);
			returnHandler = inReturnHandler;
		}

		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
		{
			if ((state == STATE_INIT) && qName.equalsIgnoreCase("setting"))
			{
				state = STATE_SETTING;
				ms.id = attributes.getValue("id");
				ms.name = ms.id;

				if (attributes.getValue("type") != null)
				{
					String prefType = attributes.getValue("type");
					if (prefType.equalsIgnoreCase("integer") || prefType.equalsIgnoreCase("int"))
					{
						prefType = Integer.class.getName();
					}
					else if (prefType.equalsIgnoreCase("boolean") || prefType.equalsIgnoreCase("bool"))
					{
						prefType = Boolean.class.getName();
					}
					else if (prefType.equalsIgnoreCase("string"))
					{
						prefType = String.class.getName();
					}
					else if (prefType.equalsIgnoreCase("float"))
					{
						prefType = Float.class.getName();
					}
					try
					{
						ms.type = Class.forName(prefType);
					}
					catch (ClassNotFoundException e)
					{
						throw new SAXException(new ConfigurationException("Invalid type: " + prefType));
					}
				}
			}
			else if ((state == STATE_SETTING) && qName.equalsIgnoreCase("name"))
			{
				state = STATE_NAME;
				ms.name = "";
			}
			else if ((state == STATE_SETTING) && qName.equalsIgnoreCase("default"))
			{
				state = STATE_DEFAULT;
				defValue = "";
			}
		}

		public void endElement(String uri, String localName, String qName) throws SAXException
		{
			if ((state == STATE_SETTING) && qName.equalsIgnoreCase("setting"))
			{
				state = STATE_INIT;
				if (returnHandler != null)
				{
					reader.setContentHandler(returnHandler);
					returnHandler.endElement(uri, localName, qName);
				}
			}
			else if ((state == STATE_NAME) && qName.equalsIgnoreCase("name"))
			{
				state = STATE_SETTING;
			}
			else if ((state == STATE_DEFAULT) && qName.equalsIgnoreCase("default"))
			{
				state = STATE_SETTING;
				try
				{
					ms.setValue(defValue);
				}
				catch (ConfigurationException e)
				{
					throw new SAXException(e);
				}
				ms.defaultValue = ms.value;
			}
		}

		public void characters(char[] ch, int start, int length) throws SAXException
		{
			if (length <= 0) return;
			if (state == STATE_NAME)
			{
				ms.name += new String(ch, start, length);
			}
			else if (state == STATE_DEFAULT)
			{
				defValue += new String(ch, start, length);
			}
		}
	}
}
