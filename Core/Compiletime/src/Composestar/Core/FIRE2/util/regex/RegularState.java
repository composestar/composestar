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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A state in the regular automaton
 */
public class RegularState implements Serializable
{
	private static final long serialVersionUID = 2662274504980135336L;

	/**
	 * Counter used to give all states a unique ID
	 */
	private static int currentId;

	/**
	 * Used in toString() for state names
	 */
	private String stateId;

	/**
	 * Outgoing transitions
	 */
	private List<RegularTransition> outTransitions;

	/**
	 * If true this state will accept everything
	 */
	private boolean greedyEnd;

	public RegularState()
	{
		stateId = getNewStateId();
		outTransitions = new ArrayList<RegularTransition>();
	}

	/**
	 * @return a new state name
	 */
	private static String getNewStateId()
	{
		return "s" + currentId++;
	}

	/**
	 * @return the identifier of the state
	 */
	public String getStateId()
	{
		return stateId;
	}

	/**
	 * Add a new transition
	 * 
	 * @param transition
	 */
	public void addOutTransition(RegularTransition transition)
	{
		outTransitions.add(transition);
	}

	/**
	 * Remove the transition from this state
	 * 
	 * @param transition
	 */
	public void removeOutTransition(RegularTransition transition)
	{
		outTransitions.remove(transition);
	}

	/**
	 * @return all outgoing transition
	 */
	public List<RegularTransition> getOutTransitions()
	{
		return Collections.unmodifiableList(outTransitions);
	}

	/**
	 * If true this state is a greedy end state, meaning that it accepts all
	 * input to the end state: ".*^".
	 * 
	 * @return
	 */
	public boolean isGreedyEnd()
	{
		return greedyEnd;
	}

	/**
	 * Resolve greedy ends.
	 * 
	 * @param visited
	 */
	public void resolveGreedyEnd(Set<RegularState> visited, RegularState endState)
	{
		boolean hasSelfRef = false;
		boolean hasEndRef = false;
		visited.add(this);
		for (RegularTransition transition : outTransitions)
		{
			if (transition.isWildcard() && transition.getEndState() == this)
			{
				hasSelfRef = true;
			}
			else if (transition.isEmpty() && transition.getEndState() == endState)
			{
				// TODO: doesn't check if end state is reachable through lambda
				// transitions
				hasEndRef = true;
			}

			if (visited.contains(transition.getEndState()))
			{
				continue;
			}
			transition.getEndState().resolveGreedyEnd(visited, endState);

			if (greedyEnd)
			{
				continue;
			}
			if (transition.isEmpty())
			{
				if (transition.getEndState().isGreedyEnd())
				{
					// a lambda transition to a greedyEnd state makes this state
					// also a greedyEnd state
					greedyEnd = true;
				}
			}
		}
		if (!greedyEnd)
		{
			greedyEnd = hasSelfRef && hasEndRef;
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
		Set<RegularState> states = new HashSet<RegularState>();
		getAllStats(states);
		StringBuffer sb = new StringBuffer();
		for (RegularState state : states)
		{
			if (state.outTransitions.size() == 0)
			{
				sb.append(state.stateId);
				sb.append(" -> ");
				sb.append("<END>");
				sb.append("\n");
			}
			else
			{
				for (RegularTransition rt : state.outTransitions)
				{
					sb.append(state.stateId);
					sb.append(" -> ");
					sb.append(rt.toString());
					sb.append(" -> ");
					sb.append(rt.getEndState().stateId);
					sb.append("\n");
				}
			}
		}
		return sb.toString();
	}

	/**
	 * Find all states starting from this one
	 * 
	 * @param states
	 */
	private void getAllStats(Set<RegularState> states)
	{
		states.add(this);
		for (RegularTransition transition : outTransitions)
		{
			if (!states.contains(transition.getEndState()))
			{
				transition.getEndState().getAllStats(states);
			}
		}
	}
}
