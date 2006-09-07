package Composestar.RuntimeCore.FLIRT.Interpreter;

import Composestar.RuntimeCore.FLIRT.Message.MessageList;

import java.util.Dictionary;

/**
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 * $Id: NotRuntime.java,v 1.2 2006/02/13 12:01:32 composer Exp $
 */
public class NotRuntime extends ConditionExpressionRuntime implements Interpretable 
{
    public ConditionExpressionRuntime operand;
    
    /**
     * @roseuid 40DDD436033B
     */
    public NotRuntime() {
     
    }
    
    /**
     * @param operand
     * @roseuid 40DD5DD702B7
     */
    public NotRuntime(ConditionExpressionRuntime operand) {
    	this.operand = operand;     
    }
    
    /**
     * @param m
     * @param context
     * @return boolean
     * @roseuid 40DD967A0193
     */
    public boolean interpret(MessageList m, Dictionary context) {
    		return !operand.interpret(m,context);     
    }
}
