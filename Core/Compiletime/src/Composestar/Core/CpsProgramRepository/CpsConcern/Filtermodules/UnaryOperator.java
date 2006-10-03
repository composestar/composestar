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
 * A unary condition operator
 * 
 * @author Michiel Hendriks
 */
public abstract class UnaryOperator extends ConditionExpression
{
	/**
	 * Unary operator
	 */
	public ConditionExpression operand;

	public UnaryOperator()
	{
		super();
	}

	/**
	 * @return ConditionExpression
	 */
	public ConditionExpression getOperand()
	{
		return operand;
	}

	/**
	 * @param operandValue
	 */
	public void setOperand(ConditionExpression operandValue)
	{
		this.operand = operandValue;
	}

	/**
	 * @return boolean
	 */
	public boolean isBinary()
	{
		return (false);
	}

	/**
	 * @return boolean
	 */
	public boolean isUnary()
	{
		return (true);
	}

	/**
	 * @return boolean
	 */
	public boolean isLiteral()
	{
		return (false);
	}

}
