/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2004-2006 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */
package Composestar.Core.SECRET3;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Composestar.Core.CpsRepository2.Concern;
import Composestar.Core.CpsRepository2.QualifiedRepositoryEntity;
import Composestar.Core.CpsRepository2.FilterModules.BinaryFilterOperator;
import Composestar.Core.CpsRepository2.FilterModules.Filter;
import Composestar.Core.CpsRepository2.FilterModules.FilterExpression;
import Composestar.Core.CpsRepository2.FilterModules.FilterModule;
import Composestar.Core.CpsRepository2.SIInfo.ImposedFilterModule;
import Composestar.Core.CpsRepository2.TypeSystem.CpsMessage;
import Composestar.Core.CpsRepository2.TypeSystem.CpsSelector;
import Composestar.Core.FIRE2.model.ExecutionModel;
import Composestar.Core.FIRE2.model.ExecutionTransition;
import Composestar.Core.FIRE2.model.FireModel;
import Composestar.Core.FIRE2.model.FlowNode;
import Composestar.Core.FIRE2.model.FireModel.FilterDirection;
import Composestar.Core.FIRE2.util.regex.AbstractMatcher;
import Composestar.Core.FIRE2.util.regex.Labeler;
import Composestar.Core.FIRE2.util.regex.MatcherEx;
import Composestar.Core.FIRE2.util.regex.AbstractMatcher.MatchTrace;
import Composestar.Core.Master.ModuleNames;
import Composestar.Core.SECRET3.Model.ConflictRule;
import Composestar.Core.SECRET3.Model.Resource;
import Composestar.Utils.Logging.CPSLogger;

/**
 * A filter set analysis for a given concern, filter module ordering and filter
 * direction
 */
public class FilterSetAnalysis implements Serializable
{
	private static final long serialVersionUID = 4029416472112802480L;

	private static final CPSLogger logger = CPSLogger.getCPSLogger(ModuleNames.SECRET);

	/**
	 * The concern who's filters are being analyzed
	 */
	private Concern concern;

	/**
	 * Order used to analyze
	 */
	private List<ImposedFilterModule> order;

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
	private Map<CpsSelector, List<Conflict>> conflicts;

	public FilterSetAnalysis(Concern inconcern, List<ImposedFilterModule> inorder, FilterDirection indir, boolean insel)
	{
		concern = inconcern;
		order = inorder;
		filterDirection = indir;
		isSelectedOrder = insel;

		filters = new ArrayList<Filter>();
		conflicts = new HashMap<CpsSelector, List<Conflict>>();

		getFilterList();
	}

	/**
	 * @see #filters
	 * @return
	 */
	// TODO what is this used for?
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
	public List<Conflict> executionConflicts(CpsSelector selector)
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

	/**
	 * @return true if there is a conflict
	 */
	public boolean hasConflicts()
	{
		return conflicts.size() > 0;
	}

	/**
	 * @return the associated concern
	 */
	public Concern getConcern()
	{
		return concern;
	}

	/**
	 * @see #filterDirection
	 * @return
	 */
	public FilterDirection getFilterDirection()
	{
		return filterDirection;
	}

	/**
	 * @see #isSelectedOrder
	 * @return
	 */
	public boolean isSelected()
	{
		return isSelectedOrder;
	}

	/**
	 * @see #getOrder()
	 * @return
	 */
	public List<ImposedFilterModule> getOrder()
	{
		return order;
	}

	/**
	 * Construct the list of filters in the order that they appear according to
	 * the given ordering.
	 */
	protected void getFilterList()
	{
		for (ImposedFilterModule fmsi : order)
		{
			FilterModule fm = fmsi.getFilterModule();

			if (filterDirection == FilterDirection.OUTPUT)
			{
				getFilterList(fm.getOutputFilterExpression());
			}
			else
			{
				getFilterList(fm.getInputFilterExpression());
			}
		}
	}

	/**
	 * @param fex
	 */
	protected void getFilterList(FilterExpression fex)
	{
		if (fex instanceof Filter)
		{
			filters.add((Filter) fex);
		}
		else if (fex instanceof BinaryFilterOperator)
		{
			getFilterList(((BinaryFilterOperator) fex).getLHS());
			getFilterList(((BinaryFilterOperator) fex).getRHS());
		}
	}

	/**
	 * Analyze the given concern and filter module order
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
				if (rule.getResource().isWildcard() || rule.getResource().equals(resource))
				{
					labeler.setCurrentResource(resource);
					AbstractMatcher matcher = new MatcherEx(rule.getPattern(), execModel, labeler);

					if (matcher.matches())
					{
						for (MatchTrace trace : matcher.matchTraces())
						{
							addConflict(resource, rule, trace);
						}
					}
				}
			}
		}
	}

	/**
	 * Add a detected conflict to this analysis set
	 * 
	 * @param resource the resource that was being inspected
	 * @param rule the rule that was being validated
	 * @param trace the trace leading to the violation
	 */
	protected void addConflict(Resource resource, ConflictRule rule, MatchTrace trace)
	{
		Conflict conflict = new Conflict();
		conflict.setResource(resource);
		conflict.setRule(rule);
		List<ExecutionTransition> trans = trace.getTransition();
		conflict.setTrace(trans);
		conflict.setOperations(trace.getOperations());
		// first state has the entrance message
		CpsMessage entranceMessage = trans.get(0).getStartState().getMessage();
		conflict.setSelector(entranceMessage.getSelector());

		logger.warn(String.format("Resource operation conflict for \"%s.%s\" on the resource \"%s\". ", concern
				.getFullyQualifiedName(), entranceMessage.getSelector(), resource.getName()));
		logger.warn(String.format("Violating operation sequence: %s", conflict.getOperations()));
		QualifiedRepositoryEntity re = null;
		for (ExecutionTransition et : trans)
		{
			FlowNode fn = et.getStartState().getFlowNode();
			if (fn.containsName(FlowNode.FILTER_NODE))
			{
				if (!(fn.getRepositoryLink() instanceof QualifiedRepositoryEntity))
				{
					continue;
				}
				re = (QualifiedRepositoryEntity) fn.getRepositoryLink();
				if (re.getSourceInformation() == null)
				{
					re = null;
					continue;
				}
			}
			else if (fn.containsName(FlowNode.FILTER_ACTION_NODE))
			{
				if (re != null)
				{
					if (!fn.containsName("ContinueAction") && !fn.containsName("SkipAction"))
					{
						logger.warn(re.getFullyQualifiedName(), re);
					}
					re = null;
				}
			}
		}
		logger.warn(String.format("Violation of the conflict rule: %s", rule.toString()));

		List<Conflict> lst = conflicts.get(conflict.getSelector());
		if (lst == null)
		{
			lst = new ArrayList<Conflict>();
			conflicts.put(conflict.getSelector(), lst);
		}
		lst.add(conflict);
	}
}
