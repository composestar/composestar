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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import Composestar.Core.Exception.ConfigurationException;
import Composestar.Core.INCRE.INCREModule;
import Composestar.Core.INCRE.Config.ModulesHandler;
import Composestar.Utils.Logging.CPSLogger;

/**
 * Encapsulates the data in the module's moduleinfo.xml <br />
 * Create an instance of this class using the <code>ModuleInfo.load()</code>
 * static method. To use this information in a module request the instance
 * through the Configuration class.
 * 
 * @author Michiel Hendriks
 */
public class ModuleInfo implements Serializable
{
	private static final long serialVersionUID = -818944551130427548L;

	/**
	 * Module identifier
	 */
	protected String id;

	/**
	 * The class of the module
	 */
	protected Class<?> moduleClass;

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
	protected Map<String, ModuleSetting> settings;

	/**
	 * The incre module settings
	 */
	protected INCREModule increModule;

	protected transient CPSLogger moduleLogger;

	/**
	 * Load ModuleInfo through SAX. This is used by the ModuleInfoManager
	 * 
	 * @param reader
	 * @param inReturnHandler
	 * @return
	 */
	public static ModuleInfo loadSax(XMLReader reader, ContentHandler inReturnHandler)
	{
		ModuleInfo result = new ModuleInfo();
		reader.setContentHandler(result.getNewSAXHandler(reader, inReturnHandler));
		return result;
	}

	public String getId()
	{
		return id;
	}

	public Class<?> getModuleClass()
	{
		return moduleClass;
	}

	public String getName()
	{
		return name;
	}

	public String getDescription()
	{
		return description;
	}

	public INCREModule getIncreModule()
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
			// logger.debug("Request module setting '" + key + "' for module '"
			// + id + "'");
			ModuleSetting ms = settings.get(key);
			return ms;
		}
		throw new ConfigurationException("Requested unknown module setting '" + key + "' for module '" + id + "'");
	}

	public Iterator<ModuleSetting> getSettings()
	{
		return settings.values().iterator();
	}

	public void removeSetting(String key)
	{
		settings.remove(key);
	}

	/**
	 * Reset the settings to their default values
	 */
	public void resetSettings()
	{
		for (ModuleSetting ms : settings.values())
		{
			ms.reset();
		}
	}

	public void setSettingValue(String key, Object newValue) throws ConfigurationException
	{
		ModuleSetting ms = getSetting(key);
		ms.setValue(newValue);
	}

	public void setSettingValue(String key, String newValue) throws ConfigurationException
	{
		ModuleSetting ms = getSetting(key);
		ms.setValue(newValue);
	}

	public void setSettingValue(String key, int newValue) throws ConfigurationException
	{
		ModuleSetting ms = getSetting(key);
		ms.setValue(newValue);
	}

	public void setSettingValue(String key, boolean newValue) throws ConfigurationException
	{
		ModuleSetting ms = getSetting(key);
		ms.setValue(newValue);
	}

	public void setSettingValue(String key, float newValue) throws ConfigurationException
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
		settings = new HashMap<String, ModuleSetting>();
	}

	/**
	 * Initialize the configuration
	 */
	public void initConfig(BuildConfig bcfg)
	{
		resetSettings();
		if (bcfg == null)
		{
			return;
		}
		Map<String, String> config = bcfg.getSettings();
		for (Entry<String, String> entry : config.entrySet())
		{
			if (!entry.getKey().startsWith(id + "."))
			{
				continue;
			}
			String key = entry.getKey().substring(id.length() + 1);
			try
			{
				setSettingValue(key, entry.getValue());
			}
			catch (ConfigurationException e)
			{
				if (moduleLogger == null)
				{
					moduleLogger = CPSLogger.getCPSLogger(id);
				}
				moduleLogger.error("Error setting config option '" + entry.getKey() + "' to '" + entry.getValue()
						+ "': " + e.getMessage());
			}
		}
	}

	protected DefaultHandler getNewSAXHandler(XMLReader reader, ContentHandler inReturnHandler)
	{
		return new ModuleInfoHandler(this, reader, inReturnHandler);
	}

	/**
	 * SAX Handler class for the ModuleInfo
	 * 
	 * @author Michiel Hendriks
	 */
	static class ModuleInfoHandler extends DefaultHandler
	{
		protected static final CPSLogger logger = CPSLogger.getCPSLogger("ModuleInfo");

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

		protected void processExtends(String source)
		{
			ModuleInfo extMi;
			Class<?> extClass;
			try
			{
				extClass = Class.forName(source);
			}
			catch (ClassNotFoundException e)
			{
				extClass = null;
			}
			if (extClass != null)
			{
				extMi = ModuleInfoManager.get(extClass);
			}
			else
			{
				extMi = ModuleInfoManager.get(source);
			}
			if (extMi != null)
			{
				// perform a deep copy through (de)serialization
				ModuleInfo copy = null;
				try
				{
					ByteArrayOutputStream bos = new ByteArrayOutputStream();
					ObjectOutputStream out = new ObjectOutputStream(bos);
					out.writeObject(extMi);
					out.flush();
					out.close();

					ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray()));
					copy = (ModuleInfo) in.readObject();

					// copy back data
					mi.description = copy.description;
					mi.id = copy.id;
					mi.increModule = copy.increModule;
					mi.moduleClass = copy.moduleClass;
					mi.name = copy.name;
					mi.settings = copy.settings;
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
				catch (ClassNotFoundException cnfe)
				{
					cnfe.printStackTrace();
				}
			}
		}

		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
		{
			if ((state == STATE_INIT) && qName.equalsIgnoreCase("moduleinfo"))
			{
				state = STATE_MODULE;
				mi.settings.clear();
				boolean bExtended = false;
				if (attributes.getValue("extends") != null)
				{
					processExtends(attributes.getValue("extends"));
					bExtended = true;
				}
				if (attributes.getValue("id") != null)
				{
					mi.id = attributes.getValue("id");
					mi.name = mi.id;
				}
				if (attributes.getValue("class") != null)
				{
					try
					{
						mi.moduleClass = Class.forName(attributes.getValue("class"));
					}
					catch (ClassNotFoundException e)
					{
						logger.error("Module class not found: " + e.getMessage());
					}
				}
				if (mi.id == null || mi.id.equals(""))
				{
					throw new SAXException("ModuleInfo requires an id attribute.");
				}
				if (mi.moduleClass == null)
				{
					throw new SAXException("ModuleInfo requires an class attribute.");
				}

				if (bExtended)
				{
					// make sure incre module is up to date
					mi.increModule.setName(mi.id);
					mi.increModule.setModuleClass(mi.moduleClass);
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
					if (Boolean.valueOf(attributes.getValue("remove")))
					{
						// remove the setting
						mi.removeSetting(attributes.getValue("id"));
					}
					else
					{
						moduleSetting = new ModuleSetting(reader, this);
						reader.setContentHandler(moduleSetting.getSAXHandler());
						moduleSetting.getSAXHandler().startElement(uri, localName, qName, attributes);
					}
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
						mi.removeSetting(moduleSetting.getId()); // overloading
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
				mi.increModule.setName(mi.id);
				mi.increModule.setModuleClass(mi.moduleClass);
				increHandler = null;
			}
		}

		public void characters(char[] ch, int start, int length) throws SAXException
		{
			if (length <= 0)
			{
				return;
			}
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
