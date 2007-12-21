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

package Composestar.Utils.Perf.Report;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Map.Entry;

import Composestar.Utils.Perf.CPSTimer;

/**
 * A sorted tree of all timers
 * 
 * @author Michiel Hendriks
 */
public class CPSTimerTree
{
	protected static final CPSTimerTreeComparator comp = new CPSTimerTreeComparator();

	protected CPSTimerTree parent;

	protected SortedSet<CPSTimerTree> children;

	protected String name;

	protected CPSTimer myTimer;

	protected CPSTimerTree(String myname)
	{
		name = myname;
		children = new TreeSet<CPSTimerTree>(comp);
	}

	protected CPSTimerTree(String myname, CPSTimer forTimer)
	{
		this(myname);
		myTimer = forTimer;
	}

	public static CPSTimerTree constructTree(Map<String, CPSTimer> timers)
	{
		//
		// Node
		// Node.Child
		// Node.Child.Child
		// Node2.Child2.Child1
		// Node2.Child2.Child2
		//		 
		Queue<CPSTimerTree> proc = new LinkedList<CPSTimerTree>();
		Map<String, CPSTimerTree> lookup = new HashMap<String, CPSTimerTree>();
		for (Entry<String, CPSTimer> entry : timers.entrySet())
		{
			CPSTimerTree item = new CPSTimerTree(entry.getKey(), entry.getValue());
			proc.add(item);
			lookup.put(entry.getKey(), item);
		}
		while (proc.size() > 0)
		{
			CPSTimerTree item = proc.remove();
			String parName = item.getName();
			int idx = parName.lastIndexOf('.');
			if (idx < 0)
			{
				parName = "";
			}
			else
			{
				parName = parName.substring(0, idx);
			}
			CPSTimerTree par = lookup.get(parName);
			if (par == null)
			{
				par = new CPSTimerTree(parName);
				lookup.put(parName, par);
				if (parName.length() > 0)
				{
					proc.add(par);
				}
			}
			par.addChild(item);
		}
		return lookup.get(""); // the root item
	}

	protected long getCreationTime()
	{
		if (myTimer != null)
		{
			return myTimer.getCreationTime();
		}
		if (children.size() > 0)
		{
			return children.first().getCreationTime();
		}
		return Long.MIN_VALUE;
	}

	/**
	 * Get the full name of this tree item
	 * 
	 * @return
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * The CPSTimer associated with this tree item. Can be null when this is
	 * just an grouping parent for child timers (like the root tree item).
	 * 
	 * @return
	 */
	public CPSTimer getTimer()
	{
		return myTimer;
	}

	/**
	 * Get the children of this tree item.
	 * 
	 * @return
	 */
	public SortedSet<CPSTimerTree> getChildren()
	{
		return Collections.unmodifiableSortedSet(children);
	}

	protected void addChild(CPSTimerTree item)
	{
		children.add(item);
		item.setParent(this);
	}

	protected void setParent(CPSTimerTree value)
	{
		parent = value;
	}

	private static class CPSTimerTreeComparator implements Comparator<CPSTimerTree>
	{
		public int compare(CPSTimerTree o1, CPSTimerTree o2)
		{
			return (int) (o1.getCreationTime() - o2.getCreationTime());
		}
	}
}
