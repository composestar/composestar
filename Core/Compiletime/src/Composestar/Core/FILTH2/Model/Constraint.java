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

import java.util.List;

/**
 * A constraint between two "actions"
 * 
 * @author Michiel Hendriks
 */
public abstract class Constraint
{

	/**
	 * The left hand side of the constraint
	 */
	protected Action lhs;

	/**
	 * The right hand side of the constraint
	 */
	protected Action rhs;

	public Constraint(Action left, Action right)
	{
		lhs = left;
		rhs = right;
		if (!(lhs instanceof PhantomAction))
		{
			lhs.addConstraint(this);
		}
	}

	/**
	 * @return the left side of the constraint
	 */
	public Action getLeft()
	{
		return lhs;
	}

	/**
	 * @return the right hand side of the constraint
	 */
	public Action getRight()
	{
		return rhs;
	}

	/**
	 * @return the name of this constraint
	 */
	public abstract String getName();

	/**
	 * This method will be called for all constraints. It should return true if
	 * the provided order is legal according to this constraint.
	 * 
	 * @param order
	 * @return
	 */
	public boolean isValidOrder(List<Action> order)
	{
		return true;
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
		sb.append(getName());
		sb.append("(");
		sb.append(lhs.toString());
		sb.append(", ");
		sb.append(rhs.toString());
		sb.append(")");
		return sb.toString();
	}
}
