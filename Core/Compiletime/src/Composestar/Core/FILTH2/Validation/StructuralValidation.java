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

package Composestar.Core.FILTH2.Validation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import Composestar.Core.CpsRepository2.SISpec.Constraints.Constraint;
import Composestar.Core.CpsRepository2.SISpec.Constraints.ConstraintValue;
import Composestar.Core.CpsRepository2Impl.SISpec.Constraints.ExcludeConstraint;
import Composestar.Core.CpsRepository2Impl.SISpec.Constraints.IncludeConstraint;
import Composestar.Core.Master.ModuleNames;
import Composestar.Utils.Logging.CPSLogger;

/**
 * Algorithm to detect structural constraint conflicts between the
 * include/exclude constraints. NOTE: This algorithm will assume that
 * include/exclude only contain FilterModuleConstraintValue as arguments
 * 
 * @author Michiel Hendriks
 */
public final class StructuralValidation
{
	protected static final CPSLogger logger = CPSLogger.getCPSLogger(ModuleNames.FILTH + ".Validation");

	private StructuralValidation()
	{}

	/**
	 * Check all structural constrains to see if there is an conflict
	 * 
	 * @param constraints
	 * @return
	 */
	public static boolean isValid(Set<Constraint> constraints)
	{
		Set<IncludeConstraint> incl = new HashSet<IncludeConstraint>();
		Set<ExcludeConstraint> excl = new HashSet<ExcludeConstraint>();

		for (Constraint entry : constraints)
		{
			if (entry instanceof ExcludeConstraint)
			{
				excl.add((ExcludeConstraint) entry);
			}
			else if (entry instanceof IncludeConstraint)
			{
				incl.add((IncludeConstraint) entry);
			}
		}

		for (ExcludeConstraint entry : excl)
		{
			ConstraintValue[] args = entry.getArguments();
			Set<IncludeConstraint> visited = new HashSet<IncludeConstraint>();
			if (hasIncludePath(args[0].getStringValue(), args[1].getStringValue(), incl, visited)
					|| hasIncludePath(args[1].getStringValue(), args[0].getStringValue(), incl, visited))
			{
				logger.error(String.format("Constraint %s conflicts with include constraints: %s", entry, visited));
				return false;
			}
		}
		return true;
	}

	private static boolean hasIncludePath(String source, String target, Set<IncludeConstraint> constraints,
			Set<IncludeConstraint> visited)
	{
		visited.clear();
		Queue<String> openNodes = new LinkedList<String>();
		// actually path == visited
		List<String> path = new ArrayList<String>();
		String current = source;
		while (current != null && !current.equals(target))
		{
			path.add(current);
			current = selectOpenNode(current, target, openNodes, path, constraints, visited);
		}
		return current != null;
	}

	/**
	 * Select an open node (or the target if it's reachable) from the list
	 * 
	 * @param current
	 * @param target
	 * @param openNodes
	 * @param path
	 * @return
	 */
	private static String selectOpenNode(String current, String target, Queue<String> openNodes, List<String> path,
			Set<IncludeConstraint> constraints, Set<IncludeConstraint> visited)
	{
		for (IncludeConstraint incc : constraints)
		{
			String val = incc.getArguments()[0].getStringValue();
			if (!openNodes.contains(val) && !path.contains(val))
			{
				openNodes.add(val);
				visited.add(incc);
			}
		}
		if (openNodes.contains(target))
		{
			return target;
		}
		return openNodes.poll();
	}
}
