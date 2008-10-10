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

package Composestar.Core.CpsRepository2Impl.Filters;

import Composestar.Core.CpsRepository2.Filters.FilterAction;
import Composestar.Core.CpsRepository2.Filters.PrimitiveFilterType;
import Composestar.Core.CpsRepository2Impl.AbstractRepositoryEntity;

/**
 * Basic implementation of a primitive filter type
 * 
 * @author Michiel Hendriks
 */
public class PrimitiveFilterTypeImpl extends AbstractRepositoryEntity implements PrimitiveFilterType
{
	private static final long serialVersionUID = 7080307948241253239L;

	/**
	 * The name of the filter
	 */
	protected String filterName;

	/**
	 * Action to execute when the message accepts on the call
	 */
	protected FilterAction acceptCallAction;

	/**
	 * Action to execute when the message accepts on the return
	 */
	protected FilterAction acceptReturnAction;

	/**
	 * Action to execute when the message rejects on the call
	 */
	protected FilterAction rejectCallAction;

	/**
	 * Action to execute when the message rejects on the return
	 */
	protected FilterAction rejectReturnAction;

	/**
	 * @param name The name of this filter
	 * @throws NullPointerException Thrown when the name is null
	 * @throws IllegalArgumentException Thrown when the name is empty
	 */
	public PrimitiveFilterTypeImpl(String name) throws NullPointerException, IllegalArgumentException
	{
		super();
		if (name == null)
		{
			throw new NullPointerException("filter name cannot be null");
		}
		if (name.isEmpty())
		{
			throw new IllegalArgumentException("filter name cannot be empty");
		}
		filterName = name;
	}

	/*
	 * (non-Javadoc)
	 * @seeComposestar.Core.CpsRepository2.Filters.PrimitiveFilterType#
	 * getAcceptCallAction()
	 */
	public FilterAction getAcceptCallAction()
	{
		return acceptCallAction;
	}

	/**
	 * @param value the new filter action
	 * @throws NullPointerException thrown when the filter action is null
	 */
	public void setAcceptCallAction(FilterAction value) throws NullPointerException
	{
		if (value == null)
		{
			throw new NullPointerException();
		}
		acceptCallAction = value;
	}

	/*
	 * (non-Javadoc)
	 * @seeComposestar.Core.CpsRepository2.Filters.PrimitiveFilterType#
	 * getAcceptReturnAction()
	 */
	public FilterAction getAcceptReturnAction()
	{
		return acceptReturnAction;
	}

	/**
	 * @param value the new filter action
	 * @throws NullPointerException thrown when the filter action is null
	 */
	public void setAcceptReturnAction(FilterAction value) throws NullPointerException
	{
		if (value == null)
		{
			throw new NullPointerException();
		}
		acceptReturnAction = value;
	}

	/*
	 * (non-Javadoc)
	 * @seeComposestar.Core.CpsRepository2.Filters.PrimitiveFilterType#
	 * getRejectCallAction()
	 */
	public FilterAction getRejectCallAction()
	{
		return rejectCallAction;
	}

	/**
	 * @param value the new filter action
	 * @throws NullPointerException thrown when the filter action is null
	 */
	public void setRejectCallAction(FilterAction value) throws NullPointerException
	{
		if (value == null)
		{
			throw new NullPointerException();
		}
		rejectCallAction = value;
	}

	/*
	 * (non-Javadoc)
	 * @seeComposestar.Core.CpsRepository2.Filters.PrimitiveFilterType#
	 * getRejectReturnAction()
	 */
	public FilterAction getRejectReturnAction()
	{
		return rejectReturnAction;
	}

	/**
	 * @param value the new filter action
	 * @throws NullPointerException thrown when the filter action is null
	 */
	public void setRejectReturnAction(FilterAction value) throws NullPointerException
	{
		if (value == null)
		{
			throw new NullPointerException();
		}
		rejectReturnAction = value;
	}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Core.CpsRepository2.Filters.FilterType#getFilterName()
	 */
	public String getFilterName()
	{
		return filterName;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return getFilterName() + " [" + super.toString() + "]";
	}

}
