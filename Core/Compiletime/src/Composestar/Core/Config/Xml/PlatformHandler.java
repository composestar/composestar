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
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import Composestar.Core.Config.Platform;

/**
 * SAX Handler for the platform element in a platform configuration
 * 
 * @author Michiel Hendriks
 */
public class PlatformHandler extends CpsBaseHandler
{
	/**
	 * Handling a platform element
	 */
	protected static final int STATE_PLATFORM = 1;

	/**
	 * Handling a languages element
	 */
	protected static final int STATE_LANGUAGES = 2;

	/**
	 * OSFilter handler
	 */
	protected OSFilterHandler osfilterHandler;

	/**
	 * Language element handler
	 */
	protected LanguageHandler languageHandler;

	/**
	 * Resource type handler
	 */
	protected ResourceTypeHandler resourceHandler;

	/**
	 * The resulting platform definition
	 */
	protected Platform platform;

	public PlatformHandler(XMLReader inReader, DefaultHandler inParent)
	{
		super(inReader, inParent);
	}

	/**
	 * @return the defined platform
	 */
	public Platform getPlatform()
	{
		return platform;
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
		if (state == 0 && "platform".equals(name))
		{
			state = STATE_PLATFORM;
			try
			{
				platform = new Platform(attributes.getValue("id"));
			}
			catch (IllegalArgumentException e)
			{
				throw new SAXParseException(e.getMessage(), locator);
			}
		}
		else if (state == STATE_PLATFORM && "osfilter".equals(name))
		{
			if (osfilterHandler == null)
			{
				osfilterHandler = new OSFilterHandler(reader, this);
			}
			reader.setContentHandler(osfilterHandler);
			osfilterHandler.startElement(uri, localName, name, attributes);

		}
		else if (state == STATE_PLATFORM && "languages".equals(name))
		{
			state = STATE_LANGUAGES;
		}
		else if (state == STATE_LANGUAGES && "language".equals(name))
		{
			if (languageHandler == null)
			{
				languageHandler = new LanguageHandler(reader, this);
			}
			reader.setContentHandler(languageHandler);
			languageHandler.startElement(uri, localName, name, attributes);
		}
		else if (state == STATE_PLATFORM && "resources".equals(name))
		{
			if (resourceHandler == null)
			{
				resourceHandler = new ResourceTypeHandler(reader, this, "resources");
			}
			reader.setContentHandler(resourceHandler);
			resourceHandler.startElement(uri, localName, name, attributes);

		}
		else
		{
			startUnknownElement(uri, localName, name, attributes);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.Config.Xml.CpsBaseHandler#endElement(java.lang.String,
	 *      java.lang.String, java.lang.String)
	 */
	@Override
	public void endElement(String uri, String localName, String name) throws SAXException
	{
		super.endElement(uri, localName, name);
		if (state == STATE_PLATFORM && "platform".equals(name))
		{
			returnHandler(uri, localName, name);
			platform = null;
		}
		else if (state == STATE_PLATFORM && "osfilter".equals(name))
		{
			if (osfilterHandler != null)
			{
				platform.setOSFilter(osfilterHandler.getOSFilter());
				osfilterHandler = null;
			}
		}
		else if (state == STATE_LANGUAGES && "languages".equals(name))
		{
			state = STATE_PLATFORM;
		}
		else if (state == STATE_LANGUAGES && "language".equals(name))
		{
			if (languageHandler != null)
			{
				try
				{
					platform.addLanguage(languageHandler.getLanguage());
				}
				catch (IllegalArgumentException e)
				{
					throw new SAXParseException(e.getMessage(), locator);
				}
				languageHandler = null;
			}
		}
		else if (state == STATE_PLATFORM && "resources".equals(name))
		{
			if (resourceHandler != null)
			{
				platform.addResources(resourceHandler.getResources());
				resourceHandler = null;
			}
		}
		else
		{
			endUnknownElement(uri, localName, name);
		}
	}
}
