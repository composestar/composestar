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

import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

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

	protected XMLReader reader;

	protected Locator locator;

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

	@Override
	public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException
	{
		super.startElement(uri, localName, name, attributes);
		if (charData.length() > 0)
		{
			charData = new StringBuffer();
		}
	}

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

	public void startUnknownElement(String uri, String localName, String name, Attributes attributes)
			throws SAXException
	{
		String msg = String.format(
				"%s encountered an unknown element at %d:%d qname=%s, localName=%s, uri=%s, state=%d", getClass()
						.getSimpleName(), locator.getLineNumber(), locator.getColumnNumber(), name, localName, uri,
				state);
		logger.info(msg);
	}

	public void endUnknownElement(String uri, String localName, String name) throws SAXException
	{
	// System.err.println("Encountered unknown element uri:" + uri + "
	// localName:" + localName + " name:" + name);
	}

	@Override
	public void setDocumentLocator(Locator loc)
	{
		super.setDocumentLocator(loc);
		locator = loc;
	}

}
