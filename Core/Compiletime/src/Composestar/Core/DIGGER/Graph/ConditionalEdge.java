/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2006 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id: ConditionalEdge.java,v 1.4 2006/10/04 11:49:02 elmuerte Exp $
 */
package Composestar.Core.DIGGER.Graph;

import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.ConditionExpression;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.*;

/**
 * An edge with an condition expression
 * 
 * @author Michiel Hendriks
 */
public class ConditionalEdge extends Edge
{
	protected ConditionExpression expression;

	/**
	 * @param inDestination
     * @param inExpression
	 */
	public ConditionalEdge(Node inDestination, ConditionExpression inExpression)
	{
		super(inDestination);
		expression = inExpression;
	}

	public ConditionExpression getExpression()
	{
		return expression;
	}

	public String getExpressionAsString()
	{
		return getExpressionAsString(expression);
	}

	protected String getExpressionAsString(ConditionExpression expr)
	{
		if (expr instanceof True)
		{
			return "True";
		}
		if (expr instanceof False)
		{
			return "False";
		}
		if (expr instanceof And)
		{
			return "(" + getExpressionAsString(((And) expr).getLeft()) + " & "
					+ getExpressionAsString(((And) expr).getRight()) + ")";
		}
		if (expr instanceof Or)
		{
			return "(" + getExpressionAsString(((Or) expr).getLeft()) + "|"
					+ getExpressionAsString(((Or) expr).getRight()) + ")";
		}
		if (expr instanceof Not)
		{
			return "!(" + getExpressionAsString(((Not) expr).getOperand()) + ")";
		}
		if (expr instanceof ConditionVariable)
		{
			return ((ConditionVariable) expr).getCondition().getName();
		}
		return "?";
	}

}