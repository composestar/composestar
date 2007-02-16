package Composestar.RuntimeCore.FLIRT.Interpreter;

import java.util.Dictionary;

import Composestar.RuntimeCore.FLIRT.Message.MessageList;

/**
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente. Licensed under LGPL v2.1 or (at your
 * option) any later version. [http://www.fsf.org/copyleft/lgpl.html] $Id:
 * NotRuntime.java 1835 2006-10-03 21:43:54Z elmuerte $
 */
public class NotRuntime extends UnaryOperatorRuntime implements Interpretable
{
	/**
	 * @roseuid 40DDD436033B
	 */
	public NotRuntime()
	{

	}

	/**
	 * @param operand
	 * @roseuid 40DD5DD702B7
	 */
	public NotRuntime(ConditionExpressionRuntime operand)
	{
		super(operand);
	}

	/**
	 * @param m
	 * @param context
	 * @return boolean
	 * @roseuid 40DD967A0193
	 */
	public boolean interpret(MessageList m, Dictionary context)
	{
		return !operand.interpret(m, context);
	}
}