/*
 * This file is part of the Compose* project.
 * http://composestar.sourceforge.net
 * Copyright (C) 2007 University of Twente.
 *
 * Compose* is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as 
 * published by the Free Software Foundation; either version 2.1 of 
 * the License, or (at your option) any later version.
 *
 * Compose* is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public 
 * License along with this program. If not, see 
 * <http://www.gnu.org/licenses/>.
 *
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.txt
 *
 * $Id$
 */

package Composestar.Core.Config.Xml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.TransformerException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import Composestar.Core.Config.BuildConfig;
import Composestar.Core.Config.Xml.Legacy.ConvertBuildConfig;
import Composestar.Core.Exception.ConfigurationException;
import Composestar.Core.SECRET3.SECRETResources;
import Composestar.Core.SECRET3.Config.Xml.XmlConfiguration;
import Composestar.Utils.IOStream;

/**
 * BuildConfig loader, loads from an XML source.
 * 
 * @author Michiel Hendriks
 */
public class BuildConfigHandler extends DefaultBuildConfigHandler
{
	public static final String CURRENT_VERSION = "2.0";

	/**
	 * The version of the buildconfiguration file format
	 */
	protected String version;

	/**
	 * Handle required for build configuration conversion.
	 */
	protected InputStream source;

	protected SAXParser parser;

	/**
	 * Load the build configuration from the specified file. If it's an outdated
	 * format it will try to convert the file before parsing it.
	 * 
	 * @param file
	 * @return
	 * @throws ConfigurationException
	 */
	public static BuildConfig loadBuildConfig(File file) throws ConfigurationException
	{
		if (file == null)
		{
			throw new IllegalArgumentException("file can not be null");
		}

		InputStream is = getInputStream(file);
		try
		{
			return loadBuildConfig(is);
		}
		catch (OutdatedFormatException e)
		{
			return convertAndLoad(file, e);
		}

	}

	/**
	 * Loads the build configuration from the stream. This loading mechanism
	 * does not support automatic conversion of the input data to the current
	 * format.
	 * 
	 * @param stream
	 * @return
	 * @throws ConfigurationException
	 * @throws OutdatedFormatException
	 */
	public static BuildConfig loadBuildConfig(InputStream stream) throws ConfigurationException,
			OutdatedFormatException
	{
		BuildConfigHandler handler = new BuildConfigHandler(stream);
		return handler.getBuildConfig();
	}

	/**
	 * Convert the file and try loading it again.
	 * 
	 * @param file
	 * @param formatEx
	 * @return
	 * @throws ConfigurationException
	 */
	protected static BuildConfig convertAndLoad(File file, OutdatedFormatException formatEx)
			throws ConfigurationException
	{
		logger.info("Attempting conversion of build configuration from " + formatEx.getDetectedVersion() + " to "
				+ formatEx.getNeedVersion());
		InputStream is = getInputStream(file);
		IOStream iostream = new IOStream(false);
		ConvertBuildConfig converter = ConvertBuildConfig.getInstance();
		try
		{
			converter.convert(is, iostream.getOutputStream(), formatEx.getDetectedVersion());
		}
		catch (TransformerException e)
		{
			throw new ConfigurationException("BuildConfig conversion exception: " + e.getMessageAndLocation());
		}
		try
		{
			return loadBuildConfig(iostream.getInputStream());
		}
		catch (OutdatedFormatException e)
		{
			throw new ConfigurationException("Unable to convert build configuration: " + file.toString());
		}
	}

	/**
	 * Method to be used when the build configuration is nested in an other XML
	 * source. The normal way to load a buildconfiguration from an XML file is
	 * by using the static loadBuildConfig methods.
	 * 
	 * @param inReader
	 * @param inParent
	 */
	public BuildConfigHandler(XMLReader inReader, DefaultHandler inParent)
	{
		super(inReader, inParent);
	}

