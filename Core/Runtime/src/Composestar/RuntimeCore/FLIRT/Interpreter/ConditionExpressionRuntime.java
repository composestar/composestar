package Composestar.RuntimeCore.FLIRT.Interpreter;

import Composestar.RuntimeCore.FLIRT.Message.Message;

import java.util.Dictionary;

/**
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 * $Id$
 */
public abstract class ConditionExpressionRuntime extends ReferenceEntityRuntime implements Interpretable 
{
    
    /**
     * @roseuid 40DD5DD702F3
     */
    public ConditionExpressionRuntime() {
     
    }
    
    /**
     * @param m
     * @param context
     * @return boolean
     * @roseuid 40DD62BE017B
     */
    public boolean interpret(Message m, Dictionary context) {
     return true;
    }
}
/**
 * boolean ConditionExpressionRuntime.interpret(Composestar.Runtime.message.Message,java.util.Dictionary){
 * return true;
 * }
 */
