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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import Composestar.Core.Exception.ConfigurationException;
import Composestar.Utils.Logging.CPSLogger;

/**
 * The default handler for the configuration files. Automatically processes the
 * char data to convenient access. And keeps track of the parent handlers.
 * 
 * @author Michiel Hendriks
 */
public abstract class CpsBaseHandler extends DefaultHandler
{
	protected static final CPSLogger logger = CPSLogger.getCPSLogger("Configuration.XML");

	/**
	 * The current XMLReader instance
	 */
	protected XMLReader reader;

	/**
	 * Locator used for detailed parsing error reporting
	 */
	protected Locator locator;

	/**
	 * The parent handler that received handling when this handler is done
	 */
	protected DefaultHandler parent;

	/**
	 * Can be used to keep state
	 */
	protected int state;

	/**
	 * Will contain the contents of the character data. It is reset on every new
	 * element.
	 */
	protected StringBuffer charData;

	/**
	 * Must be set to the namespace this handler expects
	 */
	protected String namespace;

	/**
	 * Will contain the current normalized tagname
	 */
	protected String currentName;

	/**
	 * Get the inputstream, will detect if it's a gzip compressed file.
	 * 
	 * @param file
	 * @return
	 * @throws ConfigurationException
	 */
	protected static InputStream getInputStream(File file) throws ConfigurationException
	{
		try
		{
			InputStream is = new BufferedInputStream(new FileInputStream(file));
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
			return is;
		}
		catch (FileNotFoundException e)
		{
			throw new ConfigurationException("Build configuration file not found: " + file);
		}
	}

	public CpsBaseHandler(XMLReader inReader, DefaultHandler inParent)
	{
		super();
		reader = inReader;
		parent = inParent;
		charData = new StringBuffer();
		if (parent instanceof CpsBaseHandler)
		{
			setDocumentLocator(((CpsBaseHandler) parent).locator);
		}
	}

	/**
	 * Normalizes the current tag name. Returns the normalized an accepted name,
	 * or null when not accepted.
	 * 
	 * @param uri
	 * @param localName
	 * @param name
	 * @throws SAXException
	 */
	protected String normalizeName(String uri, String localName, String name)
	{
		if ("".equals(uri))
		{
			return name;
		}
		else if (namespace != null && namespace.equals(uri))
		{
			return localName;
		}
		else
		{
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String,
	 *      java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	@Override
	public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException
	{
		super.startElement(uri, localName, name, attributes);
		currentName = normalizeName(uri, localName, name);
		if (charData.length() > 0)
		{
			charData = new StringBuffer();
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
		currentName = normalizeName(uri, localName, name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
	 */
	@Override
	public void characters(char[] ch, int start, int length) throws SAXException
	{
		charData.append(ch, start, length);
	}

	/**
	 * Return to the parent handler
	 */
	protected void returnHandler()
	{
		state = 0;
		if (parent != null)
		{
			reader.setContentHandler(parent);
		}
	}

	/**
	 * Return to the parent handler and send the current end tag data
	 * 
	 * @param uri
	 * @param localName
	 * @param name
	 * @throws SAXException
	 */
	protected void returnHandler(String uri, String localName, String name) throws SAXException
	{
		returnHandler();
		if (parent != null)
		{
			parent.endElement(uri, localName, name);
		}
	}

	/**
	 * Handles an unknown start element
	 * 
	 * @param uri
	 * @param localName
	 * @param name
	 * @param attributes
	 * @throws SAXException
	 */
	public void startUnknownElement(String uri, String localName, String name, Attributes attributes)
			throws SAXException
	{
		String msg = String.format(
				"%s encountered an unknown element at %d:%d qname=%s, localName=%s, uri=%s, state=%d", getClass()
						.getSimpleName(), locator.getLineNumber(), locator.getColumnNumber(), name, localName, uri,
				state);
		logger.warn(msg);
	}

	/**
	 * Handles an unknown end element
	 * 
	 * @param uri
	 * @param localName
	 * @param name
	 * @throws SAXException
	 */
	public void endUnknownElement(String uri, String localName, String name) throws SAXException
	{
		String msg = String.format(
				"%s encountered an unknown end element at %d:%d qname=%s, localName=%s, uri=%s, state=%d", getClass()
						.getSimpleName(), locator.getLineNumber(), locator.getColumnNumber(), name, localName, uri,
				state);
		logger.warn(msg);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xml.sax.helpers.DefaultHandler#setDocumentLocator(org.xml.sax.Locator)
	 */
	@Override
	public void setDocumentLocator(Locator loc)
	{
		super.setDocumentLocator(loc);
		locator = loc;
	}

}
