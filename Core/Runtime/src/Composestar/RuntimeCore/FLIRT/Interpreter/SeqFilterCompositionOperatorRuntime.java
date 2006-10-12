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
public class SeqFilterCompositionOperatorRuntime extends FilterCompositionOperatorRuntime implements Interpretable 
{
    private FilterRuntime previous;
    
    /**
     * @roseuid 40DDDE520094
     */
    public SeqFilterCompositionOperatorRuntime() {
     
    }
    
    /**
     * @param previous
     * @roseuid 40DD59C50060
     */
    public SeqFilterCompositionOperatorRuntime(FilterRuntime previous) {
    	this.previous = previous;     
    }
    
    /**
     * @param m
     * @param context
     * @return boolean
     * @roseuid 40DD96A801D6
     */
    public boolean interpret(MessageList m, Dictionary context) {
    	FilterRuntime right = super.getRightArgument();
    	FilterRuntime left = this.previous;
		
		// TODO: FIX this how do we determine if we should continue after interpretation of the
    	// first filter! This is dependant on the filtertype but do we want this information here!
    	return true;     
    }
}
