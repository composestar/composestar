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

import Composestar.Core.CpsRepository2.References.FilterModuleReference;
import Composestar.Core.CpsRepository2.SISpec.Constraints.FilterModuleConstraintValue;
import Composestar.Core.CpsRepository2Impl.AbstractRepositoryEntity;

/**
 * A filter module constraint that uses a filter module reference.
 * 
 * @author Michiel Hendriks
 */
public class FilterModuleConstraintValueImpl extends AbstractRepositoryEntity implements FilterModuleConstraintValue
{
	private static final long serialVersionUID = 1752409162279862032L;

	/**
	 * A reference to a filter module.
	 */
	protected FilterModuleReference filterModuleReference;

	/**
	 * @param fmr The filter module reference
	 * @throws NullPointerException Thrown when the filter module reference is
	 *             null.
	 */
	public FilterModuleConstraintValueImpl(FilterModuleReference fmr) throws NullPointerException
	{
		super();
		if (fmr == null)
		{
			throw new NullPointerException();
		}
		filterModuleReference = fmr;
	}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Core.CpsRepository2.SISpec.FilterModuleConstraintValue
	 * #getFilterModuleReference()
	 */
	public FilterModuleReference getFilterModuleReference()
	{
		return filterModuleReference;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.SISpec.ConstraintValue#getStringValue()
	 */
	public String getStringValue()
	{
		return filterModuleReference.getReferenceId();
	}
}
