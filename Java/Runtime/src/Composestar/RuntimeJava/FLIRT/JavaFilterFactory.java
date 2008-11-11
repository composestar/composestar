/*
 * This file is part of the Compose* project.
 * http://composestar.sourceforge.net
 * Copyright (C) 2005-2008 University of Twente.
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
package Composestar.RuntimeJava.FLIRT;

import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterType;
import Composestar.Core.CpsProgramRepository.Filters.LegacyCustomFilterType;
import Composestar.RuntimeCore.FLIRT.Filtertypes.FilterFactory;
import Composestar.RuntimeCore.FLIRT.Filtertypes.FilterTypeRuntime;
import Composestar.RuntimeCore.Utils.Debug;

public class JavaFilterFactory extends FilterFactory
{
	public JavaFilterFactory()
	{
		setInstance(this);
	}

	/*
	 * (non-Javadoc)
	 * @seeComposestar.RuntimeCore.FLIRT.Filtertypes.FilterFactory#
	 * getCustomFilterTypeFor
	 * (Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules
	 * .FilterType)
	 */
	@Override
	protected FilterTypeRuntime getCustomFilterTypeFor(FilterType filterType)
	{
		String filtername = ((LegacyCustomFilterType) filterType).getClassName();
		try
		{
			Class<?> cls = ClassLoader.getSystemClassLoader().loadClass(filtername);
			if (FilterTypeRuntime.class.isAssignableFrom(cls))
			{
				FilterTypeRuntime ftr = (FilterTypeRuntime) cls.newInstance();
				return ftr;
			}
		}
		catch (ClassNotFoundException e)
		{
			Debug.out(Debug.MODE_ERROR, "FLIRT", e.getMessage());
			return null;
		}
		catch (InstantiationException e)
		{
			Debug.out(Debug.MODE_ERROR, "FLIRT", e.getMessage());
			return null;
		}
		catch (IllegalAccessException e)
		{
			Debug.out(Debug.MODE_ERROR, "FLIRT", e.getMessage());
			return null;
		}
		Debug.out(Debug.MODE_ERROR, "FLIRT", "Filter type " + ((LegacyCustomFilterType) filterType).getName()
				+ " not found");
		return null;
	}
}
