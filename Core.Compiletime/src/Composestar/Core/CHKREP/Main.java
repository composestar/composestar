/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */
/**
 * Main class
 */
package Composestar.Core.CHKREP;

import Composestar.Core.Exception.ModuleException;
import Composestar.Core.Master.CTCommonModule;
import Composestar.Core.Master.CommonResources;
import Composestar.Core.RepositoryImplementation.DataStore;

/**
 * Main class used to run the parser
 */
public class Main implements CTCommonModule
{
	/**
	 * Constructor
	 */
	public Main()
	{}

	/**
	 * Run methods called by master
	 * 
	 * @param resources Resources received from master (such as the repository)
	 */
	public void run(CommonResources resources) throws ModuleException
	{
		DoCheck dc = new DoCheck();
		dc.go(DataStore.instance());
	}
}
