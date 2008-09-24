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
import java.util.Collections;

import Composestar.Core.CpsRepository2.FMParams.FMParameterValue;
import Composestar.Core.CpsRepository2.TypeSystem.CpsVariable;
import Composestar.Core.CpsRepository2Impl.AbstractRepositoryEntity;

/**
 * A basic filter module parameter list.
 * 
 * @author Michiel Hendriks
 */
public class SimpleFMParamValue extends AbstractRepositoryEntity implements FMParameterValue
{
	private static final long serialVersionUID = -2009091299407476099L;

	/**
	 * Contains the values
	 */
	protected Collection<CpsVariable> values;

	/**
	 * Create a new basic collection
	 * 
	 * @param var The value of this parameter value
	 * @throws NullPointerException Thrown when the value is null
	 */
	public SimpleFMParamValue(CpsVariable var) throws NullPointerException
	{
		super();
		if (var == null)
		{
			throw new NullPointerException("Variable can not be null");
		}
		values = new ArrayList<CpsVariable>();
		values.add(var);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.FMParams.FMParameterValue#getValues()
	 */
	public Collection<CpsVariable> getValues()
	{
		return Collections.unmodifiableCollection(values);
	}

}
