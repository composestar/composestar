/*
 * This file is part of the Compose* project.
 * http://composestar.sourceforge.net
 * Copyright (C) 2009 University of Twente.
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

package Composestar.Core.CpsRepository2Impl.SISpec.Constraints;

import java.util.List;

import Composestar.Core.CpsRepository2.SISpec.Constraints.ConstraintValue;
import Composestar.Core.CpsRepository2.SISpec.Constraints.ExecutionManager;
import Composestar.Core.CpsRepository2.SISpec.Constraints.ExecutionResult;
import Composestar.Core.CpsRepository2.SISpec.Constraints.FilterModuleConstraintValue;

/**
 * @author Michiel Hendriks
 */
public class OrderingConstraint extends BinaryConstraint
{
	private static final long serialVersionUID = 6190724523817484420L;

	public static final String NAME = "pre";

	/**
	 * @param type
	 * @throws NullPointerException
	 * @throws IllegalArgumentException
	 */
	public OrderingConstraint(FilterModuleConstraintValue lhs, FilterModuleConstraintValue rhs)
			throws NullPointerException, IllegalArgumentException
	{
		super(NAME, lhs, rhs);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.FILTH2.Model.Constraint#isValidOrder(java.util.List)
	 */
	public boolean evalConstraint(List<ConstraintValue> order, ExecutionManager exec)
	{
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
