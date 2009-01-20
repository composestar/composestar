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

package Composestar.Core.SECRET3.Config.Xml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import Composestar.Core.Config.Xml.CpsBaseHandler;
import Composestar.Core.Exception.ConfigurationException;
import Composestar.Core.Master.ModuleNames;
import Composestar.Core.SECRET3.SECRETResources;
import Composestar.Utils.Logging.CPSLogger;

/**
 * Load SECRET configuration from an XML file.
 * 
 * @author Michiel Hendriks
 */
public class XmlConfiguration extends CpsBaseHandler
{
	protected static final CPSLogger logger = CPSLogger.getCPSLogger(ModuleNames.SECRET + ".Config");

	public static final String NAMESPACE = "http://composestar.sourceforge.net/schema/SecretConfiguration";

	protected static final int STATE_SECRET = 1;

	/**
	 * Currently processing an analysis block. This block is completely ignored
	 * for configuration
	 */
	protected static final int STATE_ANALYSIS = 2;

	protected SECRETResources resources;

	/**
	 * Handler for the resource elements
	 */
	protected ResourceHandler resh;

	/**
	 * Handler for the rule elements
	 */
	protected RuleHandler ruleh;

	/**
	 * Handler for the action elements
	 */
	protected ActionHandler acth;

	/**
	 * Flags that identify which sections has included. There is a strict order
	 * in which the elements should be passed. First resources, then rules, then
	 * actions.
	 */
	protected boolean[] hadSection = new boolean[3];

	/**
	 * Load the configuration from the specified file.
	 * 
	 * @param file
	 * @param resc
	 * @throws ConfigurationException
	 */
	public static void loadBuildConfig(File file, SECRETResources resc) throws ConfigurationException
	{
		if (file == null)
		{
			throw new IllegalArgumentException("file can not be null");
		}
		loadBuildConfig(getInputStream(file), resc);
	}

	/**
	 * Loads the configuration from the stream.
	 * 
	 * @param stream
	 * @param resc
	 * @throws ConfigurationException
	 */
	public static void loadBuildConfig(InputStream stream, SECRETResources resc) throws ConfigurationException
	{
		SAXParserFactory factory = SAXParserFactory.newInstance();
		try
		{
			factory.setNamespaceAware(true);
			SAXParser parser = factory.newSAXParser();
			XmlConfiguration dh = new XmlConfiguration(parser.getXMLReader(), null, resc);
			parser.parse(stream, dh);
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

	/**
	 * @param inReader
	 * @param inParent
	 */
	public XmlConfiguration(XMLReader inReader, DefaultHandler inParent, SECRETResources resc)
	{
		super(inReader, inParent);
		if (resc == null)
		{
			throw new IllegalArgumentException("SECRETResources can not be null");
		}
		namespace = NAMESPACE;
		resources = resc;
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
		if (state == 0 && ("secret".equals(name) || "secret".equals(localName)))
		{
			state = STATE_SECRET;
			hadSection[0] = false;
			hadSection[1] = false;
			hadSection[2] = false;
		}
		else if (state == STATE_SECRET && "resource".equals(currentName))
		{
			if (hadSection[1] || hadSection[2])
			{
				throw new SAXParseException("Resource element not allowed after action and rule elements", locator);
			}
			hadSection[0] = true;
			if (resh == null)
			{
				resh = new ResourceHandler(reader, this, resources);
			}
			reader.setContentHandler(resh);
			resh.startElement(uri, localName, name, attributes);
		}
		else if (state == STATE_SECRET && "action".equals(currentName))
		{
			if (hadSection[2])
			{
				throw new SAXParseException("Action element not allowed after rule elements", locator);
			}
			hadSection[1] = true;
			if (acth == null)
			{
				acth = new ActionHandler(reader, this, resources);
			}
			reader.setContentHandler(acth);
			acth.startElement(uri, localName, name, attributes);
		}
		else if (state == STATE_SECRET && "rule".equals(currentName))
		{
			hadSection[2] = true;
			if (ruleh == null)
			{
				ruleh = new RuleHandler(reader, this, resources);
			}
			reader.setContentHandler(ruleh);
			ruleh.startElement(uri, localName, name, attributes);
		}
		else if (state == STATE_SECRET && "analysis".equals(currentName))
		{
			state = STATE_ANALYSIS;
			// ignore this stuff
		}
		else
		{
			if (state != STATE_ANALYSIS)
			{
				startUnknownElement(uri, localName, name, attributes);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public void endElement(String uri, String localName, String name) throws SAXException
	{
		super.endElement(uri, localName, name);
		if (state == STATE_SECRET && "secret".equals(currentName))
		{
			returnHandler(uri, localName, name);
		}
		else if (state == STATE_ANALYSIS && "analysis".equals(currentName))
		{
			state = STATE_SECRET;
		}
		else
		{
			if (state != STATE_ANALYSIS)
			{
				endUnknownElement(uri, localName, name);
			}
		}
	}
}
