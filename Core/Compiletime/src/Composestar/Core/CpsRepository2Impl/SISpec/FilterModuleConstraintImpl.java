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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import Composestar.Core.CpsRepository2.SISpec.ConstraintValue;
import Composestar.Core.CpsRepository2.SISpec.FilterModuleConstraint;
import Composestar.Core.CpsRepository2Impl.AbstractRepositoryEntity;

/**
 * Basic generic constraint implementation
 * 
 * @author Michiel Hendriks
 */
public class FilterModuleConstraintImpl extends AbstractRepositoryEntity implements FilterModuleConstraint
{
	private static final long serialVersionUID = -7452451542459968398L;

	/**
	 * The string type;
	 */
	protected String constraintType;

	/**
	 * Contains the argument values.
	 */
	protected List<ConstraintValue> arguments;

	/**
	 * Create a filter module constraint of a given type
	 * 
	 * @param type
	 * @throws NullPointerException
	 * @throws IllegalArgumentException
	 */
	public FilterModuleConstraintImpl(String type) throws NullPointerException, IllegalArgumentException
	{
		super();
		if (type == null)
		{
			throw new NullPointerException("Type can not be null");
		}
		if (type.isEmpty())
		{
			throw new IllegalArgumentException("Type can not be empty");
		}
		constraintType = type;
		arguments = new ArrayList<ConstraintValue>();
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
	 * @see Composestar.Core.CpsRepository2.SISpec.FilterModuleConstraint
	 * #getArguments()
	 */
	public List<ConstraintValue> getArguments()
	{
		return Collections.unmodifiableList(arguments);
	}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Core.CpsRepository2.SISpec.FilterModuleConstraint
	 * #setArguments(java.util.List)
	 */
	public void setArguments(List<ConstraintValue> args) throws NullPointerException, IllegalArgumentException
	{
		if (args == null)
		{
			throw new NullPointerException();
		}
		if (args.size() < 2 || args.size() > 3)
		{ // TODO needs to be fixed to actually check the data w.r.t. the
			// constraint type
			throw new IllegalArgumentException(String.format("%s takes 2 arguments, received %d", getConstraintType(),
					args.size()));
		}
		arguments.clear();
		arguments.addAll(args);
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
		for (ConstraintValue val : arguments)
		{
			if (i > 0)
			{
				sb.append(',');
			}
			++i;
			sb.append(val.getStringValue());
		}
		sb.append(')');
		return sb.toString();
	}
}
