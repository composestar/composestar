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

package Composestar.Core.CpsRepository2Impl.SIInfo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import Composestar.Core.CpsRepository2.SIInfo.ImposedFilterModule;
import Composestar.Core.CpsRepository2.SIInfo.Superimposed;
import Composestar.Core.CpsRepository2Impl.AbstractRepositoryEntity;

/**
 * @author Michiel Hendriks
 */
public class SuperimposedImpl extends AbstractRepositoryEntity implements Superimposed
{
	private static final long serialVersionUID = -1502207697152585068L;

	/**
	 * All imposed filter modules, in any order
	 */
	protected List<ImposedFilterModule> imposedFilterModules;

	/**
	 * The selected order
	 */
	protected List<ImposedFilterModule> selectedOrder;

	/**
	 * All known orders
	 */
	protected Collection<List<ImposedFilterModule>> allOrders;

	/**
	 *  
	 */
	public SuperimposedImpl()
	{
		super();
		imposedFilterModules = new ArrayList<ImposedFilterModule>();
		allOrders = new HashSet<List<ImposedFilterModule>>();
	}

	/*
	 * (non-Javadoc)
	 * @seeComposestar.Core.CpsRepository2.SIInfo.Superimposed#addFilterModule(
	 * Composestar.Core.CpsRepository2.SIInfo.ImposedFilterModule)
	 */
	public void addFilterModule(ImposedFilterModule ifm) throws NullPointerException
	{
		if (ifm == null)
		{
			throw new NullPointerException("Imposed filter module can not be null");
		}
		imposedFilterModules.add(ifm);
		ifm.setOwner(this);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.SIInfo.Superimposed#getFilterModules()
	 */
	public List<ImposedFilterModule> getFilterModules()
	{
		return Collections.unmodifiableList(imposedFilterModules);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.SIInfo.Superimposed#addFilterModuleOrder
	 * (java.util.List)
	 */
	public void addFilterModuleOrder(List<ImposedFilterModule> order) throws NullPointerException,
			IllegalArgumentException
	{
		if (order == null)
		{
			throw new NullPointerException("Order can not be null");
		}
		if (!imposedFilterModules.containsAll(order) || !order.contains(imposedFilterModules))
		{
			throw new IllegalArgumentException("Order does not contain the same set of imposed filter modules");
		}
		if (!allOrders.contains(order))
		{
			allOrders.add(order);
			if (selectedOrder == null)
			{
				selectedOrder = order;
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Core.CpsRepository2.SIInfo.Superimposed#getAllOrders()
	 */
	public Collection<List<ImposedFilterModule>> getAllOrders()
	{
		return Collections.unmodifiableCollection(allOrders);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.CpsRepository2.SIInfo.Superimposed#getFilterModuleOrder
	 * ()
	 */
	public List<ImposedFilterModule> getFilterModuleOrder()
	{
		return selectedOrder;
	}

	public void setFilterModuleOrder(List<ImposedFilterModule> order) throws NullPointerException,
			IllegalArgumentException
	{
		if (order == null)
		{
			throw new NullPointerException("Order can not be null");
		}
		if (!imposedFilterModules.containsAll(order) || !order.contains(imposedFilterModules))
		{
			throw new IllegalArgumentException("Order does not contain the same set of imposed filter modules");
		}
		if (!allOrders.contains(order))
		{
			allOrders.add(order);
		}
		selectedOrder = order;
	}
}
