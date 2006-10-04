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
public abstract class BinaryOperator extends ConditionExpression
{
	public ConditionExpression left;
	public ConditionExpression right;

	public BinaryOperator()
	{
		super();
	}

	/**
	 * @return ConditionExpression
	 */
	public ConditionExpression getLeft()
	{
		return left;
	}

	/**
	 * @return ConditionExpression
	 */
	public ConditionExpression getRight()
	{
		return right;
	}

	/**
	 * @param leftValue
	 */
	public void setLeft(ConditionExpression leftValue)
	{
		this.left = leftValue;
	}

	/**
	 * @param rightValue
	 */
	public void setRight(ConditionExpression rightValue)
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
