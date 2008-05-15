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
 * If X is included then Y must also be included in the order
 * 
 * @author Michiel Hendriks
 */
public class IncludeConstraint extends StructuralConstraint
{
	public static final String NAME = "include";

	public IncludeConstraint(Action left, Action right)
	{
		super(left, right);
		if (!(rhs instanceof PhantomAction))
		{
			// because include requires both actions to be present
			rhs.addConstraint(this);
		}
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
	public boolean isValidOrder(List<Action> order, ExecutionManager exec)
	{
		if (lhs instanceof PhantomAction)
		{
			return false;
		}
		if (rhs instanceof PhantomAction)
		{
			return false;
		}
		if (order.indexOf(lhs) == -1 || order.indexOf(rhs) == -1)
		{
			return false;
		}
		// both normal actions are present
		return true;
	}
}
