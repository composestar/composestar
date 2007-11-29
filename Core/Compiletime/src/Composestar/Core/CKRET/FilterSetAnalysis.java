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
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import Composestar.Core.CKRET.Config.ConflictRule;
import Composestar.Core.CKRET.Config.Resource;
import Composestar.Core.CKRET.Config.ResourceType;
import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Filter;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterModule;
import Composestar.Core.FILTH.FilterModuleOrder;
import Composestar.Core.FIRE2.model.ExecutionModel;
import Composestar.Core.FIRE2.model.ExecutionTransition;
import Composestar.Core.FIRE2.model.FireModel;
import Composestar.Core.FIRE2.model.FlowNode;
import Composestar.Core.FIRE2.model.Message;
import Composestar.Core.FIRE2.model.FireModel.FilterDirection;
import Composestar.Core.FIRE2.util.regex.Labeler;
import Composestar.Core.FIRE2.util.regex.Matcher;
import Composestar.Core.RepositoryImplementation.RepositoryEntity;
import Composestar.Core.SANE.FilterModuleSuperImposition;
import Composestar.Utils.Logging.CPSLogger;

/**
 * A filter set analysis
 */
public class FilterSetAnalysis implements Serializable
{
	private static final long serialVersionUID = 4029416472112802480L;

	private static final CPSLogger logger = CPSLogger.getCPSLogger(CKRET.MODULE_NAME);

	/**
	 * The concern who's filters are being analysed
	 */
	private Concern concern;

	/**
	 * Order used to analyse
	 */
	private FilterModuleOrder order;

	/**
	 * Is this the selected order
	 */
	private boolean isSelectedOrder;

	/**
	 * input or output filters
	 */
	private FilterDirection filterDirection;

	/**
	 * The list of filters
	 */
	private List<Filter> filters;

	/**
	 * A list of conflicts found
	 */
	private Map<String, List<Conflict>> conflicts;

	public FilterSetAnalysis(Concern inconcern, FilterModuleOrder inorder, FilterDirection indir, boolean insel)
	{
		concern = inconcern;
		order = inorder;
		filterDirection = indir;
		isSelectedOrder = insel;

		filters = new ArrayList<Filter>();
		conflicts = new HashMap<String, List<Conflict>>();

		getFilterList();
	}

	public List<Filter> getFilters()
	{
		return Collections.unmodifiableList(filters);
	}

	/**
	 * Return detected conflicts for a given selector
	 * 
	 * @param selector
	 * @return
	 */
	public List<Conflict> executionConflicts(String selector)
	{
		if (!conflicts.containsKey(selector))
		{
			return null;
		}
		return Collections.unmodifiableList(conflicts.get(selector));
	}

	/**
	 * Return all conflicts
	 * 
	 * @return
	 */
	public List<Conflict> executionConflicts()
	{
		List<Conflict> result = new ArrayList<Conflict>();
		for (List<Conflict> lst : conflicts.values())
		{
			result.addAll(lst);
		}
		return result;
	}

	public boolean hasConflicts()
	{
		return conflicts.size() > 0;
	}

	public Concern getConcern()
	{
		return concern;
	}

	public FilterDirection getFilterDirection()
	{
		return filterDirection;
	}

	public boolean isSelected()
	{
		return isSelectedOrder;
	}

	@SuppressWarnings("unchecked")
	protected void getFilterList()
	{
		for (FilterModuleSuperImposition fmsi : (List<FilterModuleSuperImposition>) order.filterModuleSIList())
		{
			FilterModule fm = fmsi.getFilterModule().getRef();
			Iterator ifItr;

			if (filterDirection == FilterDirection.Output)
			{
				ifItr = fm.getOutputFilterIterator();
			}
			else
			{
				ifItr = fm.getInputFilterIterator();
			}

			while (ifItr.hasNext())
			{
				Filter f = (Filter) ifItr.next();
				filters.add(f);
			}
		}
	}

	/**
	 * Analyse the given concern and filter module order
	 * 
	 * @param resources
	 */
	public void analyze(SECRETResources resources)
	{
		conflicts.clear();
		FireModel fileModel = resources.getFIRE2Resources().getFireModel(concern, order);
		ExecutionModel execModel = fileModel.getExecutionModel(filterDirection);

		Labeler labeler = resources.getLabeler();
		labeler.setCurrentConcern(concern);

		for (ConflictRule rule : resources.getRules())
		{
			for (Resource resource : resources.getResources())
			{
				if (rule.getResource().getType() == ResourceType.Wildcard || rule.getResource().equals(resource))
				{
					labeler.setCurrentResource(resource);
					Matcher matcher = new Matcher(rule.getPattern(), execModel, labeler);

					if (matcher.matches())
					{
						for (List<ExecutionTransition> trace : matcher.matchTraces())
						{
							if (trace.isEmpty())
							{
								// should never happen
								continue;
							}
							addConflict(resource, rule, trace);
						}
					}
				}
			}
		}
	}

	protected void addConflict(Resource resource, ConflictRule rule, List<ExecutionTransition> trace)
	{
		Conflict conflict = new Conflict();
		conflict.setResource(resource);
		conflict.setRule(rule);
		conflict.setTrace(trace);
		// first state has the entrance message
		Message entranceMessage = trace.get(0).getStartState().getMessage();
		conflict.setSelector(entranceMessage.getSelector());

		logger.info("Conflict trace begin ---");
		logger.info(String.format("For %s.%s", concern.getQualifiedName(), entranceMessage.getSelector()));
		for (ExecutionTransition et : trace)
		{
			FlowNode fn = et.getStartState().getFlowNode();
			if (fn.containsName(FlowNode.FILTER_NODE))
			{
				RepositoryEntity re = fn.getRepositoryLink();
				logger.info(re.getRepositoryKey(), re);
			}
		}
		logger.info("--- Conflict trace end");

		List<Conflict> lst = conflicts.get(conflict.getSelector());
		if (lst == null)
		{
			lst = new ArrayList<Conflict>();
			conflicts.put(conflict.getSelector(), lst);
		}
		lst.add(conflict);
	}
}
