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

package Composestar.Core.CKRET.Config.Xml;

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

import Composestar.Core.CKRET.CKRET;
import Composestar.Core.CKRET.SECRETResources;
import Composestar.Core.CKRET.Config.ConflictRule;
import Composestar.Core.CKRET.Config.Resource;
import Composestar.Core.CKRET.Config.ResourceType;
import Composestar.Core.Config.Xml.CpsBaseHandler;
import Composestar.Core.Exception.ConfigurationException;
import Composestar.Utils.Logging.CPSLogger;

/**
 * Load SECRET configuration from an XML file.
 * 
 * @author Michiel Hendriks
 */
public class XmlConfiguration extends CpsBaseHandler
{
	protected static final CPSLogger logger = CPSLogger.getCPSLogger(CKRET.MODULE_NAME + ".Config");

	protected static final int STATE_SECRET = 1;

	protected SECRETResources resources;

	protected ResourceHandler resh;

	protected RuleHandler ruleh;

	protected ActionHandler acth;

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
		resources = resc;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.Config.Xml.CpsBaseHandler#startElement(java.lang.String,
	 *      java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	@Override
	public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException
	{
		super.startElement(uri, localName, name, attributes);
		if (state == 0 && "secret".equals(name))
		{
			state = STATE_SECRET;
		}
		else if (state == STATE_SECRET && "resource".equals(name))
		{
			if (resh == null)
			{
				resh = new ResourceHandler(reader, this);
			}
			reader.setContentHandler(resh);
			resh.startElement(uri, localName, name, attributes);
		}
		else if (state == STATE_SECRET && "rule".equals(name))
		{
			if (ruleh == null)
			{
				ruleh = new RuleHandler(reader, this);
			}
			reader.setContentHandler(ruleh);
			ruleh.startElement(uri, localName, name, attributes);
		}
		else if (state == STATE_SECRET && "action".equals(name))
		{
			if (acth == null)
			{
				acth = new ActionHandler(reader, this);
			}
			reader.setContentHandler(acth);
			acth.startElement(uri, localName, name, attributes);
		}
		else
		{
			startUnknownElement(uri, localName, name, attributes);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String,
	 *      java.lang.String, java.lang.String)
	 */
	@Override
	public void endElement(String uri, String localName, String name) throws SAXException
	{
		super.endElement(uri, localName, name);
		if (state == STATE_SECRET && "secret".equals(name))
		{
			returnHandler(uri, localName, name);
		}
		else if (state == STATE_SECRET && "resource".equals(name))
		{
			Resource resc = resh.getRecource();
			if (resh.replaceResource())
			{
				Resource r2 = resources.getResource(resc.getName());
				r2.clearVocabulary();
			}
			// will automatically merge if needed
			resources.addResource(resc);
		}
		else if (state == STATE_SECRET && "rule".equals(name))
		{
			ConflictRule rule = ruleh.getRule();
			String resc = ruleh.getRecource();
			if (rule != null)
			{
				ResourceType rt = ResourceType.parse(resc);
				Resource r2;
				if (rt == ResourceType.Custom)
				{
					r2 = resources.getResource(resc);
				}
				else
				{
					r2 = resources.getResource(rt.toString());
				}

				if (r2 == null)
				{
					// doesn't exist yet?
					try
					{
						r2 = ResourceType.createResource(resc, true);
					}
					catch (IllegalArgumentException e)
					{
						throw new SAXParseException(e.toString(), locator);
					}
					if (!r2.getType().isMeta())
					{
						resources.addResource(r2);
					}
				}

				rule.setResource(r2);
				resources.addRule(rule);
			}
		}
		else if (state == STATE_SECRET && "action".equals(name))
		{
			resources.addOperationSequence(acth.getAction());
		}
		else
		{
			endUnknownElement(uri, localName, name);
		}
	}
}