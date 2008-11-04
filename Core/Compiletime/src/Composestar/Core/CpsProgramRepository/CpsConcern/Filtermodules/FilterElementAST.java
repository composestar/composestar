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
 * 
 */
public class FilterElementAST extends ContextRepositoryEntity
{

	private static final long serialVersionUID = -6524966819127558665L;

	public MatchingExpression conditionPart;

	public EnableOperatorType enableOperatorType;

	public FilterElementCompOper rightOperator;

	public MatchingPatternAST matchingPattern;

	public FilterElementAST()
	{
		super();
	}

	/**
	 * @return Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.ConditionExpr
	 *         ession
	 */
	public MatchingExpression getConditionPart()
	{
		return conditionPart;
	}

	/**
	 * @param conditionValue
	 */
	public void setConditionPart(MatchingExpression conditionValue)
	{
		this.conditionPart = conditionValue;
	}

	/**
	 * @return Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.EnableOperato
	 *         rType
	 */
	public EnableOperatorType getEnableOperatorType()
	{
		return enableOperatorType;
	}

	/**
	 * @param enableOperatorTypeValue
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
	 */
	public FilterElementCompOper getRightOperator()
	{
		return rightOperator;
	}

	/**
	 * @param rightOperatorValue
	 */
	public void setRightOperator(FilterElementCompOper rightOperatorValue)
	{
		this.rightOperator = rightOperatorValue;
	}

	public String asSourceCode()
	{
		StringBuffer sb = new StringBuffer();
		if (!((conditionPart instanceof True) && (enableOperatorType instanceof EnableOperator)))
		{
			if (conditionPart != null)
			{

				sb.append(conditionPart.asSourceCode());
				sb.append(" ");

			}
			if (enableOperatorType != null)
			{
				sb.append(enableOperatorType.asSourceCode());
				sb.append(" ");
			}
		}
		if (matchingPattern != null)
		{
			sb.append(matchingPattern.asSourceCode());
		}
		// right oprator is added by FilterAST
		/*
		 * if (rightOperator != null) { sb.append(rightOperator.asSourceCode()); }
		 */
		return sb.toString();
	}
}
