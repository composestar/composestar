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

import java.util.Set;

/**
 * A state with a submachine that is used for a look ahead. If the submachine
 * matches this state will match.
 * 
 * @author Michiel Hendrik
 */
public class LookaheadState extends State
{
	/**
	 * The automaton that contains the subexpression
	 */
	protected Automaton automaton;

	/**
	 * The transition to use when the automaton matches.
	 */
	protected Transition automatonTransition;

	/**
	 * Negate the results of the automaton
	 */
	protected boolean negation;

	/**
	 * 
	 */
	public LookaheadState()
	{
		super();
	}

	/**
	 * @param stateLabel
	 */
	public LookaheadState(String stateLabel)
	{
		super(stateLabel);
	}

	public String getName()
	{
		if (negation)
		{
			return "(?!)" + label;
		}
		return "(?=)" + label;
	}

	/**
	 * @param newAutomaton
	 * @param endState the state to traverse to when the automaton matches
	 */
	public void setAutomaton(Automaton newAutomaton, State endState)
	{
		if (automatonTransition != null)
		{
			transitions.remove(automatonTransition);
		}
		automaton = newAutomaton;
		automatonTransition = new Transition(this, endState);
		automatonTransition.addLabel("<ÿSubExpressionÿ>"); // never matches
	}

	public void setNegation(boolean value)
	{
		negation = value;
	}

	public boolean isNegation()
	{
		return negation;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Utils.Regex.State#nextStates(Composestar.Utils.Regex.MatchingBuffer)
	 */
	@Override
	public Set<State> nextStates(MatchingBuffer buffer, boolean includeLambda)
	{
		Set<State> results = super.nextStates(buffer, includeLambda);
		boolean result = automaton.matches(buffer.fork(), true);
		if (result != negation)
		{
			results.add(automatonTransition.getEndState());
		}
		return results;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Utils.Regex.State#consuming()
	 */
	@Override
	public boolean consuming()
	{
		return false;
	}
}
