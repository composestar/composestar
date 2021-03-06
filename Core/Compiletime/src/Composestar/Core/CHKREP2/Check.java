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
import Composestar.Core.Master.ModuleNames;
import Composestar.Core.Resources.CommonResources;
import Composestar.Utils.Logging.CPSLogger;

/**
 * CHKREP performs various checks on the repository in order to aid the
 * developer to write better code.
 * 
 * @author Michiel Hendriks
 */
public class Check implements CTCommonModule
{
	protected static final CPSLogger logger = CPSLogger.getCPSLogger(ModuleNames.CHKREP);

	/**
	 * List of checker classes to execute. To add a new checker simply add the
	 * class to the static initialization code.
	 */
	private static Set<Class<? extends AbstractChecker>> checkerClasses;

	/**
	 * Instances of the checkers to execute.
	 */
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
	 * @see Composestar.Core.Master.CTCommonModule#getModuleName()
	 */
	public String getModuleName()
	{
		return ModuleNames.CHKREP;
	}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Core.Master.CTCommonModule#getDependencies()
	 */
	public String[] getDependencies()
	{
		return new String[] { ModuleNames.COPPER };
	}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Core.Master.CTCommonModule#getImportance()
	 */
	public ModuleImportance getImportance()
	{
		return ModuleImportance.VALIDATION;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.Master.CTCommonModule#run(Composestar.Core.Resources
	 * .CommonResources)
	 */
	public ModuleReturnValue run(CommonResources resources) throws ModuleException
	{
		loadCheckers();
		boolean errorFree = runCheckers(resources);
		checkers = null;
		if (errorFree)
		{
			return ModuleReturnValue.OK;
		}
		else
		{
			return ModuleReturnValue.ERROR;
		}
	}

	/**
	 * Instantiates the configured checkers.
	 * 
	 * @see #checkerClasses
	 */
	protected void loadCheckers()
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

	/**
	 * Execute all registered repository checkers.
	 * 
	 * @param resources
	 */
	protected boolean runCheckers(CommonResources resources)
	{
		boolean errorFree = true;
		for (AbstractChecker checker : checkers)
		{
			checker.performCheck(resources.repository());
			errorFree &= checker.getResults().getErrors().isEmpty();
		}
		return errorFree;
	}
}
