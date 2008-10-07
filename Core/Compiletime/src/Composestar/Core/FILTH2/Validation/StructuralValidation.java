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
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.Map.Entry;

import Composestar.Core.CpsRepository2.SISpec.FilterModuleConstraint;
import Composestar.Core.FILTH2.Model.Action;
import Composestar.Core.FILTH2.Model.Constraint;
import Composestar.Core.FILTH2.Model.ExcludeConstraint;
import Composestar.Core.FILTH2.Model.IncludeConstraint;
import Composestar.Core.Master.ModuleNames;
import Composestar.Utils.Logging.CPSLogger;

/**
 * Algorithm to detect structural constraint conflicts between the
 * include/exclude constraints.
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
	public static boolean isValid(Map<Constraint, FilterModuleConstraint> constraints)
	{
		for (Entry<Constraint, FilterModuleConstraint> entry : constraints.entrySet())
		{
			if (!(entry.getKey() instanceof ExcludeConstraint))
			{
				continue;
			}
			Action left = entry.getKey().getLeft();
			Action right = entry.getKey().getRight();
			Set<Constraint> visited = new HashSet<Constraint>();
			if (hasIncludePath(left, right, visited) || hasIncludePath(right, left, visited))
			{
				if (entry.getValue() != null)
				{
					logger.error(String.format("Constraint %s conflicts with include constraints: %s", entry.getKey(),
							visited), entry.getValue());
				}
				else
				{
					logger.error(String.format("Constraint %s conflicts with include constraints: %s", entry.getKey(),
							visited));
				}
				return false;
			}
		}
		return true;
	}

	private static boolean hasIncludePath(Action source, Action target, Set<Constraint> visited)
	{
		visited.clear();
		Queue<Action> openNodes = new LinkedList<Action>();
		// actually path == visited
		List<Action> path = new ArrayList<Action>();
		Action current = source;
		while (current != null && current != target)
		{
			path.add(current);
			current = selectOpenNode(current, target, openNodes, path, visited);
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
	private static Action selectOpenNode(Action current, Action target, Queue<Action> openNodes, List<Action> path,
			Set<Constraint> visited)
	{
		for (IncludeConstraint incc : current.getConstraints(IncludeConstraint.class))
		{
			Action act = incc.getRight();
			if (current == act)
			{
				// because constraints are added to both sides
				act = incc.getLeft();
			}
			if (!openNodes.contains(act) && !path.contains(act))
			{
				openNodes.add(act);
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
