/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2004-2006 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */
package Composestar.Core.CKRET;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Filter;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterModule;
import Composestar.Core.FILTH.FilterModuleOrder;
import Composestar.Core.RepositoryImplementation.DataStore;

/**
 *
 */
public class FilterSetAnalysis implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5154995474750593236L;

	private Concern concern;

	private FilterModuleOrder order;

	private List filters;

	private List executions;

	private List conflictingExecutions;

	public FilterSetAnalysis(Concern inconcern, FilterModuleOrder inorder)
	{
		concern = inconcern;
		order = inorder;
		// messages = new HashMap();
		executions = new ArrayList();
		conflictingExecutions = new ArrayList();
	}

	public List getFilters()
	{
		return this.filters;
	}

	public void analyze()
	{
		this.filters = getFilterList(this.order.orderAsList());

		AbstractVM avm = new AbstractVM();
		List conflicts = avm.analyze(concern, this.order);
		if (!conflicts.isEmpty())
		{
			this.conflictingExecutions.add(conflicts);
		}
	}

	public int numConflictingExecutions()
	{
		return this.conflictingExecutions.size();
	}

	public List executionConflicts()
	{
		return this.conflictingExecutions;
	}

	protected static List getFilterList(List filterModules)
	{
		List list = new ArrayList();

		Iterator itr = filterModules.iterator();
		for (Object filterModule : filterModules)
		{
			String name = (String) filterModule;
			// if(
			// !(name.equals("CpsDefaultInnerDispatchConcern.CpsDefaultInnerDispatchFilterModule")))
			{
				FilterModule fm = (FilterModule) (DataStore.instance()).getObjectByID(name);
				Iterator ifItr = fm.getInputFilterIterator();

				while (ifItr.hasNext())
				{
					Filter f = (Filter) ifItr.next();
					list.add(f);
				}
			}
		}
		return list;
	}

	public Concern getConcern()
	{
		return this.concern;
	}
}
