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
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class RegularTransition implements Serializable
{
	private static final long serialVersionUID = -8405459283288345064L;

	/**
	 * Wildcard, matches always
	 */
	public static final String WILDCARD = "<ÿÿÿ>";

	private RegularState startState;

	private RegularState endState;

	private Set<String> labels;

	private boolean negation;

	public RegularTransition(RegularState mystart, RegularState myend)
	{
		setStartState(mystart);
		setEndState(myend);
		labels = new HashSet<String>();
	}

	public void setStartState(RegularState newStart)
	{
		if (startState != null)
		{
			startState.removeOutTransition(this);
		}
		startState = newStart;
		startState.addOutTransition(this);
	}

	public void setEndState(RegularState newEnd)
	{
		endState = newEnd;
	}

	public void setNegation(boolean value)
	{
		negation = value;
	}

	public boolean isNegation()
	{
		return negation;
	}

	public void addLabel(String label)
	{
		labels.add(label);
	}

	public void addLabels(Set<String> label)
	{
		labels.addAll(label);
	}

	public Set<String> getLabels()
	{
		return Collections.unmodifiableSet(labels);
	}

	public boolean match(String word)
	{
		if (labels.contains(WILDCARD))
		{
			// "not anything" is an interesting concept, absolutely useless, but
			// interesting nonetheless.
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

	public boolean isWildcard()
	{
		return labels.contains(WILDCARD);
	}

	public RegularState getEndState()
	{
		return endState;
	}

	public RegularState getStartState()
	{
		return startState;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		if (negation)
		{
			sb.append("!");
		}
		sb.append(labels.toString());
		return sb.toString();
	}
}
