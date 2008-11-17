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

package Composestar.Java.FLIRT.Interpreter;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import Composestar.Core.CpsRepository2.FilterElements.CanonAssignment;
import Composestar.Core.CpsRepository2.TypeSystem.CpsVariable;

/**
 * Contains the filter arguments
 * 
 * @author Michiel Hendriks
 */
public class FilterArguments
{
	public Map<String, CanonAssignment> args;

	public FilterArguments()
	{
		args = new HashMap<String, CanonAssignment>();
	}

	public FilterArguments(FilterArguments copyFrom)
	{
		args = new HashMap<String, CanonAssignment>(copyFrom.args);
	}

	/**
	 * Add a single argument
	 * 
	 * @param arg
	 */
	public void add(CanonAssignment arg)
	{
		if (arg == null)
		{
			return;
		}
		if (arg.getProperty() == null)
		{
			return;
		}
		args.put(arg.getProperty().getBaseName(), arg);
	}

	/**
	 * Add a list of filter arguments
	 * 
	 * @param arglist
	 */
	public void addAll(Collection<CanonAssignment> arglist)
	{
		for (CanonAssignment arg : arglist)
		{
			add(arg);
		}
	}

	/**
	 * @return All filter arguments
	 */
	public Collection<CanonAssignment> get()
	{
		return args.values();
	}

	/**
	 * A get filter argument value using the name
	 * 
	 * @param baseName
	 * @return
	 */
	public CpsVariable get(String baseName)
	{
		CanonAssignment asn = args.get(baseName);
		if (asn == null)
		{
			return null;
		}
		return asn.getProperty();
	}
}
