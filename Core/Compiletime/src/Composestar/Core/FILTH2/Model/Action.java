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

package Composestar.Core.FILTH2.Model;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Michiel Hendriks
 */
public class Action
{
	/**
	 * The name of this action
	 */
	protected String name;

	/**
	 * Constraints associated with this action (the left hand side of it)
	 */
	protected Set<Constraint> constraints;

	public Action(String actionName)
	{
		name = actionName;
		constraints = new HashSet<Constraint>();
	}

	/**
	 * @return the name of this action
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Add a new constraint
	 * 
	 * @param constraint
	 */
	public void addConstraint(Constraint constraint)
	{
		constraints.add(constraint);
	}

	/**
	 * @return all constraints
	 */
	public Set<Constraint> getConstraints()
	{
		return Collections.unmodifiableSet(constraints);
	}

	/**
	 * Return only the constraints of the given type
	 * 
	 * @param <T>
	 * @param type
	 * @return
	 */
	public <T extends Constraint> Set<T> getConstraints(Class<T> type)
	{
		Set<T> result = new HashSet<T>();
		for (Constraint c : constraints)
		{
			if (type.isInstance(c))
			{
				result.add(type.cast(c));
			}
		}
		return result;
	}

	@Override
	public String toString()
	{
		return getName();
	}
}
