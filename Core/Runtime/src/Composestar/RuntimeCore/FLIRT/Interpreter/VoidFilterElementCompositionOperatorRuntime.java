package Composestar.RuntimeCore.FLIRT.Interpreter;

import java.util.Dictionary;

import Composestar.RuntimeCore.FLIRT.Message.MessageList;
import Composestar.RuntimeCore.Utils.Debug;

/**
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 * $Id$
 */
public class VoidFilterElementCompositionOperatorRuntime extends FilterElementCompositionOperatorRuntime implements Interpretable 
{
    private FilterElementRuntime previous;
    
    /**
     * @roseuid 40DE9CDA01DF
     */
    public VoidFilterElementCompositionOperatorRuntime() {
		
    }
    
    /**
     * @param previous
     * @roseuid 40DD59C500CE
     */
    public VoidFilterElementCompositionOperatorRuntime(FilterElementRuntime previous) {
    	this.previous = previous;     
    }
    
    /**
     * @param m
     * @param context
     * @return boolean
     * @roseuid 40DD96A4037F
     */
    public boolean interpret(MessageList m, Dictionary context) {
		if(Debug.SHOULD_DEBUG) Debug.out(Debug.MODE_INFORMATION,"FLIRT","\t\tInterpreting VoidFilterElementCompositionOperatorRuntime...");
		// Last element so we only need to interpret the left side!
    	return this.previous.interpret(m,context);     
    }
}
