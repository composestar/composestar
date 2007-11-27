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

public class RegularState implements Serializable
{
	private static final long serialVersionUID = 2662274504980135336L;

	private static int currentId;

	/**
	 * Used in toString() for state names
	 */
	private String stateId;

	private List<RegularTransition> outTransitions;

	public RegularState()
	{
		stateId = getNewStateId();
		outTransitions = new ArrayList<RegularTransition>();
	}

	private static String getNewStateId()
	{
		return "s" + (currentId++);
	}

	public String getStateId()
	{
		return stateId;
	}

	public void addOutTransition(RegularTransition transition)
	{
		outTransitions.add(transition);
	}

	public void removeOutTransition(RegularTransition transition)
	{
		outTransitions.remove(transition);
	}

	public List<RegularTransition> getOutTransitions()
	{
		return Collections.unmodifiableList(outTransitions);
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
