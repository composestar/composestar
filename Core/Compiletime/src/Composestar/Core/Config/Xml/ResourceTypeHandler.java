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
import java.util.HashSet;
import java.util.Set;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import Composestar.Core.Config.DirectoryResource;
import Composestar.Core.Config.FileCollection;

/**
 * Handles source types
 * 
 * @author Michiel Hendriks
 */
public class ResourceTypeHandler extends CpsBaseHandler
{
	protected static final int STATE_MULTI = 1;

	protected static final int STATE_DIR = 2;

	protected static final int STATE_DIR_INCLUDE = 3;

	protected static final int STATE_DIR_EXCLUDE = 4;

	/**
	 * The tag to match when multiple is supported;
	 */
	protected String groupTag;

	/**
	 * If true multiple source are supported
	 */
	protected boolean supportMulti;

	protected Set<FileCollection> resources;

	protected FileCollection fc;

	protected DirectoryResource dr;

	/**
	 * Parse multiple source files;
	 * 
	 * @param inReader
	 * @param inParent
	 * @param inGroupTag
	 */
	public ResourceTypeHandler(XMLReader inReader, DefaultHandler inParent, String inGroupTag)
	{
		super(inReader, inParent);
		if (inGroupTag != null)
		{
			groupTag = inGroupTag.trim();
		}
		supportMulti = groupTag != null && groupTag.length() > 0;
		if (supportMulti)
		{
			state = 0;
		}
		else
		{
			state = STATE_MULTI;
		}
		resources = new HashSet<FileCollection>();
	}

	public Set<FileCollection> getResources()
	{
		return resources;
	}

	@Override
	public void endElement(String uri, String localName, String name) throws SAXException
	{
		super.endElement(uri, localName, name);
		if (state == STATE_MULTI && supportMulti && groupTag.equals(name))
		{
			returnHandler(uri, localName, name);
			resources = new HashSet<FileCollection>();
		}
		else if (state == STATE_MULTI && "file".equals(name))
		{
			String fn = charData.toString().trim();
			if (fn.length() > 0)
			{
				if (fc == null)
				{
					fc = new FileCollection();
					resources.add(fc);
				}
				fc.addFile(fn);
			}
			if (!supportMulti)
			{
				returnHandler();
			}
		}
		else if (state == STATE_DIR && "dir".equals(name))
		{
			state = STATE_MULTI;
			if (!supportMulti)
			{
				returnHandler();
			}
		}
		else if (state == STATE_DIR_INCLUDE && "include".equals(name))
		{
			String mask = charData.toString().trim();
			if (mask.length() > 0)
			{
				try
				{
					dr.addInclude(mask);
				}
				catch (IllegalArgumentException e)
				{
					throw new SAXParseException(e.getMessage(), locator);
				}
			}
			state = STATE_DIR;
		}
		else if (state == STATE_DIR_EXCLUDE && "exclude".equals(name))
		{
			String mask = charData.toString().trim();
			if (mask.length() > 0)
			{
				try
				{
					dr.addExclude(mask);
				}
				catch (IllegalArgumentException e)
				{
					throw new SAXParseException(e.getMessage(), locator);
				}
			}
			state = STATE_DIR;
		}
		else
		{
			endUnknownElement(uri, localName, name);
		}
	}

	@Override
	public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException
	{
		super.startElement(uri, localName, name, attributes);
		if (state == 0 && supportMulti && groupTag.equals(name))
		{
			state = STATE_MULTI;
		}
		else if (state == STATE_MULTI && "file".equals(name))
		{
			// nop
		}
		else if (state == STATE_MULTI && "dir".equals(name))
		{
			state = STATE_DIR;
			dr = new DirectoryResource();
			try
			{
				dr.setPath(new File(attributes.getValue("path")));
			}
			catch (IllegalArgumentException e)
			{
				throw new SAXParseException(e.getMessage(), locator);
			}
			resources.add(dr);
		}
		else if (state == STATE_DIR && "include".equals(name))
		{
			state = STATE_DIR_INCLUDE;
		}
		else if (state == STATE_DIR && "exclude".equals(name))
		{
			state = STATE_DIR_EXCLUDE;
		}
		else
		{
			startUnknownElement(uri, localName, name, attributes);
		}
	}
}
