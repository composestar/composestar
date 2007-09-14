/*
 * This file is part of the Compose* project.
 * http://composestar.sourceforge.net
 * Copyright (C) 2007 University of Twente.
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

package Composestar.Core.Config;

import java.io.Serializable;

public class FilterAction implements Serializable
{
	private static final long serialVersionUID = 7354266551225465392L;

	/**
	 * The basic name of the filter action. Used in the filter type
	 * specification.
	 */
	protected String name;

	/**
	 * The fully qualified name of the filter.
	 */
	protected String fullName;

	/**
	 * The library that contains the implementation of this filter action.
	 */
	protected String library;

	/**
	 * Create a Joint Point Context. This might not always be set.
	 */
	protected boolean createJpc;

	/**
	 * The flowbehavior of the filter action.
	 */
	protected int flowBehavior;

	/**
	 * The message change behavior.
	 */
	protected int messageChangeBehavior;

	public FilterAction()
	{}
}
