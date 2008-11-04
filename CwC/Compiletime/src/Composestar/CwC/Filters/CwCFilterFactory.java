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

import Composestar.Core.COPPER3.FilterTypeFactory;
import Composestar.Core.CpsRepository2.Repository;
import Composestar.Core.CpsRepository2.Filters.FilterAction;

/**
 * The filter factor for CwC. Certain default filter actions are simply not
 * supported.
 * 
 * @author Michiel Hendriks
 */
public class CwCFilterFactory extends FilterTypeFactory
{
	public CwCFilterFactory(Repository repo)
	{
		super(repo);
		allowCustomFilterTypeCreation = false;
	}

	/*
	 * (non-Javadoc)
	 * @seeComposestar.Core.CpsProgramRepository.Filters.DefaultFilterFactory#
	 * allowLegacyCustomFilters()
	 */
	@Override
	public void setAllowCustomFilterTypeCreation(boolean value)
	{
		throw new UnsupportedOperationException("On-the-fly custom filters are not supported in Compose*/CwC");
	}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Core.COPPER3.FilterTypeFactory#getMetaAction()
	 */
	@Override
	public FilterAction getMetaAction()
	{
		// TODO throw more specific (non-runtime) exception
		throw new UnsupportedOperationException("Meta action not available in Compose*/CwC");
	}
}
