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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * A timer class used to record duration of certain events. CPSTimers are stored
 * together using the current thread group. Use unique names for CPSTimers using
 * in parallel running threads.
 * 
 * @author Michiel Hendriks
 */
public class CPSTimer
{
	private static final int MAX_NESTING = 5;

	protected static CPSTimerRepository repository = new CPSTimerRepository();

	protected long creationTime;

	protected String name;

	protected String[] currentMessage = new String[MAX_NESTING];

	protected long[] currentStart = new long[MAX_NESTING];

	protected int idx;

	protected List<CPSTimerEvent> events;

	/**
	 * Get a timer with the given name. The name works just like the naming
	 * system for CPSLoggers.
	 * 
	 * @param name
	 * @return
	 */
	public static final CPSTimer getTimer(String name)
	{
		return repository.getTimer(name);
	}

	/**
	 * Create a new timer and directly start the timing processing with the
	 * given message
	 * 
	 * @see #getTimer(String)
	 * @param name
	 * @param msg
	 * @return
	 */
	public static final CPSTimer getTimer(String name, String msg)
	{
		CPSTimer timer = getTimer(name);
		timer.start(msg);
		return timer;
	}

	/**
	 * @see #getTimer(String, String)
	 * @see String#format(String, Object...)
	 * @param name
	 * @param format
	 * @param args
	 * @return
	 */
	public static final CPSTimer getTimer(String name, String format, Object... args)
	{
		CPSTimer timer = getTimer(name);
		timer.start(format, args);
		return timer;
	}

	/**
	 * Return all timers for this threadgroup.
	 * 
	 * @return
	 */
	public static Map<String, CPSTimer> getTimers()
	{
		return repository.getTimers();
	}

	/**
	 * Return the timers for the given thread group
	 * 
	 * @param tg
	 * @return
	 */
	public static Map<String, CPSTimer> getTimers(ThreadGroup tg)
	{
		return repository.getTimers(tg);
	}

	CPSTimer(String timerName)
	{
		creationTime = System.nanoTime();
		name = timerName;
		events = new ArrayList<CPSTimerEvent>();
	}

	/**
	 * Get the name of the timer.
	 * 
	 * @return
	 */
	public String getName()
	{
		return name;
	}

	public long getCreationTime()
	{
		return creationTime;
	}

	/**
	 * Start timing to time a process with the given message. Every start() much
	 * end with a stop() call. You can nest start calls up to a certain level
	 * (current nesting level is 5). For example, the following will create two
	 * logging events:
	 * 
	 * <pre>
	 * timer.start(&quot;foo 1&quot;);
	 * timer.start(&quot;foo 2&quot;);
	 * timer.stop();
	 * timer.stop();
	 * </pre>
	 * 
	 * @param msg
	 */
	public synchronized void start(String msg)
	{
		if (idx > MAX_NESTING)
		{
			++idx; // because stop() will decrease it
			return;
		}
		currentMessage[idx] = msg;
		currentStart[idx] = System.nanoTime();
		++idx;
	}

	/**
	 * Start timing with a formatted message
	 * 
	 * @see CPSTimer#start(String)
	 * @see String#format(String, Object...)
	 * @param format
	 * @param args
	 */
	public void start(String format, Object... args)
	{
		start(String.format(format, args));
	}

	/**
	 * Stops the currently active timer.
	 */
	public synchronized void stop()
	{
		long stopTime = System.nanoTime();
		--idx;
		if (idx < 0)
		{
			StringBuffer stackTrace = new StringBuffer();
			try
			{
				StackTraceElement[] trace = Thread.currentThread().getStackTrace();
				for (int i = 2; i < 7 && i < trace.length; i++)
				{
					// first item is the call to getStackTrace
					// second item is this method
					if (i > 2)
					{
						stackTrace.append("; ");
					}
					stackTrace.append(trace[i].toString());
				}
				if (trace.length > 7)
				{
					stackTrace.append(" [...] ");
				}
			}
			catch (SecurityException se)
			{
				stackTrace.append("unable to retrieve the stack trace.");
			}

			// shouldn't happen
			addEvent(new CPSTimerEvent("Timer results are corrupt, stop() calls outnumber the start() calls. Trace: "
					+ stackTrace.toString(), 0, 0));
			idx = 0;
			return;
		}
		addEvent(new CPSTimerEvent(currentMessage[idx], currentStart[idx], stopTime));
		currentMessage[idx] = "";
		currentStart[idx] = 0;
	}

	protected synchronized void addEvent(CPSTimerEvent event)
	{
		events.add(event);
	}

	/**
	 * Get the list of timer events for this timer.
	 * 
	 * @return
	 */
	public List<CPSTimerEvent> getEvents()
	{
		return Collections.unmodifiableList(events);
	}

	/**
	 * Return the last timer event, or null if no event has been finished.
	 * 
	 * @return
	 */
	public CPSTimerEvent getLastEvent()
	{
		if (events.size() > 0)
		{
			return events.get(events.size() - 1);
		}
		return null;
	}
}
