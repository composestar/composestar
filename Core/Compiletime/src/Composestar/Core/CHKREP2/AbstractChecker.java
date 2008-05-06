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

package Composestar.Core.CHKREP2;

import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.Utils.Logging.CPSLogger;

/**
 * Base class for all repository checkers.
 * 
 * @author Michiel Hendriks
 */
public abstract class AbstractChecker
{
	protected static final CPSLogger logger = CPSLogger.getCPSLogger(Check.MODULE_NAME);

	/**
	 * This will contain the results of an executed check.
	 * 
	 * @see #getResults()
	 */
	protected CheckResults results;

	public AbstractChecker()
	{
		results = new CheckResults();
	}

	/**
	 * Will be called by the Check class when this module should perform it's
	 * checks.
	 * 
	 * @param repository
	 */
	public abstract void performCheck(DataStore repository);

	/**
	 * Return the CheckResults instance.
	 * 
	 * @return
	 */
	public CheckResults getResults()
	{
		return results;
	}
}
