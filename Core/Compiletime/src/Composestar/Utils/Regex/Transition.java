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
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * A normal state transition
 * 
 * @author Michiel Hendriks
 */
public class Transition implements Serializable
{
	private static final long serialVersionUID = -8370677697516793898L;

	/**
	 * The wildcard label, always matches
	 */
	public static final String WILDCARD = "<ÿÿÿ>";

	protected Set<String> labels;

	protected State begin;

	protected State end;

	public Transition()
	{
		labels = new HashSet<String>();
	}

	public Transition(State beginState, State endState)
	{
		this();
		setBeginState(beginState);
		setEndState(endState);
	}

	/**
	 * Add a single label to this transition
	 * 
	 * @param label
	 */
	public void addLabel(String label)
	{
		if (label == null)
		{
			throw new NullPointerException("label can not be null");
		}
		if (label.trim().length() == 0)
		{
			throw new IllegalArgumentException("label can not be empty");
		}
		labels.add(label.trim());
	}

	/**
	 * Add a set of labels to this transition
	 * 
	 * @param lbls
	 */
	public void addLabels(Collection<String> lbls)
	{
		if (lbls == null)
		{
			throw new NullPointerException("labels can not be null");
		}
		for (String s : lbls)
		{
			if (s.trim().length() > 0)
			{
				labels.add(s.trim());
			}
		}
	}

	/**
	 * Get a read only list of all labels in this transition
	 * 
	 * @return
	 */
	public Set<String> getLabels()
	{
		return Collections.unmodifiableSet(labels);
	}

	/**
	 * Return true if this is a lambda transition.
	 * 
	 * @return
	 */
	public boolean isLambda()
	{
		return labels.isEmpty();
	}

	/**
	 * Set the begin state
	 * 
	 * @param newBegin
	 */
	public void setBeginState(State newBegin)
	{
		if (begin == newBegin)
		{
			return;
		}
		if (begin != null)
		{
			begin.removeTransition(this);
		}
		begin = newBegin;
		begin.addTransition(this);
	}

	/**
	 * Get the begin state
	 * 
	 * @return
	 */
	public State getBeginState()
	{
		return begin;
	}

	/**
	 * Set the begin state
	 * 
	 * @param newBegin
	 */
	public void setEndState(State newEnd)
	{
		end = newEnd;
	}

	/**
	 * Get the end state
	 * 
	 * @return
	 */
	public State getEndState()
	{
		return end;
	}

	/**
	 * Returns true when the transition has this label
	 * 
	 * @param label
	 * @return
	 */
	public boolean containsLabel(String label)
	{
		if (label == null)
		{
			return false;
		}
		if (labels.contains(WILDCARD))
		{
			return true;
		}
		return labels.contains(label);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return labels.toString();
	}
}
