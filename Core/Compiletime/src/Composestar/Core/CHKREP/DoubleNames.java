/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */
package Composestar.Core.CHKREP;

import Composestar.Core.Exception.ModuleException;
import Composestar.Core.RepositoryImplementation.DataStore;

public class DoubleNames extends BaseChecker
{

	@Override
	public boolean performCheck()
	{
		return true;
	}

	@Override
	public void check(DataStore ds) throws ModuleException
	{}
}
