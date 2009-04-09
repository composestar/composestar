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

package Composestar.Core.CpsRepository2Impl.SISpec.Constraints;

import Composestar.Core.CpsRepository2.SISpec.Constraints.Constraint;
import Composestar.Core.CpsRepository2.SISpec.Constraints.ConstraintValue;
import Composestar.Core.CpsRepository2Impl.AbstractRepositoryEntity;

/**
 * Basic generic constraint implementation
 * 
 * @author Michiel Hendriks
 */
public abstract class BinaryConstraint extends AbstractRepositoryEntity implements Constraint
{
	private static final long serialVersionUID = -7452451542459968398L;

	/**
	 * The string type;
	 */
	protected String constraintType;

	/**
	 * The left hand side value
	 */
	protected ConstraintValue lhs;

	/**
	 * The right hand side value
	 */
	protected ConstraintValue rhs;

	/**
	 * Create a filter module constraint of a given type
	 * 
	 * @param type
	 * @throws NullPointerException
	 * @throws IllegalArgumentException
	 */
	protected BinaryConstraint(String type, ConstraintValue lhsValue, ConstraintValue rhsValue)
			throws NullPointerException, IllegalArgumentException
	{
		super();
		if (type == null)
		{
			throw new NullPointerException("Type can not be null");
		}
		if (type.length() == 0)
		{
			throw new IllegalArgumentException("Type can not be empty");
		}
		constraintType = type;
		lhs = lhsValue;
		rhs = rhsValue;
	}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Core.CpsRepository2.SISpec.FilterModuleConstraint
	 * #getConstraintType()
	 */
	public String getConstraintType()
	{
		return constraintType;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.SISpec.Constraints.Constraint#getArguments
	 * ()
	 */
	public ConstraintValue[] getArguments()
	{
		return new ConstraintValue[] { lhs, rhs };
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		sb.append(constraintType);
		sb.append('(');
		int i = 0;
		for (ConstraintValue cv : getArguments())
		{
			if (i > 0)
			{
				sb.append(',');
			}
			++i;
			sb.append(cv.getStringValue());
		}
		sb.append(')');
		return sb.toString();
	}
}
