/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2007 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */

package Composestar.Core.DIGGER2;

import java.util.List;

import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.ConditionExpression;

/**
 * A special MessageResult that signals a recursive message.
 * 
 * @author Michiel Hendriks
 */
public class RecursiveMessageResult extends AbstractMessageResult
{
	/**
	 * Number of variable conditions encounterd in the trail
	 */
	protected int vars;

	/**
	 * The trace of trails it took to encounter a recursive dispatch
	 */
	protected List<Trail> trace;

	/**
	 * @param inCrumb the start/end of the recursion
	 * @param inTrace the complete trail of the recursion
	 */
	public RecursiveMessageResult(Breadcrumb inCrumb, List<Trail> inTrace)
	{
		crumb = inCrumb;
		trace = inTrace;

		for (Trail trail : trace)
		{
			trail.setRecursive(true);
			int cnd = trail.getCondition().simulateResult();
			if (cnd != ConditionExpression.RESULT_FALSE)
			{
				vars += cnd;
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.DIGGER2.AbstractMessageResult#isValidResult()
	 */
	@Override
	public boolean isValidResult()
	{
		return false;
	}

	/**
	 * Return the trace. This is a list of trails.
	 * 
	 * @return
	 */
	public List<Trail> getTrace()
	{
		return trace;
	}

	/**
	 * Return the number of (estimated) conditions the recursion depends on.
	 * 
	 * @return
	 */
	public int numVars()
	{
		return vars;
	}
}
