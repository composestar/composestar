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
	private Concern concern;

	private Map<FilterModuleOrder, FilterSetAnalysis> orders;

	public ConcernAnalysis(Concern inconcern)
	{
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

		oa.analyze();

		orders.put(order, oa);

		switch (CKRET.getMode())
		{
			case CKRET.NORMAL:
				CKRET.getReporter().reportOrder(order, oa, isSelected, false);
				break;
			case CKRET.REDUNDANT:
				CKRET.getReporter().reportOrder(order, oa, isSelected, false);
				break;

			case CKRET.PROGRESSIVE:
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
