/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */
package Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules;

import java.util.ArrayList;
import java.util.Iterator;

import Composestar.Core.RepositoryImplementation.ContextRepositoryEntity;
import Composestar.Core.RepositoryImplementation.DataStore;

/**
 * @modelguid {5976EB5C-BFC4-46BF-995B-AD41D42C48C7}
 */
public class FilterElement extends ContextRepositoryEntity
{
	public FilterElementAST filterElementAST;

	// public ConditionExpression conditionPart; -> delegate to FilterElementAST
	// public EnableOperatorType enableOperatorType; -> delegate to
	// FilterElementAST
	// public FilterElementCompOper rightOperator; -> delegate to
	// FilterElementAST

	public MatchingPattern matchingPattern;

	/**
	 * @modelguid {51317331-0B65-4CD1-AE8A-20C987FD14D0}
	 * @roseuid 401FAA63027A
	 * @deprecated
	 */
	public FilterElement()
	{
		super();
		// matchingPatterns = new Vector();
	}

	public FilterElement(FilterElementAST ast)
	{
		filterElementAST = ast;

		if (filterElementAST.getMatchingPattern() != null)
		{
			matchingPattern = new MatchingPattern(filterElementAST.getMatchingPattern());
			matchingPattern.setParent(this);
			DataStore.instance().addObject(matchingPattern);
		}
	}

	/**
	 * @return Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.ConditionExpr
	 *         ession
	 * @roseuid 401FAA630284
	 */
	public ConditionExpression getConditionPart()
	{
		return filterElementAST.conditionPart;
	}

	/**
	 * @param conditionValue
	 * @roseuid 401FAA63028E
	 */
	public void setConditionPart(ConditionExpression conditionValue)
	{
		filterElementAST.setConditionPart(conditionValue);
	}

	/**
	 * @return Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.EnableOperato
	 *         rType
	 * @roseuid 401FAA6302A3
	 */
	public EnableOperatorType getEnableOperatorType()
	{
		return filterElementAST.getEnableOperatorType();
	}

	/**
	 * @param enableOperatorTypeValue
	 * @roseuid 401FAA6302B6
	 */
	public void setEnableOperatorType(EnableOperatorType enableOperatorTypeValue)
	{
		filterElementAST.setEnableOperatorType(enableOperatorTypeValue);
	}
	
	public MatchingPattern getMatchingPattern()
	{
		return matchingPattern;
	}

	public void setMatchingPattern(MatchingPattern mpat)
	{
		matchingPattern = mpat;
	}

	/**
	 * @return Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterElement
	 *         CompOper
	 * @roseuid 402AB5780268
	 */
	public FilterElementCompOper getRightOperator()
	{
		return filterElementAST.getRightOperator();
	}

	/**
	 * @param rightOperatorValue
	 * @roseuid 402AB57C02DB
	 */
	public void setRightOperator(FilterElementCompOper rightOperatorValue)
	{
		filterElementAST.setRightOperator(rightOperatorValue);
	}

	public FilterElementAST getFilterElementAST()
	{
		return filterElementAST;
	}
}
