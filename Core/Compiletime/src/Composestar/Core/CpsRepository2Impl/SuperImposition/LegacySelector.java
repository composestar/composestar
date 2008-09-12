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

package Composestar.Core.CpsRepository2Impl.SuperImposition;

/**
 * The legacy selector. Comes in two forms: *=select.this.class and
 * *:this.class.and.children
 * 
 * @author Michiel Hendriks
 */
public class LegacySelector extends AbstractSelector
{
	private static final long serialVersionUID = -6014012823126879229L;

	/**
	 * The fully qualified name of the class that is selected
	 */
	protected String classSelection;

	/**
	 * If true include sub classes
	 */
	protected boolean includeChildren;

	/**
	 * @param entityName The name of the selector
	 * @param classFqn The fully qualified name of the class to select
	 * @param andSubs If true also select all subclasses
	 * @throws NullPointerException Thrown when the name or FQN are null
	 * @throws IllegalArgumentException Thrown when the name or FQN are empty
	 */
	public LegacySelector(String entityName, String classFqn, boolean andSubs) throws NullPointerException,
			IllegalArgumentException
	{
		super(entityName);
		if (classFqn == null)
		{
			throw new NullPointerException("Class selection can not be null");
		}
		if (classFqn.isEmpty())
		{
			throw new IllegalArgumentException("Class selection can not be empty");
		}
		classSelection = classFqn;
		includeChildren = andSubs;
	}

	/**
	 * @return The selected class name
	 */
	public String getClassSelection()
	{
		return classSelection;
	}

	/**
	 * @return True if all subclasses should also be selected.
	 */
	public boolean isIncludeChildren()
	{
		return includeChildren;
	}

}
