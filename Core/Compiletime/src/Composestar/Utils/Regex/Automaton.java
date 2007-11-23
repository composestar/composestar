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

package Composestar.Utils.Regex;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

/**
 * A regular expression automaton
 * 
 * @author Michiel Hendriks
 */
public class Automaton
{
	protected State begin;

	protected State end;

	public Automaton(State beginState, State endState)
	{
		begin = beginState;
		end = endState;
	}

	/**
	 * Returns true when the buffer matches this automaton (e.g. the end state
	 * is in the resulting state collection).
	 * 
	 * @param buffer
	 * @return
	 */
	public boolean matches(MatchingBuffer buffer)
	{
		if (begin == end && buffer.current() == null)
		{
			return true;
		}
		Set<State> visited = new HashSet<State>();
		Queue<State> queue = new LinkedList<State>();
		queue.add(begin);
		while (!queue.isEmpty() && buffer.current() != null)
		{
			State current = queue.remove();
			visited.add(current);
			Set<State> result = current.nextStates(buffer);
			buffer.consume();
			if (result.contains(end) && buffer.current() == null)
			{
				return true;
			}
			for (State state : result)
			{
				if (!visited.contains(state))
				{
					queue.add(state);
				}
			}
		}
		return false;
	}
}
