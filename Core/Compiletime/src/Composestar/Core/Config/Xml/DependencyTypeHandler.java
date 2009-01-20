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
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import Composestar.Core.Config.Dependency;

/**
 * Handles source types
 * 
 * @author Michiel Hendriks
 */
public class DependencyTypeHandler extends CpsBaseHandler
{
	protected static final int STATE_MULTI = 1;

	/**
	 * The tag to match when multiple is supported;
	 */
	protected String groupTag;

	/**
	 * If true multiple source are supported
	 */
	protected boolean supportMulti;

	/**
	 * Set of dependencies that are returned
	 */
	protected Set<Dependency> deps;

	/**
	 * Parse multiple source files;
	 * 
	 * @param inReader
	 * @param inParent
	 * @param inGroupTag
	 */
	public DependencyTypeHandler(XMLReader inReader, DefaultHandler inParent, String inGroupTag)
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
		deps = new HashSet<Dependency>();
	}

	/**
	 * @return the parsed dependencies
	 */
	public Set<Dependency> getDependencies()
	{
		return deps;
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
		if (state == STATE_MULTI && supportMulti && groupTag.equals(name))
		{
			returnHandler(uri, localName, name);
		}
		else if (state == STATE_MULTI && "file".equals(name))
		{
			String fn = charData.toString().trim();
			if (fn.length() > 0)
			{
				deps.add(new Dependency(new File(fn)));
			}
			if (!supportMulti)
			{
				returnHandler();
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
		if (state == 0 && supportMulti && groupTag.equals(name))
		{
			state = STATE_MULTI;
		}
		else if (state == STATE_MULTI && "file".equals(name))
		{
			// nop
		}
		else
		{
			startUnknownElement(uri, localName, name, attributes);
		}
	}
}
