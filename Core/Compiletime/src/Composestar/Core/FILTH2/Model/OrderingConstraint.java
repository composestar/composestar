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
 * A ordering constraint. Currently there is only one constraint that dictates
 * the order, the pre() constraint
 * 
 * @author Michiel Hendriks
 */
public class OrderingConstraint extends Constraint
{
	public static final String NAME = "pre";

	public OrderingConstraint(Action left, Action right)
	{
		super(left, right);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.FILTH2.Constraint#getName()
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
	public boolean isValidOrder(List<Action> order, ExecutionManager exec)
	{
		if (rhs instanceof PhantomAction)
		{
			// if the right side is a phantom action this constraint is always
			// true, this implies: ridx == -1
			return true;
		}
		int lidx = order.indexOf(lhs);
		int ridx = order.indexOf(rhs);
		if (lidx == -1 || ridx == -1)
		{
			// if either action is not present this constraint, is also correct
			return true;
		}
		// at this point both actions are present in the order and their
		// position must be validated
		if (exec != null)
		{
			if (exec.getResult(lhs) == ExecutionResult.NOT_EXECUTED)
			{
				// lhs did not execute, therefore rhs may not execute either
				exec.setExecutable(rhs, false);
			}
		}
		return lidx < ridx;
	}
}
