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

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import Composestar.Core.Config.Xml.CpsBaseHandler;
import Composestar.Core.Config.Xml.ModuleInfoHandler;
import Composestar.Core.Exception.ConfigurationException;
import Composestar.Utils.Logging.CPSLogger;

/**
 * Manager class for module information
 * 
 * @author Michiel Hendriks
 */
public class ModuleInfoManager
{
	protected static final CPSLogger logger = CPSLogger.getCPSLogger("ModuleInfo");

	private static ModuleInfoManager instance;

	protected Map<Class<?>, ModuleInfo> classCache;

	protected Map<String, ModuleInfo> idCache;

	protected BuildConfig buildConfig;

	/**
	 * Return the current instance of the ModuleInfoManager
	 * 
	 * @return
	 */
	public static ModuleInfoManager getInstance()
	{
		if (instance == null)
		{
			instance = new ModuleInfoManager();
		}
		return instance;
	}

	/**
	 * Load and register ModuleInfo instances from an XML file
	 * 
	 * @param source
	 * @throws ConfigurationException
	 */
	public static void load(InputSource source) throws ConfigurationException
	{
		try
		{
			SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
			parser.parse(source, getInstance().getNewSAXHandler(parser.getXMLReader()));
		}
		catch (ParserConfigurationException e)
		{
			throw new ConfigurationException("Parser Configuration Exception: " + e.getMessage());
		}
		catch (SAXParseException e)
		{
			throw new ConfigurationException(String.format("SAX Parse Exception at #%d,%d : %s", e.getLineNumber(), e
					.getColumnNumber(), e.getMessage()));
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

	public static void load(InputStream source) throws ConfigurationException
	{
		load(new InputSource(source));
	}

	/**
	 * Return the ModuleInfo instance for a certain
	 * 
	 * @param forClass
	 * @return
	 */
	public static ModuleInfo get(Class<?> forClass)
	{
		ModuleInfoManager mim = getInstance();
		if (mim.classCache.containsKey(forClass))
		{
			return mim.classCache.get(forClass);
		}
		InputStream is = forClass.getResourceAsStream("moduleinfo.xml");
		if (is != null)
		{
			try
			{
				load(new InputSource(is));
				if (mim.classCache.containsKey(forClass))
				{
					return mim.classCache.get(forClass);
				}
			}
			catch (ConfigurationException e)
			{
				logger.error("Failed to load module info from XML: " + e.getMessage());
			}
		}
		logger.debug("No module info available for " + forClass);
		return null;
	}

	public static ModuleInfo get(String moduleName)
	{
		ModuleInfoManager mim = getInstance();
		if (mim.idCache.containsKey(moduleName))
		{
			return mim.idCache.get(moduleName);
		}
		return null;
	}

	/**
	 * Remove a ModuleInfo class
	 * 
	 * @param mi
	 */
	public static void remove(ModuleInfo mi)
	{
		if (mi == null)
		{
			return;
		}
		// TODO: remove from idCache
		remove(mi.getModuleClass());
	}

	/**
	 * Remove a certain registered class
	 * 
	 * @param mi
	 */
	public static void remove(Class<?> forClass)
	{
		ModuleInfoManager mim = getInstance();
		mim.classCache.remove(forClass);
	}

	/**
	 * Removes all instances of the given module name. This does not remove them
	 * from the class cache, just the module id cache.
	 * 
	 * @param moduleName
	 */
	public static void remove(String moduleName)
	{
		ModuleInfoManager mim = getInstance();
		mim.idCache.remove(moduleName);
	}

	/**
	 * Removes all registered ModuleInfo instances
	 */
	public static void clear()
	{
		ModuleInfoManager mim = getInstance();
		mim.idCache.clear();
		mim.classCache.clear();
	}

	/**
	 * Register a ModuleInfo to the system
	 * 
	 * @param newMi
	 */
	public void addModuleInfo(ModuleInfo newMi)
	{
		if (classCache.containsKey(newMi.getModuleClass()))
		{
			logger.warn("Manager already has a ModuleInfo for " + newMi.getModuleClass());
		}
		else
		{
			logger.info("Adding ModuleInfo for " + newMi.getId() + ": " + newMi.getModuleClass());
			classCache.put(newMi.getModuleClass(), newMi);
		}
		String id = newMi.getId();
		if (idCache.containsKey(id))
		{
			logger.debug("Already a module registered with id: " + id);
			// TODO: properly handle this, we now assume that the last module
			// registered is the active one
		}
		idCache.put(id, newMi);
		newMi.initConfig(buildConfig);
	}

	/**
	 * Set the buildconfiguration
	 * 
	 * @param newConfig
	 */
	public void setBuildConfig(BuildConfig newConfig)
	{
		buildConfig = newConfig;
		for (ModuleInfo mi : classCache.values())
		{
			mi.initConfig(buildConfig);
		}
	}

	protected ModuleInfoManager()
	{
		classCache = new HashMap<Class<?>, ModuleInfo>();
		idCache = new HashMap<String, ModuleInfo>();
	}

	/**
	 * Return the SAX DefaultHandler
	 * 
	 * @param reader
	 * @return
	 */
	protected DefaultHandler getNewSAXHandler(XMLReader reader)
	{
		return new ModuleInfosHandler(this, reader);
	}

	/**
	 * SAX Handler for loading multiple moduleinfos from an XML file
	 * 
	 * @author Michiel Hendriks
	 */
	static class ModuleInfosHandler extends CpsBaseHandler
	{
		protected ModuleInfoManager manager;

		protected ModuleInfoHandler miHandler;

		public ModuleInfosHandler(ModuleInfoManager inManager, XMLReader inReader)
		{
			super(inReader, null);
			manager = inManager;
			reader = inReader;
		}

		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
		{
			if (qName.equals("moduleinfo"))
			{
				miHandler = new ModuleInfoHandler(reader, this);
				reader.setContentHandler(miHandler);
				miHandler.startElement(uri, localName, qName, attributes);
			}
		}

		public void endElement(String uri, String localName, String qName) throws SAXException
		{
			if (qName.endsWith("moduleinfo"))
			{
				if (miHandler != null)
				{
					manager.addModuleInfo(miHandler.getModuleInfo());
				}
			}
		}
	}
}