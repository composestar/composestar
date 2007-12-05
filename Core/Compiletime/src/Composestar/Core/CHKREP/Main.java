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
import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.Core.Resources.CommonResources;

/**
 * Main class used to run the parser
 */
public class Main implements CTCommonModule
{
	public static final String MODULE_NAME = "CHKREP";

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
		DataStore ds = DataStore.instance();

		// Testbench
		NotUsedInternals nui = new NotUsedInternals();
		nui.check(ds);

		NotUsedExternals nue = new NotUsedExternals();
		nue.check(ds);

		NotUsedCondition nuc = new NotUsedCondition();
		nuc.check(ds);

		NotUsedSelector nus = new NotUsedSelector();
		nus.check(ds);

		// Allready checked by REXREF
		// ExistFilterModule efm = new ExistFilterModule();
		// efm.check(ds);

		// Allready checked by REXREF
		// ExistSelector es = new ExistSelector();
		// es.check(ds);

		ExistCondition ec = new ExistCondition();
		ec.check(ds);
	}
}
