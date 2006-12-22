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

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import Composestar.Core.INCRE.Module;
import Composestar.Core.INCRE.Config.ModulesHandler;
import Composestar.Utils.Logging.CPSLogger;

/**
 * Encapsulates the data in the module's module.xml
 * 
 * @author Michiel Hendriks
 */
public class ModuleInfo
{
	protected static final CPSLogger logger = CPSLogger.getCPSLogger("Configuration");

	/**
	 * Module identifier
	 */
	protected String id;

	/**
	 * Human-readable name of the module
	 */
	protected String name;

	/**
	 * Description of the module (in HTML?)
	 */
	protected String description;

	/**
	 * Contains the module settings
	 */
	protected Map settings;

	/**
	 * The incre module settings
	 */
	protected Module increModule;

	protected DefaultHandler SAXHandler;

	public static ModuleInfo load(InputStream source) throws ConfigurationException
	{
		try
		{
			SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
			ModuleInfo result = new ModuleInfo();
			result.SAXHandler = result.getNewSAXHandler(parser.getXMLReader(), null);
			parser.parse(source, result.SAXHandler);
			result.SAXHandler = null;
			return result;
		}
		catch (ParserConfigurationException e)
		{
			throw new ConfigurationException("Parser Configuration Exception: " + e.getMessage());
		}
		catch (SAXException e)
		{
			throw new ConfigurationException("SAX Exception: " + e.getMessage());
		}
		catch (IOException e)
		{
			throw new ConfigurationException("Exception reading configuration: " + e.getMessage());
		}
	}

	/**
	 * Use this method to load ModuleInfo from within a SAX loading system.
	 * Note: after construction the DefaultHandler should receive the
	 * startElement() call with the &lt;module&gt; tag
	 * 
	 * @param reader
	 * @param inReturnHandler
	 * @return
	 */
	public static ModuleInfo load(XMLReader reader, ContentHandler inReturnHandler)
	{
		ModuleInfo mi = new ModuleInfo();
		mi.SAXHandler = mi.getNewSAXHandler(reader, inReturnHandler);
		return mi;
	}

	public String getId()
	{
		return id;
	}

	public String getName()
	{
		return name;
	}

	public String getDescription()
	{
		return description;
	}

	public Module getIncreModule()
	{
		return increModule;
	}

	public void addSetting(ModuleSetting ms) throws ConfigurationException
	{
		if (settings.containsKey(ms.getId()))
		{
			throw new ConfigurationException("Duplicate module setting: " + ms.getId());
		}
		settings.put(ms.getId(), ms);
	}

	public ModuleSetting getSetting(String key) throws ConfigurationException
	{
		if (settings.containsKey(key))
		{
			logger.debug("Request module setting '" + key + "' for module '" + id + "'");
			ModuleSetting ms = (ModuleSetting) settings.get(key);
			return ms;
		}
		throw new ConfigurationException("Request unknown module setting '" + key + "' for module '" + id + "'");
	}

	public Iterator getSettings()
	{
		return settings.values().iterator();
	}

	public void removeSetting(String key)
	{
		settings.remove(key);
	}

	public void setSettingValue(String key, Object newValue) throws ConfigurationException
	{
		ModuleSetting ms = getSetting(key);
		ms.setValue(newValue);
	}

	public String getStringSetting(String key) throws ConfigurationException
	{
		ModuleSetting ms = getSetting(key);
		return ms.getStringValue();
	}

	public int getIntSetting(String key) throws ConfigurationException
	{
		ModuleSetting ms = getSetting(key);
		return ms.getIntValue();
	}

	public boolean getBooleanSetting(String key) throws ConfigurationException
	{
		ModuleSetting ms = getSetting(key);
		return ms.getBooleanValue();
	}

	public String getStringSetting(String key, String def)
	{
		try
		{
			ModuleSetting ms;
			ms = getSetting(key);
			return ms.getStringValue();
		}
		catch (ConfigurationException e)
		{
			return def;
		}
	}

	public int getIntSetting(String key, int def)
	{
		try
		{
			ModuleSetting ms = getSetting(key);
			return ms.getIntValue();
		}
		catch (ConfigurationException e)
		{
			return def;
		}
	}

	public boolean getBooleanSetting(String key, boolean def)
	{
		try
		{
			ModuleSetting ms = getSetting(key);
			return ms.getBooleanValue();
		}
		catch (ConfigurationException e)
		{
			return def;
		}
	}

	protected ModuleInfo()
	{
		settings = new HashMap();
	}

	protected DefaultHandler getNewSAXHandler(XMLReader reader, ContentHandler inReturnHandler)
	{
		return new ModuleInfoHandler(this, reader, inReturnHandler);
	}

	public DefaultHandler getSAXHandler()
	{
		return SAXHandler;
	}

