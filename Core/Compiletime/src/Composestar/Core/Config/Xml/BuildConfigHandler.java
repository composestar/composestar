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
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import Composestar.Core.Config.BuildConfig;
import Composestar.Core.Master.Config.ConfigurationException;

/**
 * BuildConfig loader, loads from an XML source.
 * 
 * @author Michiel Hendriks
 */
public class BuildConfigHandler extends DefaultBuildConfigHandler
{
	protected String version;

	public static BuildConfig loadBuildConfig(File file) throws ConfigurationException
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
			return loadBuildConfig(is);
		}
		catch (FileNotFoundException e)
		{
			throw new ConfigurationException("Build configuration file not found: " + file);
		}
	}

	public static BuildConfig loadBuildConfig(InputStream stream) throws ConfigurationException
	{
		return loadBuildConfig(new InputSource(stream));
	}

	public static BuildConfig loadBuildConfig(InputSource source) throws ConfigurationException
	{
		try
		{
			BuildConfig config = new BuildConfig();
			SAXParserFactory factory = SAXParserFactory.newInstance();
			// factory.setNamespaceAware(true);
			SAXParser parser = factory.newSAXParser();
			parser.parse(source, new BuildConfigHandler(config, parser.getXMLReader()));
			return config;
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

	public BuildConfigHandler(XMLReader inReader, DefaultHandler inParent)
	{
		super(inReader, inParent);
	}

	protected BuildConfigHandler(BuildConfig inConfig, XMLReader inReader)
	{
		super(inReader, null);
		config = inConfig;
	}

	@Override
	public void endElement(String uri, String localName, String name) throws SAXException
	{
		super.endElement(uri, localName, name);
		if (version != null)
		{
			if ("buildconfiguration".equals(name))
			{
				returnHandler();
			}
			else {
				endUnknownElement(uri, localName, name);
			}
		}
		else {
			endUnknownElement(uri, localName, name);
		}
	}

	@Override
	public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException
	{
		super.startElement(uri, localName, name, attributes);
		if (version == null)
		{
			if ("buildconfiguration".equals(name))
			{
				version = attributes.getValue("version");
				if (version == null)
				{
					version = "2.0";
				}
			}
			else {
				startUnknownElement(uri, localName, name, attributes);
			}
		}
		else
		{
			// don't process anything else unless a version has been defined
			if ("settings".equals(name))
			{
				reader.setContentHandler(new SettingsHandler(reader, this));
				reader.getContentHandler().startElement(uri, localName, name, attributes);
			}
			else if ("project".equals(name))
			{
				reader.setContentHandler(new ProjectHandler(reader, this));
				reader.getContentHandler().startElement(uri, localName, name, attributes);
			}
			else if ("filters".equals(name))
			{
				reader.setContentHandler(new FilterHandler(reader, this));
				reader.getContentHandler().startElement(uri, localName, name, attributes);
			}
			else {
				startUnknownElement(uri, localName, name, attributes);
			}
		}
	}

	public static void main(String[] args)
	{
		try
		{
			org.apache.log4j.BasicConfigurator.configure();
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
