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

import java.util.HashMap;
import java.util.Map;

import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.FILTH.FilterModuleOrder;

/**
 * 
 */
public class ConcernAnalysis
{
	private SECRETResources resources;

	private Concern concern;

	private Map<FilterModuleOrder, FilterSetAnalysis> orders;

	public ConcernAnalysis(Concern inconcern, SECRETResources inresources)
	{
		resources = inresources;
		concern = inconcern;
		orders = new HashMap<FilterModuleOrder, FilterSetAnalysis>();
	}

	public Concern getConcern()
	{
		return concern;
	}

	protected boolean checkOrder(FilterModuleOrder order, boolean isSelected)
	{
		FilterSetAnalysis oa = new FilterSetAnalysis(concern, order);

		oa.analyze(resources);

		orders.put(order, oa);

		switch (CKRET.getMode())
		{
			case Normal:
				CKRET.getReporter().reportOrder(order, oa, isSelected, false);
				break;
			case Redundant:
				CKRET.getReporter().reportOrder(order, oa, isSelected, false);
				break;

			case Progressive:
				if (oa.numConflictingExecutions() == 0)
				{
					CKRET.getReporter().reportOrder(order, oa, isSelected, false);
				}
				else
				{
					CKRET.getReporter().reportOrder(order, oa, false, false);
				}
				break;
		}

		return oa.numConflictingExecutions() == 0;
	}

}
