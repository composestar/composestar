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

package Composestar.Core.CpsRepository2Impl.FilterElements;

/**
 *
 * @author arjan
 */
public class ValueMatching extends AbstractMECmpStmt
{
	private int compType;
	
	public final static int GEQ = 1;
	public final static int LEQ = 2;
	public final static int GT = 3;
	public final static int LT = 4;

	/**
	 * 
	 */
	private static final long serialVersionUID = 6374956619785086468L;

	/**
	 * 
	 */
	public ValueMatching(int compType)
	{
		super();
		this.compType = compType;
	}

	public int getCompType()
	{
		return compType;
	}
	
	
}
