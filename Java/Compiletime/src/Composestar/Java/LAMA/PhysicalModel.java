/*
 * This file is part of the Compose* project.
 * http://composestar.sourceforge.net
 * Copyright (C) 2011 University of Twente.
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

package Composestar.Java.LAMA;

import java.util.Collection;
import java.util.HashSet;

import Composestar.Core.LAMA.ProgramElement;
import Composestar.Core.LAMA.UnitResult;
import Composestar.Core.LOLA.metamodel.EUnitType;

/**
 *
 * @author arjan
 */
public class PhysicalModel extends ProgramElement
{
	

	/**
	 * 
	 */
	private static final long serialVersionUID = -2916247101075138237L;

	public String name;



	/**
	 * Constructor
	 * 
	 * @param c - <code>java.lang.Class</code> instance.
	 */
	public PhysicalModel(String name)
	{
		super();
		this.name = name;
	}

	

	public String getName()
	{
		return name;
	}



	public void setName(String name)
	{
		this.name = name;
	}



	

	

	@Override
	public String getUnitName()
	{
		return getName();
	}

	@Override
	public String getUnitType()
	{
		return EUnitType.MODEL.toString();
	}

	@Override
	public boolean hasUnitAttribute(String attribute)
	{
		return getUnitAttributes().contains(attribute);
	}



	@Override
	public Collection<String> getUnitAttributes()
	{
		return new HashSet<String>();
	}



	@Override
	public UnitResult getUnitRelation(String argumentName)
	{
		return new UnitResult();
	}



	@Override
	public String toString()
	{
		return name;
	}
}