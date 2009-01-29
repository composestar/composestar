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

package Composestar.Perf.Export;

import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.SortedSet;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import Composestar.Perf.Data.Report;
import Composestar.Perf.Data.ReportBundle;
import Composestar.Perf.Data.TimerEvent;
import Composestar.Perf.Data.TimerReport;

/**
 * @author Michiel Hendriks
 */
public class XMLExport extends Exporter
{
	protected XMLStreamWriter output;

	public static final SimpleDateFormat TS_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssz");

	/**
	 * @param bundleToExport
	 */
	public XMLExport(ReportBundle bundleToExport, OutputStream stream)
	{
		super(bundleToExport);
		try
		{
			output = XMLOutputFactory.newInstance().createXMLStreamWriter(stream);
		}
		catch (XMLStreamException e)
		{
			output = null;
			e.printStackTrace();
		}
		catch (FactoryConfigurationError e)
		{
			output = null;
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Perf.Export.Exporter#export()
	 */
	@Override
	public boolean export() throws Exception
	{
		if (output == null)
		{
			return false;
		}
		output.writeStartDocument();
		output.writeStartElement("reports");
		output.writeAttribute("name", bundle.getDirectory().getName());
		for (Report report : reports)
		{
			output.writeStartElement("report");
			output.writeAttribute("id", String.format("report%d", System.identityHashCode(report)));
			output.writeAttribute("timestamp", Long.toString(report.getReportDate().getTime()));
			output.writeAttribute("datetime", TS_FORMAT.format(report.getReportDate()));
			output.writeEndElement();
		}
		super.export();
		output.writeEndElement();
		output.writeEndDocument();
		output.flush();
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Perf.Export.Exporter#exportTimerSet(java.lang.String,
	 * java.util.SortedSet)
	 */
	@Override
	protected void exportTimerSet(String name, SortedSet<TimerReport> timerSet) throws Exception
	{
		output.writeStartElement("timer");
		output.writeAttribute("name", name);
		super.exportTimerSet(name, timerSet);
		output.writeEndElement();
	}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Perf.Export.Exporter#exportTimerEvent(java.lang.String,
	 * java.util.SortedSet)
	 */
	@Override
	protected void exportTimerEvent(String description, SortedSet<TimerEvent> events) throws Exception
	{
		output.writeStartElement("event");

		long minDur = Long.MAX_VALUE;
		long maxDur = 0;
		long totDur = 0;

		for (TimerEvent event : events)
		{
			long dur = event.getDuration();
			totDur += dur;
			if (dur > maxDur)
			{
				maxDur = dur;
			}
			if (dur < minDur)
			{
				minDur = dur;
			}
		}
		output.writeAttribute("maximum", Long.toString(maxDur));
		output.writeAttribute("minimum", Long.toString(minDur));
		output.writeAttribute("average", Long.toString(totDur / events.size()));

		output.writeStartElement("description");
		output.writeCData(description);
		output.writeEndElement();
		for (TimerEvent event : events)
		{
			output.writeStartElement("occurance");
			output.writeAttribute("report", String.format("report%d", System.identityHashCode(event.getReport())));
			output.writeAttribute("duration", Long.toString(event.getDuration()));
			output.writeAttribute("memory", Long.toString(event.getMemoryDelta()));
			output.writeEndElement();
		}
		output.writeEndElement();
	}
}
