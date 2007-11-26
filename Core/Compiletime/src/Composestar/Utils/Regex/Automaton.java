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

import java.io.Serializable;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

/**
 * A regular expression automaton
 * 
 * @author Michiel Hendriks
 */
public class Automaton implements Serializable
{
	private static final long serialVersionUID = 4633405911585613127L;

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
		return matches(buffer, false);
	}

	/**
	 * Returns true when the buffer matches this automaton (e.g. the end state
	 * is in the resulting state collection).
	 * 
	 * @param buffer
	 * @return
	 */
	public boolean matches(MatchingBuffer buffer, boolean matchSubString)
	{
		if (begin == end && buffer.current() == null)
		{
			return true;
		}
		Set<State> visited = new HashSet<State>(); // never used?
		Queue<State> queue = new LinkedList<State>();

		addStates(queue, visited, begin);

		while (!buffer.atEnd() || !queue.isEmpty())
		{
			if (buffer.atEnd() && queue.contains(end))
			{
				// might be added through a lambda transition
				return true;
			}
			Queue<State> currentQueue = new LinkedList<State>(queue);
			queue.clear();
			// visited = new HashSet<State>();
			while (!currentQueue.isEmpty())
			{
				State current = currentQueue.remove();
				// visited.add(current);
				Set<State> result = current.nextStates(buffer, false);
				if (!result.isEmpty())
				{
					if (result.contains(end))
					{
						if (matchSubString)
						{
							return true;
						}
						// matching till the end
						if (current.consuming() && buffer.next() == null)
						{
							// current should be the last token
							return true;
						}
						else if (!current.consuming() && buffer.atEnd())
						{
							// non comsuning (lookaround), should match the end
							// of the buffer
							return true;
						}
					}
				}

				// FIXME: this fails when Lookahead state contains
				// consuming edges (e.g. non lambda edges)
				if (current.consuming())
				{
					for (State state : result)
					{
						addStates(queue, visited, state);
					}
				}
				else
				{
					// non consuming adds to the current queue
					for (State state : result)
					{
						addStates(currentQueue, visited, state);
					}
				}
			}
			// don't consume until all states had a chance at the current buffer
			buffer.consume();
		}
		return false;
	}

	/**
	 * Add states to the queue, including lambda transitions
	 * 
	 * @param toqueue
	 * @param visited
	 * @param state
	 */
	protected void addStates(Queue<State> toqueue, Set<State> visited, State state)
	{
		if (visited.contains(state))
		{
			return;
		}
		if (toqueue.contains(state))
		{
			return;
		}
		toqueue.add(state);
		for (Transition t : state.getTransitions())
		{
			if (t.isLambda())
			{
				if (!visited.contains(t.getEndState()))
				{
					addStates(toqueue, visited, t.getEndState());
				}
			}
		}
	}
}
