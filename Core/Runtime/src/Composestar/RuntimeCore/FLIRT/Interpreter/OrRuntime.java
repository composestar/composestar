package Composestar.RuntimeCore.FLIRT.Interpreter;

import java.util.Dictionary;

import Composestar.RuntimeCore.FLIRT.Message.MessageList;

/**
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente. Licensed under LGPL v2.1 or (at your
 * option) any later version. [http://www.fsf.org/copyleft/lgpl.html] $Id:
 * OrRuntime.java 1835 2006-10-03 21:43:54Z elmuerte $
 */
public class OrRuntime extends BinaryOperatorRuntime implements Interpretable
{
	/**
	 * @roseuid 40DDD436038B
	 */
	public OrRuntime()
	{

	}

	/**
	 * @param left
	 * @param right
	 * @roseuid 40DD5DD7025D
	 */
	public OrRuntime(ConditionExpressionRuntime left, ConditionExpressionRuntime right)
	{
		super(left, right);
	}

	/**
	 * @param m
	 * @param context
	 * @return boolean
	 * @roseuid 40DD96770077
	 */
	public boolean interpret(MessageList m, Dictionary context)
	{
		return left.interpret(m, context) || right.interpret(m, context);
	}
}
