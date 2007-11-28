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
import java.util.List;

import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.FILTH.FilterModuleOrder;
import Composestar.Core.FIRE2.model.FireModel.FilterDirection;

/**
 * Contains filter analysis for a given concern. Depending on the analysis
 * method it can contain multiple filter set analysis.
 */
public class ConcernAnalysis
{
	private SECRETResources resources;

	private Concern concern;

	private List<FilterSetAnalysis> analysis;

	public ConcernAnalysis(Concern inconcern, SECRETResources inresources)
	{
		resources = inresources;
		concern = inconcern;
		analysis = new ArrayList<FilterSetAnalysis>();
	}

	public Concern getConcern()
	{
		return concern;
	}

	public List<FilterSetAnalysis> getAnalysis()
	{
		return Collections.unmodifiableList(analysis);
	}

	/**
	 * Analyse the given order. Returns true when it contains conflicts.
	 * 
	 * @param order
	 * @param isSelected
	 * @return
	 */
	protected boolean analyseOrder(FilterModuleOrder order, boolean isSelected)
	{
		FilterSetAnalysis oa = new FilterSetAnalysis(concern, order, FilterDirection.Input, isSelected);
		oa.analyze(resources);
		analysis.add(oa);
		return oa.hasConflicts();
	}
}
