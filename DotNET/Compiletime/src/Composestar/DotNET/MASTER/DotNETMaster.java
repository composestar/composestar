/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */

package Composestar.DotNET.MASTER;

import Composestar.Core.CpsProgramRepository.Filters.DefaultFilterFactory;
import Composestar.Core.Master.Master;
import Composestar.Core.RepositoryImplementation.DataMap;
import Composestar.Core.RepositoryImplementation.LegacyDataMap;

/**
 * Main entry point for the CompileTime. The Master class holds coreModules and
 * executes them in the order they are added.
 */
public class DotNETMaster extends Master
{
	@Override
	protected void initEvironment()
	{
		DataMap.setDataMapClass(LegacyDataMap.class);
	}

	@Override
	protected boolean loadConfiguration() throws Exception
	{
		if (!super.loadConfiguration())
		{
			return false;
		}
		DefaultFilterFactory filterFactory = new DefaultFilterFactory(resources.repository());
		filterFactory.addLegacyFilterTypes();
		resources.put(DefaultFilterFactory.RESOURCE_KEY, filterFactory);
		return true;
	}

	/**
	 * Compose* main function. Creates the Master object and invokes the run
	 * method.
	 * 
	 * @param args The command line arguments.
	 */
	public static void main(String[] args)
	{
		main(DotNETMaster.class, "ComposestarDotNET.jar", args);
	}
}
