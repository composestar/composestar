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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.naming.ConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import Composestar.Perf.Data.Report;

/**
 * @author Michiel Hendriks
 */
public class TimerResultsHandler extends BaseHandler
{
	public static final int STATE_ROOT = 1;

	public static final int STATE_TIMER = 2;

	/**
	 * Load the configuration from the specified file.
	 * 
	 * @param file
	 * @param resc
	 * @throws IOException
	 * @throws ConfigurationException
	 */
	public static boolean loadReport(File file, Report report) throws IOException
	{
		if (file == null)
		{
			throw new IllegalArgumentException("file can not be null");
		}
		return loadReport(getInputStream(file), report);
	}

	/**
	 * Loads the configuration from the stream.
	 * 
	 * @param stream
	 * @param resc
	 * @throws ConfigurationException
	 */
	public static boolean loadReport(InputStream stream, Report report)
	{
		try
		{
			SAXParserFactory factory = SAXParserFactory.newInstance();
			factory.setNamespaceAware(true);
			SAXParser parser;
			parser = factory.newSAXParser();
			TimerResultsHandler dh = new TimerResultsHandler(parser.getXMLReader(), null, report);
			parser.parse(stream, dh);
			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace(System.err);
			return false;
		}
	}

	/**
	 * The report to be filled
	 */
	protected Report report;

	protected TimerHandler timerHandler;

	/**
	 * @param inReader
	 * @param inParent
	 */
	public TimerResultsHandler(XMLReader inReader, DefaultHandler inParent, Report targetReport)
	{
		super(inReader, inParent);
		report = targetReport;
	}

	public static final SimpleDateFormat TS_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssz");

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
			state = STATE_ROOT;
			String tsString = attributes.getValue("timestamp");
			if (tsString != null)
			{
				try
				{
					report.setReportDate(TS_FORMAT.parse(tsString));
				}
				catch (ParseException e)
				{
					throw new SAXException(e);
				}
			}
			else
			{
				throw new IllegalStateException("No timestamp attribute");
			}
			report.setTimestamp(Long.parseLong(attributes.getValue("creation")));
		}
		else if (state == STATE_ROOT && "timer".equals(currentName))
		{
			state = STATE_TIMER;
			timerHandler = new TimerHandler(reader, this);
			reader.setContentHandler(timerHandler);
			timerHandler.startElement(uri, localName, name, attributes);
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
		if (state == STATE_ROOT && "timer".equals(currentName))
		{
			returnHandler(uri, localName, name);
		}
		else if (state == STATE_TIMER && "timer".equals(currentName))
		{
			state = STATE_ROOT;
			if (timerHandler != null)
			{
				report.addTimerReport(timerHandler.getTimerReport());
			}
		}
		else
		{
			endUnknownElement(uri, localName, name);
		}
	}

}
