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
import Composestar.Core.CpsRepository2.SISpec.Constraints.FilterModuleConstraintValue;

/**
 * @author Michiel Hendriks
 */
public class ExcludeConstraint extends StructuralConstraint
{
	private static final long serialVersionUID = 8984180159388624601L;

	public static final String NAME = "exclude";

	/**
	 * @param lhsValue
	 * @param rhsValue
	 * @throws NullPointerException
	 * @throws IllegalArgumentException
	 */
	public ExcludeConstraint(FilterModuleConstraintValue lhsValue, FilterModuleConstraintValue rhsValue)
			throws NullPointerException, IllegalArgumentException
	{
		super(NAME, lhsValue, rhsValue);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.FILTH2.Model.Constraint#isValidOrder(java.util.List)
	 */
	public boolean evalConstraint(List<ConstraintValue> order, ExecutionManager exec)
	{
		// if either one is not present it is ok
		if (order.indexOf(lhs) == -1 || order.indexOf(rhs) == -1)
		{
			return true;
		}
		// both are present
		return false;
	}
}
