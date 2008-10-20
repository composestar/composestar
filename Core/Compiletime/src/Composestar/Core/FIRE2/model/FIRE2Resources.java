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

package Composestar.Core.FIRE2.model;

import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import Composestar.Core.CpsRepository2.Concern;
import Composestar.Core.CpsRepository2.FilterModules.FilterModule;
import Composestar.Core.CpsRepository2.References.FilterModuleReference;
import Composestar.Core.CpsRepository2.SIInfo.ImposedFilterModule;
import Composestar.Core.FIRE2.preprocessing.FirePreprocessingResult;
import Composestar.Core.Master.ModuleNames;
import Composestar.Core.Resources.ModuleResourceManager;
import Composestar.Utils.Logging.CPSLogger;

/**
 * Manages the results of FIRE2
 * 
 * @author Michiel Hendriks
 */
public class FIRE2Resources implements ModuleResourceManager
{
	private static final long serialVersionUID = 5781711810188304052L;

	protected static final CPSLogger logger = CPSLogger.getCPSLogger(ModuleNames.FIRE);

	/**
	 * Mapping from filter module (fully qualified name) to preprocessing result
	 */
	protected Map<FilterModule, FirePreprocessingResult> ppResults;

	public FIRE2Resources()
	{
		ppResults = new WeakHashMap<FilterModule, FirePreprocessingResult>();
	}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Core.Resources.ModuleResourceManager#getModuleName()
	 */
	public String getModuleName()
	{
		return ModuleNames.FIRE;
	}

	/**
	 * Creates a fire model for the given concern, using the given
	 * FilterModuleOrder.
	 * 
	 * @param concern The concern for which the fire model needs to be created.
	 * @param order The FilterModuleOrder to be used.
	 */
	public FireModel getFireModel(Concern concern, List<ImposedFilterModule> order)
	{
		return new FireModel(this, concern, order);
	}

	/**
	 * Get the preprocessing result for the filter module with the given name
	 * 
	 * @param fmName
	 * @return
	 */
	public FirePreprocessingResult getPreprocessingResult(String fmName)
	{
		return ppResults.get(fmName);
	}

	/**
	 * Get the fire preprocessing result for this filter module
	 * 
	 * @param fm
	 * @return
	 */
	public FirePreprocessingResult getPreprocessingResult(FilterModule fm)
	{
		return ppResults.get(fm);
	}

	/**
	 * Get the fire preprocessing result for this filter module
	 * 
	 * @param fmref
	 * @return
	 */
	public FirePreprocessingResult getPreprocessingResult(FilterModuleReference fmref)
	{
		return getPreprocessingResult(fmref.getReference());
	}

	/**
	 * Add a preprocessing result to the resources
	 * 
	 * @param fm
	 * @param result
	 */
	public synchronized void addPreprocessingResult(FilterModule fm, FirePreprocessingResult result)
	{
		if (fm == null)
		{
			throw new IllegalArgumentException("Filter module can not be null");
		}
		if (result == null)
		{
			throw new IllegalArgumentException("Preprocessing result can not be null");
		}
		if (ppResults.put(fm, result) != null)
		{
			logger.warn(String.format("Previous result for %s lost", fm.getFullyQualifiedName()));
		}
	}

	/**
	 * Add a preprocessing result to the resources
	 * 
	 * @param fmref
	 * @param result
	 */
	public synchronized void addPreprocessingResult(FilterModuleReference fmref, FirePreprocessingResult result)
	{
		if (fmref == null)
		{
			throw new IllegalArgumentException("Filter module reference can not be null");
		}
		addPreprocessingResult(fmref, result);
	}

}
