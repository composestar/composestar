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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import Composestar.Core.Config.Platform;
import Composestar.Core.Config.PlatformManager;
import Composestar.Core.Exception.ConfigurationException;
import Composestar.Utils.Logging.CPSLogger;

/**
 * Handles the <platforms> tag
 * 
 * @author Michiel Hendriks
 */
public class PlatformConfigHandler extends CpsBaseHandler
{
	protected static final CPSLogger logger = CPSLogger.getCPSLogger("Configuration.Platform");

	protected static final int STATE_PLATFORMS = 1;

	protected PlatformHandler platformHandler;

	public static void loadPlatformConfig(File file) throws ConfigurationException
	{
		if (file == null)
		{
			throw new IllegalArgumentException("file can not be null");
		}
		try
		{
			InputStream is = new FileInputStream(file);
			if (file.getName().endsWith(".gz"))
			{
				try
				{
					is = new GZIPInputStream(is);
				}
				catch (IOException e)
				{
					throw new ConfigurationException("IOException: " + e.getMessage());
				}
			}
			loadPlatformConfig(is);
		}
		catch (FileNotFoundException e)
		{
			throw new ConfigurationException("Platform configuration file not found: " + file);
		}
	}

	public static void loadPlatformConfig(InputStream stream) throws ConfigurationException
	{
		loadPlatformConfig(new InputSource(stream));
	}

	public static void loadPlatformConfig(InputSource source) throws ConfigurationException
	{
		try
		{
			SAXParserFactory factory = SAXParserFactory.newInstance();
			// factory.setNamespaceAware(true);
			SAXParser parser = factory.newSAXParser();
			parser.parse(source, new PlatformConfigHandler(parser.getXMLReader(), null));
		}
		catch (ParserConfigurationException e)
		{
			throw new ConfigurationException("Parser Configuration Exception: " + e.getMessage());
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

	public PlatformConfigHandler(XMLReader inReader, DefaultHandler inParent)
	{
		super(inReader, inParent);
	}

	@Override
	public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException
	{
		super.startElement(uri, localName, name, attributes);
		if (state == 0 && "platforms".equals(name))
		{
			state = STATE_PLATFORMS;
		}
		else if (state == STATE_PLATFORMS && "platform".equals(name))
		{
			if (platformHandler == null)
			{
				platformHandler = new PlatformHandler(reader, this);
			}
			reader.setContentHandler(platformHandler);
			platformHandler.startElement(uri, localName, name, attributes);
		}
		else
		{
			startUnknownElement(uri, localName, name, attributes);
		}
	}

	@Override
	public void endElement(String uri, String localName, String name) throws SAXException
	{
		super.endElement(uri, localName, name);
		if (state == STATE_PLATFORMS && "platforms".equals(name))
		{
			returnHandler();
		}
		else if (state == STATE_PLATFORMS && "platform".equals(name))
		{
			if (platformHandler != null)
			{
				Platform platform = platformHandler.getPlatform();
				logger.info("Loaded platform: " + platform.getId());
				PlatformManager.addPlatform(platform);
			}
		}
		else
		{
			endUnknownElement(uri, localName, name);
		}
	}

	public static void main(String[] args)
	{
		try
		{
			org.apache.log4j.BasicConfigurator.configure();
			loadPlatformConfig(new File(args[0]));
			for (Platform platform : PlatformManager.getPlatforms())
			{
				System.out.println("Platform: " + platform.getId());
			}
		}
		catch (ConfigurationException e)
		{
			e.printStackTrace();
		}
	}
}
