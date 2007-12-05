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

import java.util.HashSet;
import java.util.Set;

import Composestar.Core.Exception.ModuleException;
import Composestar.Core.Master.CTCommonModule;
import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.Core.Resources.CommonResources;
import Composestar.Utils.Logging.CPSLogger;

/**
 * Entry point for CHKREP. It will load and execute the various repository
 * checkers.
 * 
 * @author Michiel Hendriks
 */
public class Check implements CTCommonModule
{
	public static final String MODULE_NAME = "CHKREP";

	protected static final CPSLogger logger = CPSLogger.getCPSLogger(MODULE_NAME);

	/**
	 * List of checker classes
	 */
	private static Set<Class<? extends AbstractChecker>> checkerClasses;

	public Set<AbstractChecker> checkers;

	static
	{
		checkerClasses = new HashSet<Class<? extends AbstractChecker>>();
		checkerClasses.add(FilterModuleChecker.class);
		checkerClasses.add(SIChecker.class);
	}

	public Check()
	{}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.Master.CTCommonModule#run(Composestar.Core.Resources.CommonResources)
	 */
	public void run(CommonResources resources) throws ModuleException
	{
		loadCheckers(resources);
		runCheckers(resources);
		checkers = null;
	}

	protected void loadCheckers(CommonResources resources)
	{
		checkers = new HashSet<AbstractChecker>();
		for (Class<? extends AbstractChecker> chkclass : checkerClasses)
		{
			AbstractChecker checker;
			try
			{
				checker = chkclass.newInstance();
				checkers.add(checker);
			}
			catch (InstantiationException e)
			{
				logger.warn(String.format("Unable to load checker class %s: %s", chkclass.getName(), e.getMessage()));
			}
			catch (IllegalAccessException e)
			{
				logger.warn(String.format("Unable to load checker class %s: %s", chkclass.getName(), e.getMessage()));
			}
		}
	}

	protected void runCheckers(CommonResources resources)
	{
		DataStore ds = DataStore.instance();
		for (AbstractChecker checker : checkers)
		{
			checker.performCheck(ds);
		}
	}
}
