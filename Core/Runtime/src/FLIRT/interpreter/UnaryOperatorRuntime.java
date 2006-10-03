package Composestar.RuntimeCore.FLIRT.Interpreter;

/**
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2006 University of Twente.
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 * $Id: ConditionExpressionRuntime.java 1518 2006-09-20 13:13:30Z reddog33hummer $
 */
public abstract class UnaryOperatorRuntime extends ConditionExpressionRuntime implements Interpretable 
{
	public ConditionExpressionRuntime operand;
    
	public UnaryOperatorRuntime()
	{

	}

	public UnaryOperatorRuntime(ConditionExpressionRuntime operand) 
	{
		this.operand = operand;    
	}

}
