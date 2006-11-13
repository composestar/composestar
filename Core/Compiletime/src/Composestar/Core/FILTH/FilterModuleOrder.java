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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.CpsProgramRepository.CpsConcern.References.FilterModuleReference;

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

	public FilterModuleOrder()
	{
		order = new Vector();

	}

	public FilterModuleOrder(Concern parent)
	{
		super(parent);
	}

	public FilterModuleOrder(LinkedList inOrder)
	{
		order = new Vector();
		int size = inOrder.size();
		for (int i = 0; i < size; i++)
		{
			FilterModuleReference fmr = (FilterModuleReference) inOrder.get(i);
			order.addElement(fmr.getRef().getQualifiedName());
			// _order.addElement(order.get(i));
		}
	}

	public void addFilterModule(FilterModuleReference fmr)
	{
		order.addElement(fmr.getQualifiedName());
	}

	public List orderAsList()
	{
		return order;
	}

	public Iterator order()
	{
		return order.iterator();
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
		String retval = "";
		List modules = orderAsList();
		for (int i = 0; i < modules.size() - 1; i++)
		{
			retval += (String) modules.get(i);
			if (i < modules.size() - 2)
			{
				retval += " + ";
			}
		}
		return retval;
	}
}
