/*
 * This file is part of the Compose* project.
 * http://composestar.sourceforge.net
 * Copyright (C) 2009 University of Twente.
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

package Composestar.Core.SECRET3.Model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Abstract operation sequence
 * 
 * @author Michiel Hendriks
 */
public abstract class OperationSequence
{
	/**
	 * A mapping of resources to a list of operations.
	 */
	protected Map<Resource, List<String>> operations;

	protected OperationSequence()
	{
		operations = new HashMap<Resource, List<String>>();
	}

	/**
	 * Add a new operation sequence for a given resource
	 * 
	 * @param resource
	 * @param opsequence
	 */
	public void addOperations(Resource resource, List<String> opsequence)
	{
		if (resource == null)
		{
			throw new IllegalArgumentException("Resource can not be null");
		}
		// remove empty operations
		while (opsequence.remove(""))
		{
			// nop
		}
		if (opsequence.size() == 0)
		{
			return;
		}
		List<String> lst = operations.get(resource);
		if (lst == null)
		{
			lst = new ArrayList<String>();
			operations.put(resource, lst);
		}
		lst.addAll(opsequence);
	}

	/**
	 * @see #addOperations(Resource, List)
	 * @param resource
	 * @param opsequence
	 */
	public void addOperations(Resource resource, String[] opsequence)
	{
		addOperations(resource, Arrays.asList(opsequence));
	}

	/**
	 * Add a operation sequence to the given resource list. The provides string
	 * is split on the semicolon.
	 * 
	 * @param resource
	 * @param opsequence
	 */
	public void addOperations(Resource resource, String opsequence)
	{
		if (opsequence == null || opsequence.trim().length() == 0)
		{
			throw new IllegalArgumentException("Operation sequence can not be null or empty");
		}
		addOperations(resource, opsequence.trim().split(";"));
	}

	/**
	 * @return the mapping of resources to operations
	 */
	public Map<Resource, List<String>> getOperations()
	{
		return Collections.unmodifiableMap(operations);
	}
}
