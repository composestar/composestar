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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import Composestar.Core.CKRET.Config.ConflictRule;
import Composestar.Core.CKRET.Config.Resource;
import Composestar.Core.CKRET.Config.ResourceType;
import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Filter;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterModule;
import Composestar.Core.FILTH.FilterModuleOrder;
import Composestar.Core.FIRE2.model.ExecutionModel;
import Composestar.Core.FIRE2.model.FireModel;
import Composestar.Core.FIRE2.model.FireModel.FilterDirection;
import Composestar.Core.FIRE2.util.regex.Labeler;
import Composestar.Core.FIRE2.util.regex.Matcher;
import Composestar.Core.SANE.FilterModuleSuperImposition;

/**
 * A filter set analysis
 */
public class FilterSetAnalysis
{
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
	private List<Conflict> conflicts;

	public FilterSetAnalysis(Concern inconcern, FilterModuleOrder inorder, FilterDirection indir, boolean insel)
	{
		concern = inconcern;
		order = inorder;
		filterDirection = indir;
		isSelectedOrder = insel;

		filters = new ArrayList<Filter>();
		conflicts = new ArrayList<Conflict>();

		getFilterList();
	}

	public List<Filter> getFilters()
	{
		return Collections.unmodifiableList(filters);
	}

	public List<Conflict> executionConflicts()
	{
		return Collections.unmodifiableList(conflicts);
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
						Conflict conflict = new Conflict();
						conflict.setResource(resource);
						conflict.setRule(rule);
						// conflict.setSequence(matcher.matchTrace());
						conflicts.add(conflict);
					}
				}
			}
		}
	}
}
