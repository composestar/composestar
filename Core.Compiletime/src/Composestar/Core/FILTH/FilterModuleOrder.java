/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003-2006 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */
package Composestar.Core.FILTH;

/**
 * @author Isti
 */

import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.SANE.FilterModuleSuperImposition;

public class FilterModuleOrder extends Composestar.Core.RepositoryImplementation.ContextRepositoryEntity
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4642012441159613281L;

	/**
	 * The key used to store this variable in the concern's dynamicmap
	 */
	public static final String SINGLE_ORDER_KEY = "SingleOrder";

	/**
	 * Key used to store all filter module orders
	 */
	public static final String ALL_ORDERS_KEY = "FilterModuleOrders";

	public Vector order;

	public Vector orderQN;

	public FilterModuleOrder()
	{
		order = new Vector();
		orderQN = new Vector();

	}

	public FilterModuleOrder(Concern parent)
	{
		super(parent);

		order = new Vector();
		orderQN = new Vector();
	}

	public FilterModuleOrder(LinkedList inOrder)
	{
		order = new Vector();
		orderQN = new Vector();

		int size = inOrder.size();
		for (int i = 0; i < size; i++)
		{
			FilterModuleSuperImposition fmsi = (FilterModuleSuperImposition) inOrder.get(i);
			order.add(fmsi);
			orderQN.addElement(fmsi.getFilterModule().getRef().getQualifiedName());
		}
	}

	public void addFilterModule(FilterModuleSuperImposition fmsi)
	{
		order.add(fmsi);
		orderQN.add(fmsi.getFilterModule().getRef().getQualifiedName());
	}

	/**
	 * Returns a list of the filter modules in this FilterModuleOrder as a list
	 * of qualified names.
	 * 
	 * @deprecated The method <code>filterModuleSIList()</code> should be
	 *             used, which returns a list of FilterModuleSuperImpositions.
	 */
	public List orderAsList()
	{
		return orderQN;
	}

	/**
	 * Returns the list of FilterModuleSuperImpositions in this
	 * FilterModuleOrder.
	 * 
	 * @return
	 */
	public List filterModuleSIList()
	{
		return order;
	}

	public int hashCode()
	{
		return super.hashCode();
	}

	public boolean equals(Object o)
	{
		if (!(o instanceof FilterModuleOrder))
		{
			return false;
		}

		FilterModuleOrder other = (FilterModuleOrder) o;
		return order.equals(other.order);
	}

	public String toString()
	{
		StringBuffer retval = new StringBuffer();
		List modules = orderAsList();
		for (int i = 0; i < modules.size() - 1; i++)
		{
			retval.append((String) modules.get(i));
			if (i < modules.size() - 2)
			{
				retval.append(" + ");
			}
		}
		return retval.toString();
	}
}