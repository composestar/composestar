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
 * Base class for the patterns used by the FIRE regex engine
 */
public abstract class Pattern implements Serializable
{
	/**
	 * The original string used to create the pattern
	 */
	protected String patternString;

	/**
	 * @return the final state in the automaton
	 */
	public abstract RegularState getEndState();

	/**
	 * @return the starting state
	 */
	public abstract RegularState getStartState();

	public Pattern(String pattern)
	{
		patternString = pattern;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return patternString;
	}

	/**
	 * @return the pattern string
	 */
	public String getPatternString()
	{
		return patternString;
	}
}
