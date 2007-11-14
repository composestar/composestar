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
import Composestar.Core.FIRE2.model.ExecutionModel;
import Composestar.Core.FIRE2.model.FireModel;
import Composestar.Core.SANE.FilterModuleSuperImposition;

/**
 *
 */
public class FilterSetAnalysis implements Serializable
{
	private static final long serialVersionUID = -5154995474750593236L;

	private Concern concern;

	private FilterModuleOrder order;

	private List<Filter> filters;

	private List<List<Conflict>> conflictingExecutions;

	public FilterSetAnalysis(Concern inconcern, FilterModuleOrder inorder)
	{
		concern = inconcern;
		order = inorder;
		conflictingExecutions = new ArrayList<List<Conflict>>();
	}

	public List<Filter> getFilters()
	{
		return filters;
	}

	public void analyze(SECRETResources resources)
	{
		filters = getFilterList(order.filterModuleSIList());

		FireModel fireModel = new FireModel(concern, order);

		ExecutionModel execModel = fireModel.getExecutionModel(FireModel.INPUT_FILTERS);

		List<Conflict> conflicts = AbstractVM.analyze(concern, execModel, resources);
		if (!conflicts.isEmpty())
		{
			conflictingExecutions.add(conflicts);
		}
	}

	public int numConflictingExecutions()
	{
		return conflictingExecutions.size();
	}

	public List<List<Conflict>> executionConflicts()
	{
		return conflictingExecutions;
	}

	protected static List<Filter> getFilterList(List<FilterModuleSuperImposition> filterModules)
	{
		List<Filter> list = new ArrayList<Filter>();

		for (FilterModuleSuperImposition fmsi : filterModules)
		{
			// if (!InnerDispatcher.isDefaultDispatch(name))
			{
				FilterModule fm = fmsi.getFilterModule().getRef();
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
