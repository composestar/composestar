package Composestar.RuntimeCore.FLIRT.Interpreter;

import Composestar.RuntimeCore.FLIRT.Message.MessageList;
import Composestar.RuntimeCore.FLIRT.MessageHandlingFacility;
import Composestar.RuntimeCore.Utils.Debug;

import java.util.Dictionary;

/**
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 * $Id: CorFilterElementCompositionOperatorRuntime.java 27 2006-02-16 23:14:57Z pascal_durr $
 */
public class CorFilterElementCompositionOperatorRuntime extends FilterElementCompositionOperatorRuntime implements Interpretable 
{
    private FilterElementRuntime previous;
    
    /**
     * @roseuid 40DE9CDA0171
     */
    public CorFilterElementCompositionOperatorRuntime() {
     
    }
    
    /**
     * @param previous
     * @roseuid 40DD59C500A6
     */
    public CorFilterElementCompositionOperatorRuntime(FilterElementRuntime previous) {
    	this.previous = previous;     
    }
    
    /**
     * @param m
     * @param context
     * @return boolean
     * @roseuid 40DD96A20169
     */
    public boolean interpret(MessageList m, Dictionary context) {
        if(Debug.SHOULD_DEBUG) Debug.out(Debug.MODE_INFORMATION,"FLIRT","\t\tInterpreting CorFilterElementCompositionOperatorRuntime...");
    	FilterElementRuntime left = this.previous;
    	FilterElementRuntime right = super.getRightArgument();
    	if(left.interpret(m,context)) // Left handside matches thus return OK
    	{
    		return true;
    	}
    	else if(right.getNextFilterElementCompositionOperator().interpret(m,context)) // Right handside matches!
    	{
    		return true;
    	}
    	return false;     
    }
}
