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
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import Composestar.Core.Config.OSFilter;

/**
 * Processes an OS filter definition
 * 
 * @author Michiel Hendriks
 */
public class OSFilterHandler extends CpsBaseHandler
{
	/**
	 * Processing an &lt;osfilter&gt; element
	 */
	protected static final int STATE_OSFILTER = 1;

	/**
	 * The resulting OS filter definition
	 */
	protected OSFilter osfilter;

	public OSFilterHandler(XMLReader inReader, DefaultHandler inParent)
	{
		super(inReader, inParent);
	}

	/**
	 * @return the parsed osfilter
	 */
	public OSFilter getOSFilter()
	{
		return osfilter;
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
		if (state == 0 && "osfilter".equals(name))
		{
			state = STATE_OSFILTER;
			osfilter = new OSFilter();
			osfilter.setName(attributes.getValue("name"));
			osfilter.setVersion(attributes.getValue("version"));
			osfilter.setArch(attributes.getValue("arch"));
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
		if (state == STATE_OSFILTER && "osfilter".equals(name))
		{
			returnHandler(uri, localName, name);
			osfilter = null;
		}
		else
		{
			endUnknownElement(uri, localName, name);
		}

	}

}
