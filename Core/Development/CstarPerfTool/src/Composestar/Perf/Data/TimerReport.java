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

import java.util.Collections;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * A report for a single timer
 * 
 * @author Michiel Hendriks
 */
public class TimerReport
{
	/**
	 * The (full) name of this timer
	 */
	protected String name;

	/**
	 * The timestamp this reported was created
	 */
	protected long timestamp;

	/**
	 * Reports of child timers
	 */
	protected SortedSet<TimerReport> children;

	/**
	 * Events in this timer
	 */
	protected SortedSet<TimerEvent> events;

	protected TimerReport parent;

	protected Report report;

	public TimerReport(String timerName)
	{
		children = new TreeSet<TimerReport>(new Comparator<TimerReport>()
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
		events = new TreeSet<TimerEvent>(new Comparator<TimerEvent>()
		{
			public int compare(TimerEvent o1, TimerEvent o2)
			{
				long res = o1.getStartTime() - o2.getStartTime();
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
		name = timerName;
	}

	/**
	 * @return the name
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * @param value the timestamp to set
	 */
	public void setTimestamp(long value)
	{
		timestamp = value;
	}

	/**
	 * @return the timestamp
	 */
	public long getTimestamp()
	{
		return timestamp;
	}

	/**
	 * @return the children
	 */
	public SortedSet<TimerReport> getChildren()
	{
		return Collections.unmodifiableSortedSet(children);
	}

	/**
	 * @return the events
	 */
	public SortedSet<TimerEvent> getEvents()
	{
		return Collections.unmodifiableSortedSet(events);
	}

	/**
	 * Add a new child timer
	 * 
	 * @param child
	 */
	public void addChild(TimerReport child)
	{
		children.add(child);
		child.setParent(this);
	}

	/**
	 * Add a new event
	 * 
	 * @param event
	 */
	public void addEvent(TimerEvent event)
	{
		event.setTimerReport(this);
		events.add(event);
	}

	/**
	 * Get an event by its description
	 * 
	 * @param name
	 * @return
	 */
	public TimerEvent getEvent(String name)
	{
		for (TimerEvent event : events)
		{
			if (event.getDescription().equals(name))
			{
				return event;
			}
		}
		return null;
	}

	/**
	 * @return the parent
	 */
	public TimerReport getParent()
	{
		return parent;
	}

	/**
	 * @param value the parent to set
	 */
	public void setParent(TimerReport value)
	{
		this.parent = value;
	}

	/**
	 * @return the report
	 */
	public Report getReport()
	{
		if (report == null && parent != null)
		{
			return parent.getReport();
		}
		return report;
	}

	/**
	 * @param value the report to set
	 */
	public void setReport(Report value)
	{
		this.report = value;
	}

}
