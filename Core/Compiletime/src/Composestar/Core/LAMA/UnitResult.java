/*
 * This file is part of the Compose* project.
 * http://composestar.sourceforge.net
 * Copyright (C) 2006-2008 University of Twente.
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
package Composestar.Core.LAMA;

import java.util.Set;

/**
 * @author havingaw
 */
public class UnitResult
{
	private Set<? extends ProgramElement> multiRes;

	private ProgramElement singleRes;

	public UnitResult()
	{ // Has to exist for .NET serialization
	}

	public UnitResult(ProgramElement single)
	{
		singleRes = single;
		multiRes = null;
	}

	public UnitResult(Set<? extends ProgramElement> multi)
	{
		multiRes = multi;
		singleRes = null;
	}

	/*
	 * @return a single program element, or null if the relation is not unique
	 */

	public ProgramElement singleValue()
	{
		return singleRes;
	}

	/*
	 * @return a hashset containing program elements, or null if the relation is
	 * unique
	 */
	public Set<? extends ProgramElement> multiValue()
	{
		return multiRes;
	}

	public boolean isSingleValue()
	{
		return singleRes != null;
	}

	public boolean isMultiValue()
	{
		return multiRes != null;
	}
}
