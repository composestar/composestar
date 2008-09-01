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

package Composestar.Core.CpsRepository2Impl.SuperImposition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import Composestar.Core.CpsRepository2.FMParams.FMParameterValue;
import Composestar.Core.CpsRepository2.References.FilterModuleReference;
import Composestar.Core.CpsRepository2.SuperImposition.FilterModuleBinding;
import Composestar.Core.CpsRepository2.SuperImposition.Selector;
import Composestar.Core.CpsRepository2Impl.AbstractRepositoryEntity;

/**
 * @author Michiel Hendriks
 */
public class FilterModuleBindingImpl extends AbstractRepositoryEntity implements FilterModuleBinding
{
	private static final long serialVersionUID = -4692188251232501938L;

	protected FilterModuleReference filterModuleReference;

	protected Selector selector;

	protected List<FMParameterValue<?>> parameterValues;

	public FilterModuleBindingImpl()
	{
		parameterValues = new ArrayList<FMParameterValue<?>>();
	}

	/*
	 * (non-Javadoc)
	 * @seeComposestar.Core.CpsRepository2.SuperImposition.FilterModuleBinding#
	 * getFilterModuleReference()
	 */
	public FilterModuleReference getFilterModuleReference()
	{
		return filterModuleReference;
	}

	/*
	 * (non-Javadoc)
	 * @seeComposestar.Core.CpsRepository2.SuperImposition.FilterModuleBinding#
	 * getParameterValues()
	 */
	public List<FMParameterValue<?>> getParameterValues()
	{
		return Collections.unmodifiableList(parameterValues);
	}

	/*
	 * (non-Javadoc)
	 * @seeComposestar.Core.CpsRepository2.SuperImposition.FilterModuleBinding#
	 * getSelector()
	 */
	public Selector getSelector()
	{
		return selector;
	}

	/*
	 * (non-Javadoc)
	 * @seeComposestar.Core.CpsRepository2.SuperImposition.FilterModuleBinding#
	 * setFilterModuleReference
	 * (Composestar.Core.CpsRepository2.References.FilterModuleReference)
	 */
	public void setFilterModuleReference(FilterModuleReference ref) throws NullPointerException
	{
		if (ref == null)
		{
			throw new NullPointerException();
		}
		filterModuleReference = ref;
	}

	/*
	 * (non-Javadoc)
	 * @seeComposestar.Core.CpsRepository2.SuperImposition.FilterModuleBinding#
	 * setParameterValues(java.util.List)
	 */
	public void setParameterValues(List<FMParameterValue<?>> list) throws NullPointerException
	{
		if (list == null)
		{
			throw new NullPointerException();
		}
		parameterValues.clear();
		parameterValues.addAll(list);
	}

	/*
	 * (non-Javadoc)
	 * @seeComposestar.Core.CpsRepository2.SuperImposition.FilterModuleBinding#
	 * setSelector(Composestar.Core.CpsRepository2.SuperImposition.Selector)
	 */
	public void setSelector(Selector sel) throws NullPointerException
	{
		if (sel == null)
		{
			throw new NullPointerException();
		}
		selector = sel;
	}

}