package Composestar.RuntimeCore.FLIRT.Interpreter;

import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.ConditionLiteral;
import Composestar.RuntimeCore.FLIRT.Message.MessageList;

import java.util.Dictionary;

/**
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 * $Id$
 */
public class ConditionLiteralRuntime extends ConditionExpressionRuntime implements Interpretable 
{
    
    /**
     * @roseuid 40DD5DD7022B
     */
    public ConditionLiteralRuntime() {
     
    }
    
    /**
     * @param m
     * @param context
     * @return boolean
     * @roseuid 40DD968201C7
     */
    public boolean interpret(MessageList m, Dictionary context) {
    	ConditionLiteral cl = (ConditionLiteral)this.getReference();
    	//We don't need the full name as the filtermodule has this info already
		String conditionname = cl.getCondition().getName();
    	ConditionResolver cr = (ConditionResolver) context.get("ConditionResolver");
        return cr.resolve(conditionname);     
    }
}
