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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Repository that keeps record of all timers.
 * 
 * @author Michiel Hendriks
 */
public class CPSTimerRepository
{
	protected static InheritableThreadLocal<Set<CPSTimer>> groupTimers = new InheritableThreadLocal<Set<CPSTimer>>()
	{
		@Override
		protected Set<CPSTimer> initialValue()
		{
			return new HashSet<CPSTimer>();
		}
	};

	protected ThreadLocal<Map<String, CPSTimer>> threadTimers;

	/**
	 * Initialize a new group for timers
	 */
	public static void newTimerGroup()
	{
		groupTimers.set(new HashSet<CPSTimer>());
	}

	/**
	 * @return the group timers
	 */
	public static Set<CPSTimer> getGroupTimers()
	{
		return Collections.unmodifiableSet(groupTimers.get());
	}

	CPSTimerRepository()
	{
		threadTimers = new ThreadLocal<Map<String, CPSTimer>>()
		{
			@Override
			protected Map<String, CPSTimer> initialValue()
			{
				return new HashMap<String, CPSTimer>();
			}
		};
	}

	/**
	 * Get a timer with the given name, creates an instance when needed
	 * 
	 * @param name
	 * @return
	 */
	public CPSTimer getTimer(String name)
	{
		Map<String, CPSTimer> list = threadTimers.get();
		CPSTimer timer = list.get(name);
		if (timer == null)
		{
			synchronized (list)
			{
				timer = new CPSTimer(name);
				list.put(name, timer);
			}
		}
		if (timer != null)
		{
			synchronized (groupTimers)
			{
				groupTimers.get().add(timer);
			}
		}
		return timer;
	}

	/**
	 * @return all registered timers
	 */
	public Map<String, CPSTimer> getTimers()
	{
		return Collections.unmodifiableMap(threadTimers.get());
	}

	/**
	 * Remove all registered timers
	 */
	public synchronized void clearTimers()
	{
		threadTimers.get().clear();
	}
}
