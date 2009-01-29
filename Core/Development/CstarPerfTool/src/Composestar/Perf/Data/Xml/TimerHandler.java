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

import Composestar.Perf.Data.TimerReport;

/**
 * @author Michiel Hendriks
 */
public class TimerHandler extends BaseHandler
{
	public static final int STATE_TIMER = 1;

	public static final int STATE_SUBTIMER = 2;

	public static final int STATE_EVENT = 3;

	protected TimerReport report;

	protected TimerHandler timerHandler;

	protected EventHandler eventHandler;

	/**
	 * @param inReader
	 * @param inParent
	 */
	public TimerHandler(XMLReader inReader, DefaultHandler inParent)
	{
		super(inReader, inParent);
	}

	/**
	 * @return
	 */
	public TimerReport getTimerReport()
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
		if (state == 0 && "timer".equals(currentName))
		{
			state = STATE_TIMER;
			String tsString = attributes.getValue("name");
			if (tsString == null)
			{
				throw new IllegalStateException("No name attribute");
			}
			report = new TimerReport(tsString);
			tsString = attributes.getValue("creation");
			if (tsString != null)
			{
				long ts = Long.parseLong(tsString);
				report.setTimestamp(ts);
			}
			else
			{
				throw new IllegalStateException("No creation attribute");
			}
		}
		else if (state == STATE_TIMER && "timer".equals(currentName))
		{
			state = STATE_SUBTIMER;
			timerHandler = new TimerHandler(reader, this);
			reader.setContentHandler(timerHandler);
			timerHandler.startElement(uri, localName, name, attributes);
		}
		else if (state == STATE_TIMER && "event".equals(currentName))
		{
			state = STATE_EVENT;
			eventHandler = new EventHandler(reader, this);
			reader.setContentHandler(eventHandler);
			eventHandler.startElement(uri, localName, name, attributes);
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
		if (state == STATE_TIMER && "timer".equals(currentName))
		{
			returnHandler(uri, localName, name);
		}
		else if (state == STATE_SUBTIMER && "timer".equals(currentName))
		{
			state = STATE_TIMER;
			if (timerHandler != null)
			{
				report.addChild(timerHandler.getTimerReport());
			}
		}
		else if (state == STATE_EVENT && "event".equals(currentName))
		{
			state = STATE_TIMER;
			if (eventHandler != null)
			{
				report.addEvent(eventHandler.getEventReport());
			}
		}
		else
		{
			endUnknownElement(uri, localName, name);
		}
	}

}
