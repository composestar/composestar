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
 * If X is included then Y must NOT be included in the order. But, also, if Y is
 * present X must not be present either.
 * 
 * @author Michiel Hendriks
 */
public class ExcludeConstraint extends StructuralConstraint
{
	public static final String NAME = "exclude";

	public ExcludeConstraint(Action left, Action right)
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
		// phantom actions are always ok
		if (lhs instanceof PhantomAction)
		{
			return true;
		}
		if (rhs instanceof PhantomAction)
		{
			return true;
		}
		// if either one is not present it is ok
		if (order.indexOf(lhs) == -1 || order.indexOf(rhs) == -1)
		{
			return true;
		}
		// both are present
		return false;
	}
}
