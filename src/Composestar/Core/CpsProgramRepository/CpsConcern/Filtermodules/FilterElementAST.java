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

import Composestar.Core.RepositoryImplementation.ContextRepositoryEntity;

/**
 * @modelguid {5976EB5C-BFC4-46BF-995B-AD41D42C48C7}
 */
public class FilterElementAST extends ContextRepositoryEntity
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6524966819127558665L;

	/**
	 * @modelguid {6783CA89-B3A5-4BAC-959E-31BB7590D241}
	 */
	public ConditionExpression conditionPart;

	/**
	 * @modelguid {0B16FE4E-FD0E-4CE7-A05C-714E74CAC427}
	 */
	public EnableOperatorType enableOperatorType;

	public FilterElementCompOper rightOperator;

	public MatchingPatternAST matchingPattern;

	/**
	 * @modelguid {51317331-0B65-4CD1-AE8A-20C987FD14D0}
	 * @roseuid 401FAA63027A
	 */
	public FilterElementAST()
	{
		super();
	}

	/**
	 * @return Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.ConditionExpr
	 *         ession
	 * @roseuid 401FAA630284
	 */
	public ConditionExpression getConditionPart()
	{
		return conditionPart;
	}

	/**
	 * @param conditionValue
	 * @roseuid 401FAA63028E
	 */
	public void setConditionPart(ConditionExpression conditionValue)
	{
		this.conditionPart = conditionValue;
	}

	/**
	 * @return Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.EnableOperato
	 *         rType
	 * @roseuid 401FAA6302A3
	 */
	public EnableOperatorType getEnableOperatorType()
	{
		return enableOperatorType;
	}

	/**
	 * @param enableOperatorTypeValue
	 * @roseuid 401FAA6302B6
	 */
	public void setEnableOperatorType(EnableOperatorType enableOperatorTypeValue)
	{
		this.enableOperatorType = enableOperatorTypeValue;
	}

	public MatchingPatternAST getMatchingPattern()
	{
		return matchingPattern;
	}

	public void setMatchingPattern(MatchingPatternAST mpat)
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
		return rightOperator;
	}

	/**
	 * @param rightOperatorValue
	 * @roseuid 402AB57C02DB
	 */
	public void setRightOperator(FilterElementCompOper rightOperatorValue)
	{
		this.rightOperator = rightOperatorValue;
	}
}
