/*
 * This file is part of the Compose* project.
 * http://composestar.sourceforge.net
 * Copyright (C) 2008 University of Twente.
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

package Composestar.Core.FILTH2.Execution;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Composestar.Core.FILTH2.Model.Action;
import Composestar.Core.FILTH2.Model.Constraint;
import Composestar.Core.FILTH2.Model.ExecutionManager;
import Composestar.Core.FILTH2.Model.ExecutionResult;
import Composestar.Core.Master.ModuleNames;
import Composestar.Utils.Logging.CPSLogger;

/**
 * @author Michiel Hendriks
 */
public class Simulator implements ExecutionManager
{
	protected static final CPSLogger logger = CPSLogger.getCPSLogger(ModuleNames.FILTH + ".Simulator");

	/**
	 * Contains the execution results for each action
	 */
	protected Map<Action, ExecutionResult> results;

	/**
	 * Contains a flag for each action to see if it is executable
	 */
	protected Map<Action, Boolean> executable;

	/**
	 * Results set when the action is executed
	 */
	protected Map<Action, ExecutionResult> fakeResults;

	/**
	 * The executed list
	 */
	protected List<Action> executed;

	public Simulator()
	{
		results = new HashMap<Action, ExecutionResult>();
		executable = new HashMap<Action, Boolean>();
	}

	/**
	 * Set the execution results to use when an action is executed
	 * 
	 * @param res
	 */
	public void setFakeResults(Map<Action, ExecutionResult> res)
	{
		fakeResults = res;
	}

	/**
	 * @return the list of actually executed actions
	 */
	public List<Action> getExecuted()
	{
		return Collections.unmodifiableList(executed);
	}

	/**
	 * Simulate the execution of an given order. Returns true for successful
	 * simulations.
	 * 
	 * @param order
	 * @return
	 */
	public boolean simulate(List<Action> order)
	{
		executed = new ArrayList<Action>();
		results.clear();
		executable.clear();
		// init the tables
		for (Action action : order)
		{
			setExecutable(action, true);
			setResult(action, ExecutionResult.NOT_EXECUTED);
		}
		// simulate
		for (Action action : order)
		{
			if (isExecutable(action))
			{
				executed.add(action);
				if (fakeResults != null && fakeResults.containsKey(action))
				{
					setResult(action, fakeResults.get(action));
				}
				else
				{
					setResult(action, ExecutionResult.UNSET);
				}
			}
			else
			{
				logger.info(String.format("Action %s is not executable, skipping", action.getName()));
			}
			// check constraints
			for (Constraint constraint : action.getConstraints())
			{
				if (!constraint.isValidOrder(order, this))
				{
					logger.error(String.format("Constraint %s violated", constraint.toString()));
					return false;
				}
			}
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.FILTH2.Model.ExecutionManager#getResult(Composestar.
	 * Core.FILTH2.Model.Action)
	 */
	public ExecutionResult getResult(Action action)
	{
		if (results.containsKey(action))
		{
			return results.get(action);
		}
		// might be in the fake results for the "constants"
		if (fakeResults.containsKey(action))
		{
			return fakeResults.get(action);
		}
		return ExecutionResult.NOT_EXECUTED;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.FILTH2.Model.ExecutionManager#isExecutable(Composestar
	 * .Core.FILTH2.Model.Action)
	 */
	public boolean isExecutable(Action action)
	{
		if (executable.containsKey(action))
		{
			return executable.get(action);
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.FILTH2.Model.ExecutionManager#setExecutable(Composestar
	 * .Core.FILTH2.Model.Action, boolean)
	 */
	public void setExecutable(Action action, boolean value)
	{
		executable.put(action, value);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.FILTH2.Model.ExecutionManager#setResult(Composestar.
	 * Core.FILTH2.Model.Action, Composestar.Core.FILTH2.Model.ExecutionResult)
	 */
	public void setResult(Action action, ExecutionResult result)
	{
		results.put(action, result);
	}
}
