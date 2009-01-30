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

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Map.Entry;

import Composestar.Perf.Data.Report;
import Composestar.Perf.Data.ReportBundle;
import Composestar.Perf.Data.TimerEvent;
import Composestar.Perf.Data.TimerReport;

/**
 * Basic exported
 * 
 * @author Michiel Hendriks
 */
public abstract class Exporter
{
	/**
	 * The bundle to export
	 */
	protected ReportBundle bundle;

	/**
	 * The reports actually being exported
	 */
	protected SortedSet<Report> reports;

	/**
	 * A list of all timers in all reports
	 */
	protected SortedMap<String, SortedSet<TimerReport>> timers;

	/**
	 * Only the latest X reports
	 */
	protected int onlyLatestX = Integer.getInteger("Composestar.Perf.Tool.Export.onlyLastX", 0);

	protected Exporter(ReportBundle bundleToExport)
	{
		bundle = bundleToExport;
		timers = new TreeMap<String, SortedSet<TimerReport>>(String.CASE_INSENSITIVE_ORDER);
		init();
	}

	/**
	 * Initialize the exporter
	 */
	public void init()
	{
		reports = bundle.getReports();
		if (onlyLatestX > 0 && reports.size() > onlyLatestX)
		{
			int skip = reports.size() - onlyLatestX;
			Report cutOffAt = null;
			Iterator<Report> it = reports.iterator();
			while (skip > 0 && it.hasNext())
			{
				it.next();
				--skip;
			}
			if (it.hasNext())
			{
				cutOffAt = it.next();
			}
			if (cutOffAt != null)
			{
				reports = reports.tailSet(cutOffAt);
			}
		}
		for (Report report : reports)
		{
			for (String name : report.getTimerNames())
			{
				SortedSet<TimerReport> timerSet = timers.get(name);
				if (timerSet == null)
				{
					timerSet = new TreeSet<TimerReport>(new Comparator<TimerReport>()
					{
						public int compare(TimerReport o1, TimerReport o2)
						{
							// sort on the report date
							return o1.getReport().getReportDate().compareTo(o2.getReport().getReportDate());
						}
					});
					timers.put(name, timerSet);
				}
				timerSet.add(report.getTimerReport(name));
			}
		}
	}

	/**
	 * Export the data;
	 * 
	 * @return TODO
	 * @throws Exception TODO
	 */
	public boolean export() throws Exception
	{
		for (Entry<String, SortedSet<TimerReport>> entry : timers.entrySet())
		{
			exportTimerSet(entry.getKey(), entry.getValue());
		}
		return true;
	}

	/**
	 * @param key
	 * @param value
	 * @throws Exception TODO
	 */
	protected void exportTimerSet(String name, SortedSet<TimerReport> timerSet) throws Exception
	{
		SortedMap<String, SortedSet<TimerEvent>> events = getTimerEvents(timerSet);
		for (Entry<String, SortedSet<TimerEvent>> entry : events.entrySet())
		{
			exportTimerEvent(entry.getKey(), entry.getValue());
		}
	}

	/**
	 * @param description
	 * @param events
	 * @throws Exception TODO
	 */
	protected abstract void exportTimerEvent(String description, SortedSet<TimerEvent> events) throws Exception;

	/**
	 * Return a map of all events for the given timer set
	 * 
	 * @param timerSet
	 * @return
	 */
	protected SortedMap<String, SortedSet<TimerEvent>> getTimerEvents(Collection<TimerReport> timerSet)
	{
		SortedMap<String, SortedSet<TimerEvent>> result =
				new TreeMap<String, SortedSet<TimerEvent>>(String.CASE_INSENSITIVE_ORDER);
		for (TimerReport report : timerSet)
		{
			for (TimerEvent event : report.getEvents())
			{
				SortedSet<TimerEvent> eventSet = result.get(event.getDescription());
				if (eventSet == null)
				{
					eventSet = new TreeSet<TimerEvent>(new Comparator<TimerEvent>()
					{
						// sort on the report date
						public int compare(TimerEvent o1, TimerEvent o2)
						{
							return o1.getReport().getReportDate().compareTo(o2.getReport().getReportDate());
						}
					});
					result.put(event.getDescription(), eventSet);
				}
				eventSet.add(event);
			}
		}
		return result;
	}
}
