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

import Composestar.Core.CpsRepository2.JoinPointContextArgument;
import Composestar.Core.CpsRepository2.Filters.FilterAction;
import Composestar.Core.CpsRepository2Impl.AbstractRepositoryEntity;

/**
 * Default filter action implementation
 * 
 * @author Michiel Hendriks
 */
public class FilterActionImpl extends AbstractRepositoryEntity implements FilterAction
{
	private static final long serialVersionUID = -7316539530851389792L;

	/**
	 * The name of the filter action
	 */
	protected String name;

	/**
	 * The flow behavior
	 */
	protected FlowBehavior flowBehavior;

	/**
	 * The resource operations
	 */
	protected String resourceOperations;

	/**
	 * The join point context this filter action needs. In case of
	 * {@link JoinPointContextArgument#NONE} and
	 * {@link JoinPointContextArgument#UNUSED} no joint point context is needed.
	 */
	protected JoinPointContextArgument joinPointContextArgument;

	/**
	 * @param actionName
	 * @throws NullPointerException
	 * @throws IllegalArgumentException
	 */
	public FilterActionImpl(String actionName) throws NullPointerException, IllegalArgumentException
	{
		super();
		if (actionName == null)
		{
			throw new NullPointerException("filter action name cannot be null");
		}
		if (actionName.isEmpty())
		{
			throw new IllegalArgumentException("filter action name cannot be empty");
		}
		name = actionName;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.Filters.FilterAction#getFlowBehavior()
	 */
	public FlowBehavior getFlowBehavior()
	{
		return flowBehavior;
	}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Core.CpsRepository2.Filters.FilterAction#getName()
	 */
	public String getName()
	{
		return name;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.Filters.FilterAction#getResourceOperations
	 * ()
	 */
	public String getResourceOperations()
	{
		return resourceOperations;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.Filters.FilterAction#needsJoinPointContext
	 * ()
	 */
	public JoinPointContextArgument needsJoinPointContext()
	{
		return joinPointContextArgument;
	}

}
