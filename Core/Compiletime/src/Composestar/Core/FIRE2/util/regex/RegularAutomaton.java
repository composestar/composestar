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

/**
 * The automaton of the regular pattern
 */
public class RegularAutomaton implements Serializable
{
	private static final long serialVersionUID = -5080084489074761193L;

	/**
	 * The starting state
	 */
	private RegularState startState;

	/**
	 * The final (accepting) state
	 */
	private RegularState endState;

	public RegularAutomaton()
	{}

	public RegularAutomaton(RegularState inStart)
	{
		this(inStart, new RegularState());
	}

	public RegularAutomaton(RegularState inStart, RegularState inEnd)
	{
		startState = inStart;
		endState = inEnd;
	}

	/**
	 * Set the start state
	 * 
	 * @param newStartState
	 */
	public void setStartState(RegularState newStartState)
	{
		startState = newStartState;
	}

	/**
	 * @return the starting state
	 */
	public RegularState getStartState()
	{
		return startState;
	}

	/**
	 * Set the end state
	 * 
	 * @param newEndState
	 */
	public void setEndState(RegularState newEndState)
	{
		endState = newEndState;
	}

	/**
	 * @return the end state
	 */
	public RegularState getEndState()
	{
		return endState;
	}
}
