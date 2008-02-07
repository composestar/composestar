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

package Composestar.CwC.Filters;

import Composestar.Core.COPPER2.FilterTypeMapping;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterAction;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterType;
import Composestar.Core.CpsProgramRepository.Filters.DefaultFilterFactory;
import Composestar.Core.CpsProgramRepository.Filters.FilterActionNames;
import Composestar.Core.CpsProgramRepository.Filters.FilterTypeNames;
import Composestar.Core.RepositoryImplementation.DataStore;

/**
 * The filter factor for CwC. Certain default filter actions are simply not
 * supported.
 * 
 * @author Michiel Hendriks
 */
public class CwCFilterFactory extends DefaultFilterFactory
{
	public CwCFilterFactory(DataStore repo)
	{
		super(repo);
	}

	public CwCFilterFactory(FilterTypeMapping ftm, DataStore repo)
	{
		super(ftm, repo);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.CpsProgramRepository.Filters.DefaultFilterFactory#addLegacyFilterTypes()
	 */
	@Override
	public void addLegacyFilterTypes() throws UnsupportedFilterTypeException
	{
		throw new UnsupportedOperationException("Legacy custom filters are not supported in Compose*/CwC");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.CpsProgramRepository.Filters.DefaultFilterFactory#allowLegacyCustomFilters()
	 */
	@Override
	public boolean allowLegacyCustomFilters()
	{
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.CpsProgramRepository.Filters.DefaultFilterFactory#createLegacyCustomFilterType(java.lang.String)
	 */
	@Override
	public FilterType createLegacyCustomFilterType(String name) throws UnsupportedFilterTypeException
	{
		throw new UnsupportedFilterTypeException(FilterTypeNames.CUSTOM);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.CpsProgramRepository.Filters.DefaultFilterFactory#getMetaAction()
	 */
	@Override
	public FilterAction getMetaAction() throws UnsupportedFilterActionException
	{
		throw new UnsupportedFilterActionException(FilterActionNames.META_ACTION);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.CpsProgramRepository.Filters.DefaultFilterFactory#setAllowLegacyCustomFilters(boolean)
	 */
	@Override
	public void setAllowLegacyCustomFilters(boolean value)
	{
		throw new UnsupportedOperationException("Legacy custom filters are not supported in Compose*/CwC");
	}

}