	/**
	 * SAX Handler class for the ModuleInfo
	 * 
	 * @author Composer
	 */
	class ModuleInfoHandler extends DefaultHandler
	{

		protected static final byte STATE_INIT = 0;

		protected static final byte STATE_MODULE = 1;

		protected static final byte STATE_NAME = 2;

		protected static final byte STATE_DESCRIPTION = 3;

		protected static final byte STATE_SETTINGS = 4;

		protected static final byte STATE_INCRE = 5;

		protected ModuleInfo mi;

		protected XMLReader reader;

		protected ContentHandler returnHandler;

		protected byte state;

		protected ModuleSetting moduleSetting;

		protected ModulesHandler increHandler;

		public ModuleInfoHandler(ModuleInfo inMi, XMLReader inReader)
		{
			mi = inMi;
			reader = inReader;
		}

		public ModuleInfoHandler(ModuleInfo inMi, XMLReader inReader, ContentHandler inReturnHandler)
		{
			this(inMi, inReader);
			returnHandler = inReturnHandler;
		}

		protected void inheritedLoad(InputStream source) throws ConfigurationException
		{
			try
			{
				SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
				parser.parse(source, mi.getNewSAXHandler(parser.getXMLReader(), null));
			}
			catch (ParserConfigurationException e)
			{
				throw new ConfigurationException("Parser Configuration Exception: " + e.getMessage());
			}
			catch (SAXException e)
			{
				throw new ConfigurationException("SAX Exception: " + e.getMessage());
			}
			catch (IOException e)
			{
				throw new ConfigurationException("Exception reading configuration: " + e.getMessage());
			}
		}

		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
		{
			if ((state == STATE_INIT) && qName.equalsIgnoreCase("moduleinfo"))
			{
				state = STATE_MODULE;
				mi.settings.clear();
				if (attributes.getValue("extends") != null)
				{
					// this module extends from an other module
					String extendsFrom = attributes.getValue("extends");
					try
					{
						inheritedLoad(getClass().getResourceAsStream(extendsFrom));
					}
					catch (ConfigurationException e)
					{
						throw new SAXException("Exception while processing extended moduleinfo: " + e.getMessage());
					}
				}
				if (attributes.getValue("id") != null)
				{
					mi.id = attributes.getValue("id");
					mi.name = mi.id;
				}
			}
			else if ((state == STATE_MODULE) && qName.equalsIgnoreCase("name"))
			{
				state = STATE_NAME;
				mi.name = "";
			}
			else if ((state == STATE_MODULE) && qName.equalsIgnoreCase("description"))
			{
				state = STATE_DESCRIPTION;
				mi.description = "";
			}
			else if ((state == STATE_MODULE) && qName.equalsIgnoreCase("settings"))
			{
				state = STATE_SETTINGS;
			}
			else if ((state == STATE_SETTINGS) && qName.equalsIgnoreCase("setting"))
			{
				try
				{
					moduleSetting = new ModuleSetting(reader, this);
					reader.setContentHandler(moduleSetting.getSAXHandler());
					moduleSetting.getSAXHandler().startElement(uri, localName, qName, attributes);
				}
				catch (ConfigurationException e)
				{
					throw new SAXException(e);
				}
			}
			else if ((state == STATE_MODULE) && qName.equalsIgnoreCase("incre"))
			{
				state = STATE_INCRE;
				increHandler = new ModulesHandler(reader, this);
				reader.setContentHandler(increHandler);
			}
		}

		public void endElement(String uri, String localName, String qName) throws SAXException
		{
			if ((state == STATE_MODULE) && qName.equalsIgnoreCase("moduleinfo"))
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
				state = STATE_MODULE;
			}
			else if ((state == STATE_DESCRIPTION) && qName.equalsIgnoreCase("description"))
			{
				state = STATE_MODULE;
			}
			else if ((state == STATE_SETTINGS) && qName.equalsIgnoreCase("settings"))
			{
				state = STATE_MODULE;
			}
			else if ((state == STATE_SETTINGS) && qName.equalsIgnoreCase("setting"))
			{
				if (moduleSetting != null)
				{
					try
					{
						mi.addSetting(moduleSetting);
					}
					catch (ConfigurationException e)
					{
						throw new SAXException(e);
					}
					moduleSetting = null;
				}
			}
			else if ((state == STATE_INCRE) && qName.equalsIgnoreCase("incre"))
			{
				state = STATE_MODULE;
				mi.increModule = increHandler.getModule();
				increHandler = null;
			}
		}

		public void characters(char[] ch, int start, int length) throws SAXException
		{
			if (length <= 0) return;
			if (state == STATE_NAME)
			{
				mi.name += new String(ch, start, length);
			}
			else if (state == STATE_DESCRIPTION)
			{
				mi.description += new String(ch, start, length);
			}
		}
	}
}
