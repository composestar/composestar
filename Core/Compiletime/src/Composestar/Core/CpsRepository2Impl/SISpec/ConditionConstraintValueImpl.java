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

package Composestar.Core.CpsRepository2Impl.SISpec;

import Composestar.Core.CpsRepository2.SISpec.ConditionConstraintValue;
import Composestar.Core.CpsRepository2.SISpec.SICondition;
import Composestar.Core.CpsRepository2Impl.AbstractRepositoryEntity;

/**
 * A constraint value that use a condition defined in the superimposition block.
 * 
 * @author Michiel Hendriks
 */
public class ConditionConstraintValueImpl extends AbstractRepositoryEntity implements ConditionConstraintValue
{
	private static final long serialVersionUID = -4636856061276353316L;

	/**
	 * The superimposition condition to use
	 */
	protected SICondition condition;

	/**
	 * @param cond
	 * @throws NullPointerException Thrown when the condition is null
	 */
	public ConditionConstraintValueImpl(SICondition cond) throws NullPointerException
	{
		super();
		if (cond == null)
		{
			throw new NullPointerException();
		}
		condition = cond;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.SISpec.ConditionConstraintValue
	 * #getCondition()
	 */
	public SICondition getCondition()
	{
		return condition;
	}

}
