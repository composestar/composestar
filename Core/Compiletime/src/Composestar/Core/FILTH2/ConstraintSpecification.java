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

package Composestar.Core.FILTH2;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * Holds constraint definitions which will be used by FILTH
 * 
 * @author Michiel Hendriks
 */
public class ConstraintSpecification implements Serializable
{
	public static final String RESOURCE_KEY = "FILTH2.ConstraintSpecification";

	private static final long serialVersionUID = 7648250138480258230L;

	/**
	 * All constraint definition
	 */
	protected Set<ConstraintDefinition> definitions;

	public ConstraintSpecification()
	{
		definitions = new HashSet<ConstraintDefinition>();
	}

	/**
	 * Add a new constraint
	 * 
	 * @param type
	 * @param arguments
	 * @return
	 */
	public ConstraintDefinition addDefinition(String type, String... arguments)
	{
		ConstraintDefinition result = new ConstraintDefinition(type, arguments);
		definitions.add(result);
		return result;
	}

	/**
	 * Add a new constraint
	 * 
	 * @param type
	 * @param arguments
	 * @return
	 */
	public ConstraintDefinition addDefinition(String type, List<String> arguments)
	{
		return addDefinition(type, arguments.toArray(new String[arguments.size()]));
	}

	/**
	 * Add a new constraint definition
	 * 
	 * @param def
	 */
	public void addDefinition(ConstraintDefinition def)
	{
		definitions.add(def);
	}

	/**
	 * Get all constraint definitions
	 * 
	 * @return
	 */
	public Set<ConstraintDefinition> getDefinitions()
	{
		return Collections.unmodifiableSet(definitions);
	}
}
