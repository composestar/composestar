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
import Composestar.Core.FILTH.InnerDispatcher;
import Composestar.Core.FIRE2.model.ExecutionModel;
import Composestar.Core.FIRE2.model.FireModel;
import Composestar.Core.RepositoryImplementation.DataStore;

/**
 *
 */
public class FilterSetAnalysis implements Serializable
{
	private static final long serialVersionUID = -5154995474750593236L;

	private Concern concern;

	private FilterModuleOrder order;

	private List<Filter> filters;

	private List executions;

	private List<List> conflictingExecutions;

	public FilterSetAnalysis(Concern inconcern, FilterModuleOrder inorder)
	{
		concern = inconcern;
		order = inorder;
		executions = new ArrayList();
		conflictingExecutions = new ArrayList<List>();
	}

	public List<Filter> getFilters()
	{
		return filters;
	}

	public void analyze()
	{
		filters = getFilterList(order.orderAsList());

		FireModel fireModel = new FireModel(concern, order);

		ExecutionModel execModel = fireModel.getExecutionModel(FireModel.INPUT_FILTERS);
		
		AbstractVM avm = new AbstractVM();
		List conflicts = avm.analyze(concern, execModel);
		if (!conflicts.isEmpty())
		{
			conflictingExecutions.add(conflicts);
		}
	}

	public int numConflictingExecutions()
	{
		return conflictingExecutions.size();
	}

	public List executionConflicts()
	{
		return conflictingExecutions;
	}

	protected static List<Filter> getFilterList(List filterModules)
	{
		List<Filter> list = new ArrayList<Filter>();

		for (Object filterModule : filterModules)
		{
			String name = (String) filterModule;
			//if (!InnerDispatcher.isDefaultDispatch(name))
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
		return concern;
	}
}
