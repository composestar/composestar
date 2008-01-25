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

package Composestar.CwC.MASTER;

import Composestar.Core.CpsProgramRepository.Filters.DefaultFilterFactory;
import Composestar.Core.CpsProgramRepository.Filters.FilterTypeNames;
import Composestar.Core.Master.Master;

/**
 * @author Michiel Hendriks
 */
public class CwCMaster extends Master
{
	@Override
	protected void loadConfiguration() throws Exception
	{
		super.loadConfiguration();
		// FIXME this probably needs to be improved, how to load it properly?
		// where to get the custom filters from?
		DefaultFilterFactory filterFactory = new DefaultFilterFactory(resources.repository());
		String[] filters = { FilterTypeNames.DISPATCH, FilterTypeNames.SEND, FilterTypeNames.ERROR,
				FilterTypeNames.BEFORE, FilterTypeNames.AFTER, FilterTypeNames.SUBSTITUTION };
		filterFactory.createFilterTypes(filters);
		resources.put(DefaultFilterFactory.RESOURCE_KEY, filterFactory);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		if (args.length == 0)
		{
			System.out.println("Usage: java -jar ComposestarCwC.jar [options] <config file>");
			return;
		}
		main(CwCMaster.class, args);
	}

}
