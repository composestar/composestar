package Composestar.RuntimeCore.FLIRT.Interpreter;

import Composestar.RuntimeCore.FLIRT.Message.MessageList;

import java.util.Dictionary;

/**
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 * $Id$
 */
public class AndRuntime extends BinaryOperatorRuntime implements Interpretable 
{    
    /**
     * @roseuid 40DDD43602E1
     */
    public AndRuntime() {
     
    }
    
    /**
     * @param left
     * @param right
     * @roseuid 40DD5DD70285
     */
    public AndRuntime(ConditionExpressionRuntime left, ConditionExpressionRuntime right) {
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
		return left.interpret(m,context) && right.interpret(m,context);     
	}
}
