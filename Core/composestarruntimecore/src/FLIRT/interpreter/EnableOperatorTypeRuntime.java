package Composestar.RuntimeCore.FLIRT.Interpreter;

import Composestar.RuntimeCore.FLIRT.Message.Message;

import java.util.Dictionary;

/**
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 * $Id: EnableOperatorTypeRuntime.java,v 1.2 2006/02/13 12:01:32 composer Exp $
 */
public abstract class EnableOperatorTypeRuntime extends ReferenceEntityRuntime implements Interpretable 
{
    
    /**
     * @roseuid 40DD6885001A
     */
    public EnableOperatorTypeRuntime() {
     
    }
    
    /**
     * @param m
     * @param context
     * @return boolean
     * @roseuid 40DD68850024
     */
    public boolean interpret(Message m, Dictionary context) {
     return true;
    }
}
