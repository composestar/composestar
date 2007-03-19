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
import Composestar.Core.RepositoryImplementation.DataStore;

/**
 * 
 */
public class FilterElement extends ContextRepositoryEntity
{
	private static final long serialVersionUID = 3420736618255579131L;

	public FilterElementAST filterElementAST;

	// public ConditionExpression conditionPart; -> delegate to FilterElementAST
	// public EnableOperatorType enableOperatorType; -> delegate to
	// FilterElementAST
	// public FilterElementCompOper rightOperator; -> delegate to
	// FilterElementAST

	public MatchingPattern matchingPattern;

	/**
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
		descriptionFileName = ast.getDescriptionFileName();
		descriptionLineNumber = ast.getDescriptionLineNumber();

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
	 */
	public ConditionExpression getConditionPart()
	{
		return filterElementAST.conditionPart;
	}

	/**
	 * @param conditionValue
	 */
	public void setConditionPart(ConditionExpression conditionValue)
	{
		filterElementAST.setConditionPart(conditionValue);
	}

	/**
	 * @return Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.EnableOperato
	 *         rType
	 */
	public EnableOperatorType getEnableOperatorType()
	{
		return filterElementAST.getEnableOperatorType();
	}

	/**
	 * @param enableOperatorTypeValue
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
	 */
	public FilterElementCompOper getRightOperator()
	{
		return filterElementAST.getRightOperator();
	}

	/**
	 * @param rightOperatorValue
	 */
	public void setRightOperator(FilterElementCompOper rightOperatorValue)
	{
		filterElementAST.setRightOperator(rightOperatorValue);
	}

	public FilterElementAST getFilterElementAST()
	{
		return filterElementAST;
	}

	public String asSourceCode()
	{
		return filterElementAST.asSourceCode();
	}
}
