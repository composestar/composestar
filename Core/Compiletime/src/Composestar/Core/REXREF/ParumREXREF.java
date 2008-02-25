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

package Composestar.Core.REXREF;

import Composestar.Core.CpsProgramRepository.CpsConcern.References.Reference;
import Composestar.Core.CpsProgramRepository.CpsConcern.References.SelectorReference;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.Resources.CommonResources;
import Composestar.Utils.Logging.CPSLogger;

/**
 * Small version of REXREF that only resolves selectors
 * 
 * @author Michiel Hendriks
 */
public class ParumREXREF extends Main
{
	public static final String MODULE_NAME = "ParumREXREF";

	protected static final CPSLogger logger = CPSLogger.getCPSLogger(Main.MODULE_NAME);

	public ParumREXREF()
	{}

	@Override
	public void run(CommonResources resources) throws ModuleException
	{
		ds = resources.repository();
		resolveReferences();
		if (hasErrors)
		{
			throw new ModuleException("There are unresolved references", MODULE_NAME);
		}
	}

	@Override
	protected void resolveReferences()
	{
		for (Object o : ds.getObjects())
		{
			if (o instanceof Reference)
			{
				if (((Reference) o).getResolved())
				{
					continue;
				}
			}
			else
			{
				continue;
			}
			if (o instanceof SelectorReference)
			{
				resolveReference((SelectorReference) o);
			}
		}
	}
}
