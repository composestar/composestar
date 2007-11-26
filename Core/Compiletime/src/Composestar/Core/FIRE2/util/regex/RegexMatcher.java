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

import java.util.Iterator;

import Composestar.Core.FIRE2.model.ExecutionModel;
import Composestar.Core.FIRE2.model.ExecutionState;
import Composestar.Utils.Regex.MatchingBuffer;

/**
 * @author Michiel Hendriks
 */
public class RegexMatcher implements MatchingBuffer
{
	protected Composestar.Utils.Regex.RegexPattern pattern;

	protected ExecutionModel model;

	protected Labeler labeler;

	public RegexMatcher(Composestar.Utils.Regex.RegexPattern pattern, ExecutionModel model, Labeler labeler)
	{
		this.pattern = pattern;
		this.model = model;
		this.labeler = labeler;
	}

	public boolean matches()
	{
		Iterator<ExecutionState> states = model.getEntranceStates();
		while (states.hasNext())
		{
			if (matches(states.next()))
			{
				return true;
			}
		}
		return false;
	}

	protected boolean matches(ExecutionState startState)
	{
		return pattern.matches(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Utils.Regex.MatchingBuffer#atEnd()
	 */
	public boolean atEnd()
	{
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Utils.Regex.MatchingBuffer#consume()
	 */
	public void consume()
	{
	// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Utils.Regex.MatchingBuffer#current()
	 */
	public String current()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Utils.Regex.MatchingBuffer#fork()
	 */
	public MatchingBuffer fork()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Utils.Regex.MatchingBuffer#next()
	 */
	public String next()
	{
		// TODO Auto-generated method stub
		return null;
	}

}
