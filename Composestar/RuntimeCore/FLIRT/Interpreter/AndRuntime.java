package Composestar.RuntimeCore.FLIRT.Interpreter;

import Composestar.RuntimeCore.FLIRT.Message.MessageList;

import java.util.Dictionary;

/**
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 * $Id: AndRuntime.java 27 2006-02-16 23:14:57Z pascal_durr $
 */
public class AndRuntime extends ConditionExpressionRuntime implements Interpretable 
{
    public ConditionExpressionRuntime right;
    public ConditionExpressionRuntime left;
    
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
    	this.left = left;
    	this.right = right;     
    }
    
    /**
     * @param m
     * @param context
     * @return boolean
     * @roseuid 40DD96770077
     */
    public boolean interpret(MessageList m, Dictionary context) {
     	return left.interpret(m,context) && right.interpret(m,context);     
    }
}
