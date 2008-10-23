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

package Composestar.Core.CpsRepository2Impl.TypeSystem;

import Composestar.Core.CpsRepository2.TypeSystem.CpsSelector;
import Composestar.Core.CpsRepository2Impl.AbstractRepositoryEntity;
import Composestar.Core.LAMA.MethodInfo;

/**
 * CpsSelector implementation used a correct CpsProgramElement is converted
 * 
 * @author Michiel Hendriks
 */
public class CpsSelectorMethodInfo extends AbstractRepositoryEntity implements CpsSelector
{
	private static final long serialVersionUID = 8530247021951450601L;

	/**
	 * The associated method info
	 */
	protected MethodInfo mi;

	public CpsSelectorMethodInfo(MethodInfo methodInfo) throws NullPointerException
	{
		super();
		mi = methodInfo;
	}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Core.CpsRepository2.TypeSystem.CpsSelector#getName()
	 */
	public String getName()
	{
		return mi.getName();
	}

	/**
	 * @return The associated method info
	 */
	public MethodInfo getMethodInfo()
	{
		return mi;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return mi.toString();
	}
}
