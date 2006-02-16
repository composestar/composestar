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
 * $Id: MatchingPatternRuntime.java,v 1.2 2006/02/13 12:01:32 composer Exp $
 */
public class MatchingPatternRuntime extends ReferenceEntityRuntime implements Interpretable 
{
    public MatchingPartRuntime theMatchingPartRuntime;
    public SubstitutionPartRuntime theSubstitutionPartRuntime;
    
    /**
     * @roseuid 40DD68840299
     */
    public MatchingPatternRuntime() {
     
    }
    
    /**
     * @param m
     * @param context
     * @return boolean
     * @roseuid 40DD688402A3
     */
    public boolean interpret(MessageList m, Dictionary context) {
		if(Debug.SHOULD_DEBUG) Debug.out(Debug.MODE_INFORMATION,"FLIRT","\t\t\tInterpreting matchingPattern '"+this.theSubstitutionPartRuntime.getReference()+"'..." );
		boolean returnvalue = this.theMatchingPartRuntime.interpret(m,context);
		if(returnvalue && this.theSubstitutionPartRuntime != null && this.theSubstitutionPartRuntime.getReference() != null)
		{
			returnvalue = this.theSubstitutionPartRuntime.interpret(m,context);
		}
		return returnvalue;
    }
}