	/**
	 * The main constructor that will parse the input source to a BuildConfig
	 * instance. Use the getBuildConfig() method to get the build configuration.
	 * 
	 * @param inConfig
	 * @param inSource
	 * @throws ConfigurationException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	protected BuildConfigHandler(InputStream inSource) throws ConfigurationException, OutdatedFormatException
	{
		super(null, null);
		config = new BuildConfig();
		source = inSource;
		SAXParserFactory factory = SAXParserFactory.newInstance();
		try
		{
			factory.setNamespaceAware(true);
			parser = factory.newSAXParser();
			reader = parser.getXMLReader();
			parser.parse(source, this);
		}
		catch (ParserConfigurationException e)
		{
			throw new ConfigurationException("Parser Configuration Exception: " + e.getMessage());
		}
		catch (OutdatedFormatException e)
		{
			// continue throwing it.
			throw e;
		}
		catch (SAXParseException e)
		{
			throw new ConfigurationException("SAX Parse Exception at #" + e.getLineNumber() + "," + e.getLineNumber()
					+ ": " + e.getMessage());
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

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.Config.Xml.CpsBaseHandler#endElement(java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public void endElement(String uri, String localName, String name) throws SAXException
	{
		super.endElement(uri, localName, name);
		if (version != null)
		{
			if ("buildconfiguration".equals(name) || "buildconfiguration".equals(localName))
			{
				returnHandler();
			}
			else if ("secret".equals(currentName))
			{
				// nop
			}
			else
			{
				endUnknownElement(uri, localName, name);
			}
		}
		else
		{
			endUnknownElement(uri, localName, name);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.Config.Xml.CpsBaseHandler#startElement(java.lang.String,
	 * java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	@Override
	public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException
	{
		super.startElement(uri, localName, name, attributes);
		if (version == null)
		{
			if ("buildconfiguration".equals(name) || "buildconfiguration".equals(localName))
			{
				version = attributes.getValue("version");
				if (version == null)
				{
					version = "2.0";
				}
				// TODO: check version
			}
			else if ("BuildConfiguration".equals(currentName))
			{
				logger.info("BuildConfiguration version 1 detected.");
				throw new OutdatedFormatException("1.0", CURRENT_VERSION);
			}
			else
			{
				startUnknownElement(uri, localName, name, attributes);
			}
		}
		else
		{
			// don't process anything else unless a version has been defined
			if ("settings".equals(currentName))
			{
				reader.setContentHandler(new SettingsHandler(reader, this));
				reader.getContentHandler().startElement(uri, localName, name, attributes);
			}
			else if ("project".equals(currentName))
			{
				reader.setContentHandler(new ProjectHandler(reader, this));
				reader.getContentHandler().startElement(uri, localName, name, attributes);
			}
			else if ("filters".equals(currentName))
			{
				reader.setContentHandler(new FilterHandler(reader, this));
				reader.getContentHandler().startElement(uri, localName, name, attributes);
			}
			else if ("secret".equals(currentName))
			{
				SECRETResources resc = new SECRETResources();
				config.setSecretResources(resc);
				reader.setContentHandler(new XmlConfiguration(reader, this, resc));
				reader.getContentHandler().startElement(uri, localName, name, attributes);
			}
			else
			{
				startUnknownElement(uri, localName, name, attributes);
			}
		}
	}

	/**
	 * Just for testing
	 * 
	 * @param args
	 */
	public static void main(String[] args)
	{
		try
		{
			org.apache.log4j.BasicConfigurator.configure();
			Logger.getRootLogger().setLevel(Level.ALL);
			BuildConfig cfg = loadBuildConfig(new File(args[0]));
			System.out.println("Sources:      " + cfg.getProject().getSourceFiles());
			System.out.println("Concerns:     " + cfg.getProject().getConcernFiles());
			System.out.println("Dependencies: " + cfg.getProject().getFilesDependencies());
			System.out.println("Resources:    " + cfg.getProject().getResourceFiles());
			System.out.println("");
		}
		catch (ConfigurationException e)
		{
			e.printStackTrace();
		}
	}
}
