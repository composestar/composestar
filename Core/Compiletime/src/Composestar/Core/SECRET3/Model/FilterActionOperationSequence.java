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

import Composestar.Core.CpsRepository2.Filters.FilterAction;

/**
 * Operation sequence attached to a filter action
 * 
 * @author Michiel Hendriks
 */
public class FilterActionOperationSequence extends OperationSequence
{
	protected FilterAction filterAction;

	public FilterActionOperationSequence(FilterAction action)
	{
		super();
		if (action == null)
		{
			throw new NullPointerException("Filter action cannot be null");
		}
		filterAction = action;
	}

	/**
	 * @return the action
	 */
	public FilterAction getFilterAction()
	{
		return filterAction;
	}
}
