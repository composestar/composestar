/*
 * This file is part of the Compose* project.
 * http://composestar.sourceforge.net
 * Copyright (C) 2009 University of Twente.
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

package Composestar.Perf.Data.Xml;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import Composestar.Perf.Data.TimerEvent;

/**
 * @author Michiel Hendriks
 */
public class EventHandler extends BaseHandler
{
	public static final int STATE_EVENT = 1;

	protected TimerEvent report;

	/**
	 * @param inReader
	 * @param inParent
	 */
	public EventHandler(XMLReader inReader, DefaultHandler inParent)
	{
		super(inReader, inParent);
	}

	/**
	 * @return
	 */
	public TimerEvent getEventReport()
	{
		return report;
	}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Perf.Data.Xml.BaseHandler#startElement(java.lang.String,
	 * java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	@Override
	public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException
	{
		super.startElement(uri, localName, name, attributes);
		if (state == 0 && "event".equals(currentName))
		{
			state = STATE_EVENT;
			report = new TimerEvent();
			report.setDuration(Long.parseLong(attributes.getValue("duration")));
			report.setMemoryDelta(Long.parseLong(attributes.getValue("memory")));
			report.setStartTime(Long.parseLong(attributes.getValue("start")));
			report.setEndTime(Long.parseLong(attributes.getValue("stop")));
		}
		else
		{
			startUnknownElement(uri, localName, name, attributes);
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
		if (state == STATE_EVENT && "event".equals(currentName))
		{
			report.setDescription(charData.toString().trim());
			returnHandler(uri, localName, name);
		}
		else
		{
			endUnknownElement(uri, localName, name);
		}
	}
}
