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

import Composestar.Core.CpsProgramRepository.Legacy.LegacyFilterTypes;
import Composestar.Core.Master.Master;
import Composestar.Core.RepositoryImplementation.DataMap;
import Composestar.Core.RepositoryImplementation.LegacyDataMap;

/**
 * Main entry point for the CompileTime. The Master class holds coreModules and
 * executes them in the order they are added.
 */
public class DotNETMaster extends Master
{
	protected void initEvironment()
	{
		DataMap.setDataMapClass(LegacyDataMap.class);
		LegacyFilterTypes.useLegacyFilterTypes = true;
	}

	/**
	 * Compose* main function. Creates the Master object and invokes the run
	 * method.
	 * 
	 * @param args The command line arguments.
	 */
	public static void main(String[] args)
	{
		if (args.length == 0)
		{
			System.out.println("Usage: java -jar ComposestarDotNET.jar [options] <config file>");
			return;
		}
		main(DotNETMaster.class, args);
	}
}
