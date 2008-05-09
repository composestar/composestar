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
 * cond(X,Y), Y is only allowed if X is present and before Y.
 * 
 * @author Michiel Hendriks
 */
public class CondConstraint extends ControlConstraint
{
	public static final String NAME = "cond";

	public CondConstraint(Action left, Action right)
	{
		super(left, right);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.FILTH2.Model.Constraint#getName()
	 */
	@Override
	public String getName()
	{
		return NAME;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.FILTH2.Model.Constraint#isValidOrder(java.util.List)
	 */
	@Override
	public boolean isValidOrder(List<Action> order)
	{
		// constraint only says something over an existing rhs
		int ridx = order.indexOf(rhs);
		if (rhs instanceof PhantomAction || ridx == -1)
		{
			return true;
		}
		// if lhs is a phantom, then rhs may not be present
		int lidx = order.indexOf(lhs);
		if (lhs instanceof PhantomAction || lidx == -1)
		{
			return ridx == 1;
		}
		// lhs must be before rhs
		return lidx < ridx;
	}
}
