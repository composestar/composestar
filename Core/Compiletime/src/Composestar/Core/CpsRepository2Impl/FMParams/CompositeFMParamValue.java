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

package Composestar.Core.CpsRepository2Impl.FMParams;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import Composestar.Core.CpsRepository2.FMParams.FMParameterValue;
import Composestar.Core.CpsRepository2.TypeSystem.CpsVariable;
import Composestar.Core.CpsRepository2Impl.AbstractRepositoryEntity;

/**
 * A filter module parameter value that is constructed from other filter module
 * parameter values. An instance of this is only used in case of a list of
 * values.
 * 
 * @author Michiel Hendriks
 */
public class CompositeFMParamValue extends AbstractRepositoryEntity implements FMParameterValue
{
	private static final long serialVersionUID = 2365380768980948972L;

	/**
	 * List of values
	 */
	protected List<FMParameterValue> values;

	/**
	 * 
	 */
	public CompositeFMParamValue()
	{
		values = new ArrayList<FMParameterValue>();
	}

	/**
	 * Add a new value to this list. Calls setOwner(this) on the value after
	 * adding it
	 * 
	 * @param value
	 * @throws NullPointerException Thrown when the value is null
	 */
	public void addValue(FMParameterValue value) throws NullPointerException
	{
		if (value == null)
		{
			throw new NullPointerException();
		}
		values.add(value);
		value.setOwner(this);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.FMParams.FMParameterValue#getValues()
	 */
	public Collection<CpsVariable> getValues()
	{
		Collection<CpsVariable> result = new ArrayList<CpsVariable>();
		for (FMParameterValue value : values)
		{
			result.addAll(value.getValues());
		}
		return result;
	}

}
