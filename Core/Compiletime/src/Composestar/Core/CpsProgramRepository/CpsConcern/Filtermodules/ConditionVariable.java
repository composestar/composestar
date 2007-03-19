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

import Composestar.Core.CpsProgramRepository.CpsConcern.References.ConditionReference;

/**
 * Name of 1 condition (leafs in the composite pattern tree): this is just a
 * wrapper for References.ConditionReference, necessary to deal with typing
 * (i.e. we must inherit from ConditionExpression, and
 * References.ConditionReference is in a Reference inheritance tree. This class
 * used to be called ConditionLiteral
 */
public class ConditionVariable extends ConditionExpression
{
	private static final long serialVersionUID = 3303327628885059583L;

	public ConditionReference condition;

	public ConditionVariable()
	{
		super();
	}

	/**
	 * @return ConditionReference
	 */
	public ConditionReference getCondition()
	{
		return condition;
	}

	/**
	 * @param conditionValue
	 */
	public void setCondition(ConditionReference conditionValue)
	{
		this.condition = conditionValue;
	}

	public int simulateResult()
	{
		return 1;
	}
	
	public String asSourceCode()
	{
		return condition.getName();
	}
}
