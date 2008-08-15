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

package Composestar.Core.CpsRepository2Impl;

import java.util.Arrays;
import java.util.List;

/**
 * A primitive concern is used for non-cpsconcerns in the base program.
 * 
 * @author Michiel Hendriks
 */
public class PrimitiveConcern extends AbstractConcern
{
	private static final long serialVersionUID = -380338713585814918L;

	/**
	 * Create a new primitive concern using a fully qualified name
	 * 
	 * @param fullyQualifiedName
	 * @throws IndexOutOfBoundsException Thrown when the list is empty;
	 * @throws IllegalArgumentException Thrown when the name is empty
	 */
	public PrimitiveConcern(String... fullyQualifiedName) throws NullPointerException, IllegalArgumentException
	{
		this(Arrays.asList(fullyQualifiedName));
	}

	/**
	 * Create a new primitive concern using a fully qualified name
	 * 
	 * @param fullyQualifiedName
	 * @throws NullPointerException Thrown when the list is null
	 * @throws IndexOutOfBoundsException Thrown when the list is empty;
	 * @throws IllegalArgumentException Thrown when the name is empty
	 */
	public PrimitiveConcern(List<String> fullyQualifiedName) throws NullPointerException, IndexOutOfBoundsException,
			IllegalArgumentException
	{
		this(fullyQualifiedName.get(fullyQualifiedName.size() - 1), fullyQualifiedName.subList(0, fullyQualifiedName
				.size() - 1));
	}

	/**
	 * Create a new primitive concern
	 * 
	 * @param name
	 * @param namespace
	 * @throws IllegalArgumentException when the name is empty
	 * @throws NullPointerException when the name is null
	 */
	public PrimitiveConcern(String name, List<String> namespace) throws NullPointerException, IllegalArgumentException
	{
		super(name, namespace);
	}
}
