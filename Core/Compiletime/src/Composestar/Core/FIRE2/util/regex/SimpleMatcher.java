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

package Composestar.Core.FIRE2.util.regex;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

/**
 * A simple pattern matcher for the FIRE2 RegularAutomatons.
 * 
 * @author Michiel Hendriks
 */
public class SimpleMatcher
{
	protected Pattern pattern;

	protected Queue<String> words;

	protected Set<RegularState> states;

	public static final boolean matches(Pattern pattern, Queue<String> words)
	{
		SimpleMatcher sm = new SimpleMatcher(pattern, words);
		return sm.matches();
	}

	public static final boolean matches(Pattern pattern, List<String> words)
	{
		Queue<String> queue = new LinkedList<String>();
		queue.addAll(words);
		return matches(pattern, queue);
	}

	public static final boolean matches(Pattern pattern, String[] words)
	{
		Queue<String> queue = new LinkedList<String>();
		queue.addAll(Arrays.asList(words));
		return matches(pattern, queue);
	}

	public static final boolean matches(Pattern pattern, String sentence)
	{
		return matches(pattern, sentence.split("\\s"));
	}

	protected SimpleMatcher(Pattern inPattern, Queue<String> inWords)
	{
		pattern = inPattern;
		words = inWords;
		states = new HashSet<RegularState>();
		addState(pattern.getStartState());
	}

	protected void addState(RegularState state)
	{
		if (states.contains(state))
		{
			return;
		}
		states.add(state);

		// add lambda transitions
		for (RegularTransition rt : state.getOutTransitions())
		{
			if (rt.isEmpty())
			{
				addState(rt.getEndState());
			}
		}
	}

	protected boolean matches()
	{
		while (words.size() > 0)
		{
			String word = words.remove();
			if ("".equals(word))
			{
				continue;
			}
			if (traverseStates(word) == 0)
			{
				break;
			}
		}
		return (words.size() == 0) && (states.contains(pattern.getEndState()));
	}

	protected int traverseStates(String word)
	{
		Queue<RegularState> queue = new LinkedList<RegularState>(states);
		Set<RegularState> visited = new HashSet<RegularState>();
		states.clear();
		while (queue.size() > 0)
		{
			RegularState state = queue.remove();
			visited.add(state);
			for (RegularTransition rt : state.getOutTransitions())
			{
				if (rt.isEmpty())
				{
					RegularState rst = rt.getEndState();
					if (!queue.contains(rst) && !visited.contains(rst))
					{
						queue.add(rst);
					}
				}
				else if (rt.match(word))
				{
					addState(rt.getEndState());
				}
			}
		}
		return states.size();
	}
}
