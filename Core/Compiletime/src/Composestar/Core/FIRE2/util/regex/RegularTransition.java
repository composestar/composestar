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

import java.util.HashSet;

class RegularTransition
{
	private RegularState startState;

	private RegularState endState;

	private HashSet<String> labels;

	private boolean negation;

	public RegularTransition(RegularState startState, RegularState endState)
	{
		this.startState = startState;
		this.endState = endState;

		startState.addOutTransition(this);

		labels = new HashSet<String>();
	}

	public void setNegation(boolean negation)
	{
		this.negation = negation;
	}

	public boolean isNegation()
	{
		return negation;
	}

	public void addLabel(String label)
	{
		labels.add(label);
	}

	public boolean match(String word)
	{
		if (labels.contains("_"))
		{
			return !negation;
		}
		else if (negation)
		{
			return !labels.contains(word);
		}
		else
		{
			return labels.contains(word);
		}
	}

	public boolean isEmpty()
	{
		return labels.isEmpty();
	}

	public RegularState getEndState()
	{
		return endState;
	}

	public RegularState getStartState()
	{
		return startState;
	}
}
