/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2005 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */
package Composestar.Core.CHKREP;

import Composestar.Core.RepositoryImplementation.*;
import Composestar.Core.Exception.*;

/**
 * BaseChecker is the common interface for all the checks of CHKREP
 */

public interface BaseChecker
{
	/**
	 * Performs the actual checks.
	 * 
	 * @return return non-fatal, so fatal == false!
	 */
	boolean performCheck();

	/**
	 * This is the function to perform the check It also gives the debug
	 * warnings and possible throws errors.
	 * 
	 * @param newDs The DataStore that needs to be checked
	 * @throws ModuleException
	 */
	void check(DataStore newDs) throws ModuleException;
}
