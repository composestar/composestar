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

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;

import Composestar.Core.CpsRepository2.FMParams.FMParameterValue;
import Composestar.Core.CpsRepository2.TypeSystem.CpsVariable;
import Composestar.Core.CpsRepository2Impl.AbstractRepositoryEntity;

/**
 * A basic filter module parameter list.
 * 
 * @author Michiel Hendriks
 */
public class BasicFMParamValue extends AbstractRepositoryEntity implements FMParameterValue
{
	private static final long serialVersionUID = -2009091299407476099L;

	/**
	 * Contains the values
	 */
	protected Collection<CpsVariable> values;

	/**
	 * Create a new basic collection
	 */
	public BasicFMParamValue()
	{
		super();
		values = new LinkedHashSet<CpsVariable>();
	}

	/**
	 * Add a new variable to this filter module variable. Called setOwner(this)
	 * after the variable was added.
	 * 
	 * @param var The variable to add
	 * @throws NullPointerException Thrown when the variable is null
	 */
	public void addValue(CpsVariable var) throws NullPointerException
	{
		if (var == null)
		{
			throw new NullPointerException();
		}
		values.add(var);
		var.setOwner(this);
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
