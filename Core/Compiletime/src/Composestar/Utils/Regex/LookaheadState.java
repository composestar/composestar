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

import java.util.Collections;
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
	 */
	public void setAutomaton(Automaton newAutomaton)
	{
		automaton = newAutomaton;
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
	public Set<State> nextStates(MatchingBuffer buffer)
	{
		boolean result = automaton.matches(buffer.fork());
		if (result != negation)
		{
			return super.nextStates(buffer);
		}
		return Collections.emptySet();
	}

}
