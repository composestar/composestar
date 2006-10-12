package Composestar.RuntimeCore.FLIRT.Interpreter;

/**
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2006 University of Twente.
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 * $Id: ConditionExpressionRuntime.java 1518 2006-09-20 13:13:30Z reddog33hummer $
 */
public abstract class BinaryOperatorRuntime extends ConditionExpressionRuntime implements Interpretable 
{
	public ConditionExpressionRuntime left;
	public ConditionExpressionRuntime right;
    
	public BinaryOperatorRuntime()
	{

	}

	public BinaryOperatorRuntime(ConditionExpressionRuntime left, ConditionExpressionRuntime right) 
	{
		this.left = left;
		this.right = right;     
	}
}
