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

import java.util.HashMap;
import java.util.Map;

import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterModule;
import Composestar.Core.CpsProgramRepository.CpsConcern.References.FilterModuleReference;
import Composestar.Core.FILTH.FilterModuleOrder;
import Composestar.Core.FIRE2.preprocessing.FirePreprocessingResult;
import Composestar.Core.FIRE2.preprocessing.Preprocessor;
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

	protected static final CPSLogger logger = CPSLogger.getCPSLogger(Preprocessor.MODULE_NAME);

	protected Map<String, FirePreprocessingResult> ppResults;

	public FIRE2Resources()
	{
		ppResults = new HashMap<String, FirePreprocessingResult>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.Resources.ModuleResourceManager#getModuleName()
	 */
	public String getModuleName()
	{
		return Preprocessor.MODULE_NAME;
	}

	/**
	 * Creates a fire model for the given concern, using the given
	 * FilterModuleOrder.
	 * 
	 * @param concern The concern for which the fire model needs to be created.
	 * @param order The FilterModuleOrder to be used.
	 */
	public FireModel getFireModel(Concern concern, FilterModuleOrder order)
	{
		return new FireModel(this, concern, order);
	}

	/**
	 * Creates a FireModel for a given concern and a given filter set, specified
	 * by the FilterModule array.
	 * 
	 * @param concern
	 * @param modules
	 */
	public FireModel getFireModel(Concern concern, FilterModule[] modules)
	{
		return new FireModel(this, concern, modules);
	}

	public FirePreprocessingResult getPreprocessingResult(String fmName)
	{
		return ppResults.get(fmName);
	}

	public FirePreprocessingResult getPreprocessingResult(FilterModule fm)
	{
		return getPreprocessingResult(fm.getQualifiedName());
	}

	public FirePreprocessingResult getPreprocessingResult(FilterModuleReference fmref)
	{
		return getPreprocessingResult(fmref.getRef());
	}

	public void addPreprocessingResult(String fmName, FirePreprocessingResult result)
	{
		if (fmName == null || fmName.trim().length() == 0)
		{
			throw new IllegalArgumentException("Filter module name can not be null or empty");
		}
		if (result == null)
		{
			throw new IllegalArgumentException("Preprocessing result can not be null");
		}
		if (ppResults.put(fmName, result) != null)
		{
			logger.warn(String.format("Previous result for %s lost", fmName));
		}
	}

	public void addPreprocessingResult(FilterModule fm, FirePreprocessingResult result)
	{
		if (fm == null)
		{
			throw new IllegalArgumentException("Filter module can not be null");
		}
		addPreprocessingResult(fm.getQualifiedName(), result);
	}

	public void addPreprocessingResult(FilterModuleReference fmref, FirePreprocessingResult result)
	{
		if (fmref == null)
		{
			throw new IllegalArgumentException("Filter module reference can not be null");
		}
		addPreprocessingResult(fmref.getRef(), result);
	}

}
