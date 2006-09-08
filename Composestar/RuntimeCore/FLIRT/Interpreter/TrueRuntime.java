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
 * $Id: TrueRuntime.java 27 2006-02-16 23:14:57Z pascal_durr $
 */
public class TrueRuntime extends ConditionExpressionRuntime implements Interpretable 
{
    
    /**
     * @roseuid 40DD5DD701F9
     */
    public TrueRuntime() {
     
    }
    
    /**
     * @param m
     * @param context
     * @return boolean
     * @roseuid 40DD967F02F9
     */
    public boolean interpret(MessageList m, Dictionary context) {
		if(Debug.SHOULD_DEBUG) Debug.out(Debug.MODE_INFORMATION,"FLIRT","\t\t\tInterpreting TrueRuntime...");
		return true;
    }
}
