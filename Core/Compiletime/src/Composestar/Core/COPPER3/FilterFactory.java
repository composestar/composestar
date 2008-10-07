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

import Composestar.Core.CpsRepository2.Repository;
import Composestar.Core.CpsRepository2.Filters.FilterType;
import Composestar.Core.CpsRepository2.Filters.PrimitiveFilterType;
import Composestar.Core.CpsRepository2Impl.Filters.PrimitiveFilterTypeImpl;

/**
 * @author Michiel Hendriks
 */
public class FilterFactory
{
	public static final String RESOURCE_KEY = "COPPER3.FilterFactory";

	protected Repository repository;

	protected FilterTypeMapping mapping;

	public FilterFactory(Repository repo)
	{
		repository = repo;
	}

	public FilterFactory(Repository repo, FilterTypeMapping ftmap)
	{
		repository = repo;
		mapping = ftmap;
	}

	public FilterType createFilter(String name)
	{
		if (name.indexOf('.') > -1)
		{
			// FIXME: error
			return null;
		}
		FilterType res = new PrimitiveFilterTypeImpl(name);
		if (mapping != null)
		{
			mapping.registerFilterType(res);
		}
		repository.add(res);
		return res;
	}

	public void setTypeMapping(FilterTypeMapping filterTypes)
	{
		mapping = filterTypes;
		for (PrimitiveFilterType ft : repository.getAll(PrimitiveFilterType.class))
		{
			mapping.registerFilterType(ft);
		}
	}
}
