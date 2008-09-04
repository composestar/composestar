/*
 * This file is part of the Compose* project.
 * http://composestar.sourceforge.net
 * Copyright (C) 2008 University of Twente.
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

package Composestar.Core.CpsRepository2Impl.FilterElements;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import Composestar.Core.CpsRepository2.FilterElements.CanonAssignment;
import Composestar.Core.CpsRepository2.FilterElements.FilterElement;
import Composestar.Core.CpsRepository2.FilterElements.MatchingExpression;
import Composestar.Core.CpsRepository2.Instantiatable.Instantiator;
import Composestar.Core.CpsRepository2Impl.AbstractRepositoryEntity;

/**
 * The basic implementation of a filter element
 * 
 * @author Michiel Hendriks
 */
public class FilterElementImpl extends AbstractRepositoryEntity implements FilterElement
{
	private static final long serialVersionUID = 2310623411240919661L;

	/**
	 * The message matching expression
	 */
	protected MatchingExpression matchingExpression;

	/**
	 * The assignment part
	 */
	protected Map<String, CanonAssignment> assignments;

	/**
	 * Create a new filter element
	 */
	public FilterElementImpl()
	{
		super();
		assignments = new HashMap<String, CanonAssignment>();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.FilterElements.FilterElement#addAssignment
	 * (Composestar.Core.CpsRepository2.FilterElements.CanonAssignment)
	 */
	public CanonAssignment addAssignment(CanonAssignment assignment) throws NullPointerException
	{
		if (assignment == null)
		{
			throw new NullPointerException();
		}
		if (assignment.getVariable() == null)
		{
			throw new NullPointerException("Assignment has no variable");
		}
		CanonAssignment old = assignments.put(assignment.getVariable().getName(), assignment);
		assignment.setOwner(this);
		return old;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.FilterElements.FilterElement#getAssignment
	 * (java.lang.String)
	 */
	public CanonAssignment getAssignment(String assignmentName)
	{
		return assignments.get(assignmentName);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.FilterElements.FilterElement#getAssignments
	 * ()
	 */
	public Collection<CanonAssignment> getAssignments()
	{
		return Collections.unmodifiableCollection(assignments.values());
	}

	/*
	 * (non-Javadoc)
	 * @seeComposestar.Core.CpsRepository2.FilterElements.FilterElement#
	 * getMatchingExpression()
	 */
	public MatchingExpression getMatchingExpression()
	{
		return matchingExpression;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.FilterElements.FilterElement#removeAssignment
	 * (Composestar.Core.CpsRepository2.FilterElements.CanonAssignment)
	 */
	public CanonAssignment removeAssignment(CanonAssignment assignment) throws NullPointerException
	{
		if (assignment == null)
		{
			throw new NullPointerException();
		}
		if (assignments.values().remove(assignment))
		{
			return assignment;
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @seeComposestar.Core.CpsRepository2.FilterElements.FilterElement#
	 * setMatchingExpression
	 * (Composestar.Core.CpsRepository2.FilterElements.MatchingExpression)
	 */
	public void setMatchingExpression(MatchingExpression expr) throws NullPointerException
	{
		if (expr == null)
		{
			throw new NullPointerException();
		}
		matchingExpression = expr;
		matchingExpression.setOwner(this);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.Instantiatable.Instantiatable#newInstance
	 * (Composestar.Core.CpsRepository2.Instantiatable.Instantiator)
	 */
	public FilterElement newInstance(Instantiator instantiator) throws UnsupportedOperationException
	{
		return instantiator.instantiate(this);
	}

}
