package Composestar.RuntimeCore.FLIRT.Interpreter;

import Composestar.RuntimeCore.FLIRT.Message.Message;

import java.util.Dictionary;

/**
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 * $Id: FilterElementCompositionOperatorRuntime.java,v 1.2 2006/02/13 12:01:32 composer Exp $
 */
public abstract class FilterElementCompositionOperatorRuntime extends ReferenceEntityRuntime implements Interpretable {
    public FilterElementRuntime rightArgument;
    
    /**
     * @roseuid 40DD59C50269
     */
    public FilterElementCompositionOperatorRuntime() {
     
    }
    
    /**
     * @return Composestar.Runtime.FLIRT.interpreter.FilterElementRuntime
     * @roseuid 40DD5F5E0011
     */
    public FilterElementRuntime getRightArgument() {
		return this.rightArgument;
    }
    
    /**
     * @param m
     * @param context
     * @return boolean
     * @roseuid 40DD62BE01E0
     */
    public boolean interpret(Message m, Dictionary context) {
     return true;
    }
}
