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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Repository that keeps record of all timers.
 * 
 * @author Michiel Hendriks
 */
public class CPSTimerRepository
{
	protected Map<ThreadGroup, Map<String, CPSTimer>> timers;

	public CPSTimerRepository()
	{
		timers = new HashMap<ThreadGroup, Map<String, CPSTimer>>();
	}

	public CPSTimer getTimer(String name)
	{
		Map<String, CPSTimer> list = getThreadGroupTimers();
		CPSTimer timer = list.get(name);
		if (timer == null)
		{
			synchronized (list)
			{
				timer = new CPSTimer(name);
				list.put(name, timer);
			}
		}
		return timer;
	}

	public Map<String, CPSTimer> getTimers()
	{
		return Collections.unmodifiableMap(getThreadGroupTimers());
	}

	public Map<String, CPSTimer> getTimers(ThreadGroup tg)
	{
		Map<String, CPSTimer> list = timers.get(tg);
		if (list != null)
		{
			return Collections.unmodifiableMap(list);
		}
		return Collections.emptyMap();
	}

	public synchronized void clearTimers()
	{
		timers.remove(Thread.currentThread().getThreadGroup());
	}

	public synchronized void clearTimers(ThreadGroup tg)
	{
		timers.remove(tg);
	}

	public synchronized void clearAllTimers()
	{
		timers.clear();
	}

	protected Map<String, CPSTimer> getThreadGroupTimers()
	{
		Map<String, CPSTimer> list = timers.get(Thread.currentThread().getThreadGroup());
		if (list == null)
		{
			synchronized (timers)
			{
				list = new HashMap<String, CPSTimer>();
				timers.put(Thread.currentThread().getThreadGroup(), list);
			}
		}
		return list;
	}
}
