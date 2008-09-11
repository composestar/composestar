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

import Composestar.Core.CpsRepository2.FMParams.FMParameter;
import Composestar.Core.CpsRepository2Impl.AbstractQualifiedRepositoryEntity;

/**
 * @author Michiel Hendriks
 */
public class FMParameterImpl extends AbstractQualifiedRepositoryEntity implements FMParameter
{
	private static final long serialVersionUID = -5073109061680829971L;

	/**
	 * True if this is a parameter list
	 */
	protected boolean parameterList;

	/**
	 * @param entityName
	 * @throws NullPointerException
	 * @throws IllegalArgumentException
	 */
	public FMParameterImpl(String entityName) throws NullPointerException, IllegalArgumentException
	{
		super(entityName);
		if (entityName.startsWith("??") && !entityName.startsWith("?", 2) && entityName.length() > 2)
		{
			parameterList = true;
		}
		else if (entityName.startsWith("?") && !entityName.startsWith("?", 1) && entityName.length() > 1)
		{
			// also correct
		}
		else
		{
			throw new IllegalArgumentException(String.format("%s is not a valid parameter name", entityName));
		}
	}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Core.CpsRepository2.FMParams.FMParameter#getRawName()
	 */
	public String getRawName()
	{
		if (parameterList)
		{
			return name.substring(2);
		}
		else
		{
			return name.substring(1);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.FMParams.FMParameter#isParameterList()
	 */
	public boolean isParameterList()
	{
		return parameterList;
	}

}
