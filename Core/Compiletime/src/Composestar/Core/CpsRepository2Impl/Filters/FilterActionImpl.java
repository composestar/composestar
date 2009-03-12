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
	 * The system name
	 */
	protected String systemName;

	/**
	 * The flow behavior
	 */
	protected FlowBehavior flowBehavior = FlowBehavior.CONTINUE;

	/**
	 * The resource operations
	 */
	protected String resourceOperations = "";

	/**
	 * The join point context this filter action needs. In case of
	 * {@link JoinPointContextArgument#NONE} and
	 * {@link JoinPointContextArgument#UNUSED} no joint point context is needed.
	 */
	protected JoinPointContextArgument joinPointContextArgument = JoinPointContextArgument.UNUSED;

	/**
	 * Create a filter action with a set system name
	 * 
	 * @param actionName
	 * @param sysName
	 * @throws NullPointerException Throw when the action name is null
	 * @throws IllegalArgumentException
	 */
	public FilterActionImpl(String actionName, String sysName) throws NullPointerException, IllegalArgumentException
	{
		super();
		if (actionName == null)
		{
			throw new NullPointerException("filter action name cannot be null");
		}
		if (actionName.length() == 0)
		{
			throw new IllegalArgumentException("filter action name cannot be empty");
		}
		name = actionName;
		systemName = sysName;
	}

	/**
	 * @param actionName
	 * @throws NullPointerException
	 * @throws IllegalArgumentException
	 */
	public FilterActionImpl(String actionName) throws NullPointerException, IllegalArgumentException
	{
		this(actionName, null);
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

	/**
	 * Set the flow behavior
	 * 
	 * @param value
	 */
	public void setFlowBehavior(FlowBehavior value)
	{
		flowBehavior = value;
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
	 * @see Composestar.Core.CpsRepository2.Filters.FilterAction#getSystemName()
	 */
	public String getSystemName()
	{
		return systemName;
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

	/**
	 * Set the resource operation sequence
	 * 
	 * @param value
	 */
	public void setResourceOperations(String value)
	{
		if (value == null)
		{
			value = "";
		}
		resourceOperations = value;
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

	/**
	 * Set the join point context value
	 * 
	 * @param value
	 */
	public void setJoinPointContextArgument(JoinPointContextArgument value)
	{
		joinPointContextArgument = value;
	}
}
