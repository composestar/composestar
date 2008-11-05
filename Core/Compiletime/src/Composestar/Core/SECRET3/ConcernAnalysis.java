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
import java.util.List;

import Composestar.Core.CpsRepository2.Concern;
import Composestar.Core.CpsRepository2.SIInfo.ImposedFilterModule;
import Composestar.Core.FIRE2.model.FireModel.FilterDirection;

/**
 * Contains filter analysis for a given concern. Depending on the analysis
 * method it can contain multiple filter set analysis.
 */
public class ConcernAnalysis implements Serializable
{
	private static final long serialVersionUID = 1512786473548387129L;

	private SECRETResources resources;

	private Concern concern;

	private List<FilterSetAnalysis> analysis;

	public ConcernAnalysis(Concern inconcern, SECRETResources inresources)
	{
		resources = inresources;
		concern = inconcern;
		analysis = new ArrayList<FilterSetAnalysis>();
	}

	/**
	 * @return the concern associated with this analysis
	 */
	public Concern getConcern()
	{
		return concern;
	}

	/**
	 * @return the list of filter set analysis created for this concern
	 */
	public List<FilterSetAnalysis> getAnalysis()
	{
		return Collections.unmodifiableList(analysis);
	}

	/**
	 * @return true when this concern contains a conflict in any of the filter
	 *         set analysis
	 * @see FilterSetAnalysis#hasConflicts()
	 */
	public boolean hasConflicts()
	{
		for (FilterSetAnalysis fsa : analysis)
		{
			if (fsa.hasConflicts())
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * Return the filter set analysis for the selected filter module ordering
	 * and the given filter direction
	 * 
	 * @param direction
	 * @return
	 */
	public FilterSetAnalysis getSelectedAnalysis(FilterDirection direction)
	{
		for (FilterSetAnalysis fsa : analysis)
		{
			if (fsa.isSelected() && fsa.getFilterDirection() == direction)
			{
				return fsa;
			}
		}
		return null;
	}

	/**
	 * Analyse the given order. Returns true when there are no conflicts
	 * 
	 * @param order the filter module to use during the analysis
	 * @param isSelected is this the selected filter module order
	 * @return true when there was a conflict
	 */
	protected boolean analyseOrder(List<ImposedFilterModule> order, boolean isSelected)
	{
		FilterSetAnalysis oa = new FilterSetAnalysis(concern, order, FilterDirection.Input, isSelected);
		oa.analyze(resources);
		analysis.add(oa);
		return !oa.hasConflicts();
	}
}
