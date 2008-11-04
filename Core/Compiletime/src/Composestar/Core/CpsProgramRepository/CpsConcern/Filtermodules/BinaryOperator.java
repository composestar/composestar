/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2006 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */

package Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules;

/**
 * A binary condition operator
 * 
 * @author Michiel Hendriks
 */
public abstract class BinaryOperator extends MatchingExpression
{
	public MatchingExpression left;

	public MatchingExpression right;

	public BinaryOperator()
	{
		super();
	}

	/**
	 * @return ConditionExpression
	 */
	public MatchingExpression getLeft()
	{
		return left;
	}

	/**
	 * @return ConditionExpression
	 */
	public MatchingExpression getRight()
	{
		return right;
	}

	/**
	 * @param leftValue
	 */
	public void setLeft(MatchingExpression leftValue)
	{
		this.left = leftValue;
	}

	/**
	 * @param rightValue
	 */
	public void setRight(MatchingExpression rightValue)
	{
		this.right = rightValue;
	}

	/**
	 * @return boolean
	 */
	public boolean isBinary()
	{
		return true;
	}
}
