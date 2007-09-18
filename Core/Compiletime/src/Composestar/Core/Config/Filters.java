/*
 * This file is part of the Compose* project.
 * http://composestar.sourceforge.net
 * Copyright (C) 2007 University of Twente.
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

package Composestar.Core.Config;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Contains the filter definitions. It either contains a list of custom filters
 * or a lists of filter types and actions. Technically both options can exist at
 * the same time.
 * 
 * @author Michiel Hendriks
 */
public class Filters implements Serializable
{
	private static final long serialVersionUID = -6488920626356100535L;

	protected Set<CustomFilter> customFilters;

	protected Map<String, FilterType> filterTypes;

	protected Map<String, FilterAction> filterActions;

	public Filters()
	{
		customFilters = new HashSet<CustomFilter>();
		filterTypes = new HashMap<String, FilterType>();
		filterActions = new HashMap<String, FilterAction>();
	}

	public Set<CustomFilter> getCustomFilters()
	{
		return Collections.unmodifiableSet(customFilters);
	}

	public Map<String, FilterType> getFilterTypes()
	{
		return Collections.unmodifiableMap(filterTypes);
	}

	public Map<String, FilterAction> getFilterActions()
	{
		return Collections.unmodifiableMap(filterActions);
	}

	public void add(CustomFilter filter)
	{
		if (filter == null)
		{
			return;
		}
		customFilters.add(filter);
	}

	public void add(FilterType filter)
	{
		if (filter == null)
		{
			return;
		}
		if (filter.getName() == null)
		{
			throw new IllegalArgumentException("FilterType does not have a valid name");
		}
		filterTypes.put(filter.getName(), filter);
	}

	public void add(FilterAction filter)
	{
		if (filter == null)
		{
			return;
		}
		if (filter.getName() == null)
		{
			throw new IllegalArgumentException("FilterAction does not have a valid name");
		}
		filterActions.put("", filter);
	}
}
