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

package Composestar.Utils.Perf;

/**
 * @author Michiel Hendriks
 */
public class CPSTimerEvent
{
	protected String message;

	protected long startTime;

	protected long stopTime;

	protected long memoryDelta;

	public CPSTimerEvent(String eventMessage, long eventStart, long eventStop, long eventMemoryDelta)
	{
		message = eventMessage;
		startTime = eventStart;
		stopTime = eventStop;
		memoryDelta = eventMemoryDelta;
		// System.out.println(toString());
	}

	public String getMessage()
	{
		return message;
	}

	/**
	 * Start time (in nanoseconds)
	 * 
	 * @return
	 */
	public long getStart()
	{
		return startTime;
	}

	/**
	 * Stop time (in nanoseconds)
	 * 
	 * @return
	 */
	public long getStop()
	{
		return stopTime;
	}

	/**
	 * Duration (in nanoseconds)
	 * 
	 * @return
	 */
	public long getDuration()
	{
		if (stopTime > 0)
		{
			return stopTime - startTime;
		}
		return -1L;
	}

	/**
	 * Get the memory usage delta (estimated) between start and stop in bytes;
	 * 
	 * @return
	 */
	public long getMemoryDelta()
	{
		return memoryDelta;
	}

	@Override
	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		sb.append(message);
		if (stopTime > 0)
		{
			sb.append(" [Duration: ");
			sb.append(getDuration());
			sb.append(" nanoseconds]");
		}
		return sb.toString();
	}
}
