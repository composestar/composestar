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

package Composestar.Core.COPPER3;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import Composestar.Core.CpsRepository2.Repository;
import Composestar.Core.CpsRepository2.Filters.FilterType;
import Composestar.Core.Master.ModuleNames;
import Composestar.Utils.Logging.CPSLogger;

/**
 * Record of all registered filters.
 * 
 * @author Michiel Hendriks
 */
public class FilterTypeMapping
{
	protected static final CPSLogger logger = CPSLogger.getCPSLogger(ModuleNames.COPPER + ".FilterTypes");

	public static final String RESOURCE_KEY = "COPPER3.FilterTypeMapping";

	/**
	 * Contains a mapping from strings representing filtertypes to FilterType
	 * objects
	 */
	private Map<String, FilterType> mapping;

	public FilterTypeMapping()
	{
		mapping = new HashMap<String, FilterType>();
	}

	public FilterTypeMapping(Repository repos)
	{
		this();
		createFilterTypeMapping(repos);
	}

	/**
	 * Get a filter type for a given filter name
	 * 
	 * @param name
	 * @return
	 */
	public FilterType getFilterType(String name)
	{
		return mapping.get(name.toLowerCase());
	}

	/**
	 * @return all registered filter type names
	 */
	public Set<String> getFilterTypeNames()
	{
		return Collections.unmodifiableSet(mapping.keySet());
	}

	/**
	 * @return all register filter types
	 */
	public Collection<FilterType> getFilterTypes()
	{
		return Collections.unmodifiableCollection(mapping.values());
	}

	/**
	 * Register a filter type
	 * 
	 * @param ft
	 */
	public void registerFilterType(FilterType ft)
	{
		String typeName;
		typeName = ft.getFilterName().toLowerCase();
		if (mapping.containsKey(typeName))
		{
			logger.warn(String.format("Two filters with the same name: %s", typeName));
		}
		else
		{
			logger.info(String.format("Registering filter type: %s", typeName));
		}
		mapping.put(typeName, ft);
	}

	/**
	 * Harvest filter types from the datastore
	 * 
	 * @param repos
	 */
	private void createFilterTypeMapping(Repository repos)
	{
		for (FilterType flt : repos.getAll(FilterType.class))
		{
			registerFilterType(flt);
		}
	}

}
