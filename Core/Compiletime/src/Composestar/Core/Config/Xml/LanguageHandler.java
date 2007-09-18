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

import Composestar.Core.Config.Language;

/**
 * @author Michiel Hendriks
 */
public class LanguageHandler extends CpsBaseHandler
{
	protected static final int STATE_LANGUAGE = 1;

	protected static final int STATE_EXTENSIONS = 2;

	protected static final int STATE_DUMMY = 3;

	protected Language language;

	protected CompilerHandler compilerHandler;

	public LanguageHandler(XMLReader inReader, DefaultHandler inParent)
	{
		super(inReader, inParent);
	}

	public Language getLanguage()
	{
		return language;
	}

	@Override
	public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException
	{
		super.startElement(uri, localName, name, attributes);
		if (state == 0 && "language".equals(name))
		{
			state = STATE_LANGUAGE;
			try
			{
				language = new Language(attributes.getValue("name"));
			}
			catch (IllegalArgumentException e)
			{
				throw new SAXParseException(e.getMessage(), locator);
			}
		}
		else if (state == STATE_LANGUAGE && "extensions".equals(name))
		{
			state = STATE_EXTENSIONS;
		}
		else if (state == STATE_EXTENSIONS && "extension".equals(name))
		{
			// nop
		}
		else if (state == STATE_LANGUAGE && "dummygenerator".equals(name))
		{
			state = STATE_DUMMY;
		}
		else if (state == STATE_LANGUAGE && "compiler".equals(name))
		{
			if (compilerHandler == null)
			{
				compilerHandler = new CompilerHandler(reader, this);
			}
			reader.setContentHandler(compilerHandler);
			compilerHandler.startElement(uri, localName, name, attributes);
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
		if (state == STATE_LANGUAGE && "language".equals(name))
		{
			returnHandler(uri, localName, name);
			language = null;
		}
		else if (state == STATE_EXTENSIONS && "extensions".equals(name))
		{
			state = STATE_LANGUAGE;
		}
		else if (state == STATE_EXTENSIONS && "extension".equals(name))
		{
			try
			{
				language.addExtension(charData.toString().trim());
			}
			catch (IllegalArgumentException e)
			{
				throw new SAXParseException(e.getMessage(), locator);
			}
		}
		else if (state == STATE_DUMMY && "dummygenerator".equals(name))
		{
			state = STATE_LANGUAGE;
			try
			{
				language.setDummyGenerator(charData.toString().trim());
			}
			catch (IllegalArgumentException e)
			{
				throw new SAXParseException(e.getMessage(), locator);
			}
		}
		else if (state == STATE_LANGUAGE && "compiler".equals(name))
		{
			if (compilerHandler != null)
			{
				try
				{
					language.setCompiler(compilerHandler.getCompiler());
				}
				catch (IllegalArgumentException e)
				{
					throw new SAXParseException(e.getMessage(), locator);
				}
			}
		}
		else
		{
			endUnknownElement(uri, localName, name);
		}
	}
}
