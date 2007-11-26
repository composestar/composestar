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
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * A state in the regular expression automaton
 * 
 * @author Michiel Hendriks
 */
public class State implements Serializable
{
	private static final long serialVersionUID = -8637328782773046020L;

	private static int cnt;

	/**
	 * Identifier for this state
	 */
	protected String label;

	protected Set<Transition> transitions;

	public State()
	{
		this("s" + nextCounter());
	}

	public State(String stateLabel)
	{
		transitions = new HashSet<Transition>();
		label = stateLabel;
	}

	/**
	 * Counter to create unique state labels
	 * 
	 * @return
	 */
	protected static int nextCounter()
	{
		return cnt++;
	}

	public String getName()
	{
		return label;
	}

	/**
	 * Add a new transition to this state
	 * 
	 * @param transition
	 */
	public void addTransition(Transition transition)
	{
		if (transition == null)
		{
			throw new NullPointerException("Transition can not be null");
		}
		transitions.add(transition);
	}

	/**
	 * Remove a transition
	 * 
	 * @param transition
	 */
	public void removeTransition(Transition transition)
	{
		transitions.remove(transition);
	}

	/**
	 * Get a read only list of transitions
	 * 
	 * @return
	 */
	public Set<Transition> getTransitions()
	{
		return Collections.unmodifiableSet(transitions);
	}

	/**
	 * Return a list of next states according to the tokens in the buffer.
	 * 
	 * @param buffer
	 * @return
	 */
	public Set<State> nextStates(MatchingBuffer buffer, boolean includeLambda)
	{
		if (transitions.isEmpty())
		{
			return Collections.emptySet();
		}
		Set<State> result = new HashSet<State>();
		for (Transition t : getTransitions())
		{
			if (t.isLambda() && includeLambda)
			{
				result.add(t.getEndState());
				traverseLambdaStates(result, t.getEndState());
			}
			else if (t.containsLabel(buffer.current()))
			{
				result.add(t.getEndState());
				traverseLambdaStates(result, t.getEndState());
			}
		}
		return result;
	}

	/**
	 * Returns true when this state consumes the current buffer
	 * 
	 * @return
	 */
	public boolean consuming()
	{
		return true;
	}

	/**
	 * Traverse all lambda states
	 * 
	 * @param result
	 * @param forState
	 */
	protected void traverseLambdaStates(Set<State> result, State forState)
	{
		if (result.contains(forState))
		{
			return;
		}
		for (Transition t : forState.getTransitions())
		{
			if (t.isLambda())
			{
				result.add(t.getEndState());
				traverseLambdaStates(result, t.getEndState());
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		Set<State> states = new HashSet<State>();
		getAllStats(states);
		StringBuffer sb = new StringBuffer();
		for (State state : states)
		{
			if (state.transitions.size() == 0)
			{
				sb.append(state.getName());
				sb.append(" -> ");
				sb.append("<END>");
				sb.append("\n");
			}
			else
			{
				for (Transition rt : state.transitions)
				{
					sb.append(state.getName());
					sb.append(" -> ");
					sb.append(rt.toString());
					sb.append(" -> ");
					sb.append(rt.getEndState().getName());
					sb.append("\n");
				}
			}
		}
		return sb.toString();
	}

	private void getAllStats(Set<State> states)
	{
		states.add(this);
		for (Transition transition : transitions)
		{
			if (!states.contains(transition.getEndState()))
			{
				transition.getEndState().getAllStats(states);
			}
		}
	}
}
