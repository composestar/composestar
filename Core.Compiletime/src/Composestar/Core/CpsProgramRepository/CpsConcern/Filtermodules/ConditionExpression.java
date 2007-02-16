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
 * composite pattern
 */
public abstract class ConditionExpression extends ContextRepositoryEntity
{
	public static final int RESULT_FALSE = -1;

	public static final int RESULT_TRUE = 0;

	/**
	 * @roseuid 401FAA580095
	 */
	public ConditionExpression()
	{
		super();
	}

	/**
	 * can be used to test whether the condition expression is a binary
	 * expression such as AND and OR; to be overridden by subclasses; default
	 * implementation returns False
	 * 
	 * @return boolean
	 * @roseuid 402A9B1B017D
	 */
	public boolean isBinary()
	{
		return false;
	}

	/**
	 * can be used to test whether the condition expression is a unary
	 * expression such as NOT; to be overridden by subclasses; default
	 * implementation returns False
	 * 
	 * @return boolean
	 * @roseuid 402A9B2800D1
	 */
	public boolean isUnary()
	{
		return false;
	}

	/**
	 * can be used to test whether the condition expression is a literal, such
	 * as the name of a Condition; to be overridden by subclasses; default
	 * implementation returns False
	 * 
	 * @return boolean
	 * @roseuid 402A9B33027C
	 */
	public boolean isLiteral()
	{
		return false;
	}

	/**
	 * Simulate the evaluation of the expression.
	 * 
	 * @return Either RESULT_FALSE, RESULT_TRUE or the number of variables the
	 *         result depends on
	 */
	public abstract int simulateResult();
}
