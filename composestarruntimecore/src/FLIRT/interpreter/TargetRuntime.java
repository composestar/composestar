package Composestar.RuntimeCore.FLIRT.Interpreter;

import Composestar.RuntimeCore.FLIRT.Message.MessageList;

import java.util.Dictionary;

/**
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 * $Id: TargetRuntime.java,v 1.2 2006/02/13 12:01:32 composer Exp $
 */
public class TargetRuntime extends ReferenceEntityRuntime implements Interpretable 
{
    
    /**
     * @roseuid 40DD68840343
     */
    public TargetRuntime() {
     
    }
    
    /**
     * @param m
     * @param context
     * @return boolean
     * @roseuid 40DD6884034D
     */
    public boolean interpret(MessageList m, Dictionary context) {
     return true;
    }
}
