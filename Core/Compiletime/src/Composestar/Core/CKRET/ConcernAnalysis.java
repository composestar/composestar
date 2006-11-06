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
 * @author Administrator To change the template for this generated type comment
 *         go to Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and
 *         Comments
 */
public class ConcernAnalysis
{
	private Concern concern;

	private Map orders;

	public ConcernAnalysis(Concern concern)
	{
		this.concern = concern;
		this.orders = new HashMap();
	}

	public Concern getConcern()
	{
		return this.concern;
	}

	protected boolean checkOrder(FilterModuleOrder order, boolean isSelected)
	{
		FilterSetAnalysis oa = new FilterSetAnalysis(this.concern, order);

		oa.analyze();

		this.orders.put(order, oa);

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

		return (oa.numConflictingExecutions() == 0);
	}

}
