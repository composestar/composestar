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
 * Exception thrown in case of a recursive filter definition.
 * 
 * @author Michiel Hendriks
 */
public class RecursiveFilterException extends Exception
{
	protected int vars;

	protected Breadcrumb crumb;

	protected List<Trail> trace;

	/**
	 * Unconditional recursive filter definition.
	 * 
	 * @param inMessage
	 * @param inTrace
	 */
	public RecursiveFilterException(Breadcrumb inCrumb, List<Trail> inTrace)
	{
		this("Recursive filter definition", inCrumb, inTrace);
	}

	/**
	 * Unconditional recursive filter definition.
	 * 
	 * @param inMessage
	 * @param inTrace
	 */
	public RecursiveFilterException(String inMessage, Breadcrumb inCrumb, List<Trail> inTrace)
	{
		super(inMessage);
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

	/**
	 * Returns the originating breadcrumb
	 * 
	 * @return
	 */
	public Breadcrumb getCrumb()
	{
		return crumb;
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
