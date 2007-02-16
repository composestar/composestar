/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2006 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */

package Composestar.Core.DIGGER.Graph;

import java.util.Iterator;
import java.util.List;

import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.And;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.ConditionExpression;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.ConditionVariable;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.EnableOperator;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.False;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterElement;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.MatchingPart;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.NameMatchingType;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Not;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Or;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.SignatureMatchingType;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.True;

/**
 * An edge used by filters that contain both a condition and matchingpart. A
 * filter element always contains a single MatchingPattern.
 * 
 * @author Michiel Hendriks
 */
public class CondMatchEdge extends Edge
{
	/**
	 * The FilterElement condition
	 */
	protected ConditionExpression expression;

	/**
	 * If true the matching parts are enablers otherwise they are disablers.
	 */
	protected boolean enabler;

	/**
	 * The MatchingParts in the MatchingPattern
	 */
	protected List matchingParts;

	/**
	 * If true than the matchingParts are part of a message list
	 */
	protected boolean isMessageList;

	/**
	 * 
	 */
	public CondMatchEdge(Node inDestination, FilterElement fe)
	{
		super(inDestination);
		expression = fe.getConditionPart();
		enabler = fe.getEnableOperatorType() instanceof EnableOperator;
		matchingParts = fe.getMatchingPattern().getMatchingParts();
		isMessageList = fe.getMatchingPattern().getIsMessageList();
	}

	public ConditionExpression getExpression()
	{
		return expression;
	}

	public Iterator getMatchingParts()
	{
		return matchingParts.iterator();
	}

	public boolean getEnabler()
	{
		return enabler;
	}

	public boolean getIsMessageList()
	{
		return isMessageList;
	}

	/**
	 * Return a string representation of this edge
	 * 
	 * @return
	 */
	public String asString()
	{
		String operator;
		if (enabler)
		{
			operator = " => ";
		}
		else
		{
			operator = " ~> ";
		}
		return getExpressionAsString(expression) + operator + getMatchingPartsAsString();
	}

	public String getConditionAsString()
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
			return "(" + getExpressionAsString(((Or) expr).getLeft()) + " | "
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

	public String getMatchingPartsAsString()
	{
		// yes, it's not great code
		StringBuffer sb = new StringBuffer();
		if (isMessageList)
		{
			sb.append("#(");
		}
		else if (matchingParts.size() > 1)
		{
			sb.append("{");
		}
		Iterator it = matchingParts.iterator();
		int cnt = 0;
		while (it.hasNext())
		{
			cnt++;
			MatchingPart mp = (MatchingPart) it.next();
			if (cnt > 1)
			{
				if (isMessageList)
				{
					sb.append("; ");
				}
				else
				{
					sb.append(", ");
				}
			}
			if (mp.getMatchType() instanceof NameMatchingType)
			{
				sb.append("[");
			}
			else if (mp.getMatchType() instanceof SignatureMatchingType)
			{
				sb.append("<");
			}
			sb.append(mp.getTarget().getName());
			sb.append(".");
			sb.append(mp.getSelector().getName());
			if (mp.getMatchType() instanceof NameMatchingType)
			{
				sb.append("]");
			}
			else if (mp.getMatchType() instanceof SignatureMatchingType)
			{
				sb.append(">");
			}
		}
		if (isMessageList)
		{
			sb.append("}");
		}
		else if (matchingParts.size() > 1)
		{
			sb.append("}");
		}
		return sb.toString();
	}
}
