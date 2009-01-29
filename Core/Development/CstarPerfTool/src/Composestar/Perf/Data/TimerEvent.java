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

/**
 * An event
 * 
 * @author Michiel Hendriks
 */
public class TimerEvent
{
	/**
	 * Description of the event
	 */
	protected String description;

	/**
	 * Duration of the event
	 */
	protected long duration;

	/**
	 * (Estimated) difference is memory usage between start and end
	 */
	protected long memoryDelta;

	/**
	 * The time the event started, this is a System.nanoTime() value
	 */
	protected long startTime;

	/**
	 * The time the event ended
	 */
	protected long endTime;

	/**
	 * The owning timer report
	 */
	protected TimerReport timerReport;

	public TimerEvent()
	{}

	/**
	 * @return the description
	 */
	public String getDescription()
	{
		return description;
	}

	/**
	 * @param value the description to set
	 */
	public void setDescription(String value)
	{
		description = value;
	}

	/**
	 * @return the duration
	 */
	public long getDuration()
	{
		return duration;
	}

	/**
	 * @param value the duration to set
	 */
	public void setDuration(long value)
	{
		duration = value;
	}

	/**
	 * @return the memoryDelta
	 */
	public long getMemoryDelta()
	{
		return memoryDelta;
	}

	/**
	 * @param value the memoryDelta to set
	 */
	public void setMemoryDelta(long value)
	{
		memoryDelta = value;
	}

	/**
	 * @return the startTime
	 */
	public long getStartTime()
	{
		return startTime;
	}

	/**
	 * @param value the startTime to set
	 */
	public void setStartTime(long value)
	{
		startTime = value;
	}

	/**
	 * @return the endTime
	 */
	public long getEndTime()
	{
		return endTime;
	}

	/**
	 * @param value the endTime to set
	 */
	public void setEndTime(long value)
	{
		endTime = value;
	}

	/**
	 * @param timerReport the timerReport to set
	 */
	public void setTimerReport(TimerReport value)
	{
		timerReport = value;
	}

	/**
	 * @return the timerReport
	 */
	public TimerReport getTimerReport()
	{
		return timerReport;
	}

	/**
	 * @return
	 */
	public Report getReport()
	{
		return timerReport.getReport();
	}
}
