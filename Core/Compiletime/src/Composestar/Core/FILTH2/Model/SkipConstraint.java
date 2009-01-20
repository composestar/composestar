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
 * skip(X,Y,Z) ... if X is present Y is excluded (exec result is set to Z)
 * 
 * @author Michiel Hendriks
 */
public class SkipConstraint extends ControlConstraint
{
	public static final String NAME = "skip";

	protected Action resultAction;

	public SkipConstraint(Action left, Action right, Action result)
	{
		super(left, right);
		resultAction = result;
	}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Core.FILTH2.Model.Constraint#getName()
	 */
	@Override
	public String getName()
	{
		return NAME;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.FILTH2.Model.Constraint#isValidOrder(java.util.List,
	 * Composestar.Core.FILTH2.Model.ExecutionManager)
	 */
	@Override
	public boolean isValidOrder(List<Action> order, ExecutionManager exec)
	{
		if (exec != null)
		{
			if (exec.getResult(lhs) == ExecutionResult.TRUE)
			{
				exec.setExecutable(rhs, false);
				ExecutionResult execres = exec.getResult(resultAction);
				if (execres == ExecutionResult.NOT_EXECUTED)
				{
					// this should actually never happen
					return false;
				}
				exec.setResult(rhs, execres);
			}
		}
		// condition constraints never invalidate an order
		return true;
	}

	/*
	 * (non-Javadoc)
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
		sb.append(", ");
		sb.append(resultAction.toString());
		sb.append(")");
		return sb.toString();
	}
}
