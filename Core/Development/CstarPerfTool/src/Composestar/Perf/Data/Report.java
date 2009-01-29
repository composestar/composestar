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

package Composestar.Perf.Data;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * A single report
 * 
 * @author Michiel Hendriks
 */
public class Report
{
	/**
	 * Location of the report file
	 */
	protected File location;

	/**
	 * Time the report was created
	 */
	protected Date reportDate;

	/**
	 * The start offset for all events
	 */
	protected long timestamp;

	/**
	 * All timer present in this report (including children of timers)
	 */
	protected Map<String, TimerReport> reportsByName;

	/**
	 * All base timer reports (does not include the children), sorted by
	 * creation time
	 */
	protected SortedSet<TimerReport> timerReports;

	public Report(File reportLocation)
	{
		location = reportLocation;
		reportsByName = new HashMap<String, TimerReport>();
		timerReports = new TreeSet<TimerReport>(new Comparator<TimerReport>()
		{
			public int compare(TimerReport o1, TimerReport o2)
			{
				long res = o1.getTimestamp() - o2.getTimestamp();
				if (res > 0)
				{
					return 1;
				}
				else if (res < 0)
				{
					return -1;
				}
				return 0;
			}
		});
	}

	/**
	 * @return the location
	 */
	public File getLocation()
	{
		return location;
	}

	/**
	 * @param value the timestamp to set
	 */
	public void setReportDate(Date value)
	{
		reportDate = value;
	}

	/**
	 * @return the timestamp
	 */
	public Date getReportDate()
	{
		return reportDate;
	}

	/**
	 * @return the timestamp
	 */
	public long getTimestamp()
	{
		return timestamp;
	}

	/**
	 * @param value the timestamp to set
	 */
	public void setTimestamp(long value)
	{
		timestamp = value;
	}

	/**
	 * Convert a timestamp used by the TimerReport and events to a date
	 * 
	 * @param ts
	 * @return
	 */
	public Date timestampToDate(long ts)
	{
		long diff = ts - timestamp;
		return new Date(reportDate.getTime() + (diff / 1000000));
	}

	/**
	 * @return the timerReports
	 */
	public SortedSet<TimerReport> getTimerReports()
	{
		return Collections.unmodifiableSortedSet(timerReports);
	}

	/**
	 * Get a timer report by name
	 * 
	 * @param name
	 * @return
	 */
	public TimerReport getTimerReport(String name)
	{
		return reportsByName.get(name);
	}

	/**
	 * Get all timer names
	 * 
	 * @return
	 */
	public Set<String> getTimerNames()
	{
		return Collections.unmodifiableSet(reportsByName.keySet());
	}

	/**
	 * @param timerReport
	 */
	public void addTimerReport(TimerReport timerReport)
	{
		timerReport.setReport(this);
		reportsByName.put(timerReport.getName(), timerReport);
		timerReports.add(timerReport);
		Queue<TimerReport> regNames = new LinkedList<TimerReport>();
		regNames.addAll(timerReport.getChildren());
		while (!regNames.isEmpty())
		{
			TimerReport item = regNames.remove();
			reportsByName.put(item.getName(), item);
			regNames.addAll(item.getChildren());
		}
	}
}
