package Composestar.RuntimeCore.FLIRT.Interpreter;

import Composestar.RuntimeCore.FLIRT.Message.MessageList;

import java.util.Dictionary;

/**
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 * $Id: SelectorRuntime.java 27 2006-02-16 23:14:57Z pascal_durr $
 */
public class SelectorRuntime extends ReferenceEntityRuntime implements Interpretable 
{
    
    /**
     * @roseuid 40DD688501B4
     */
    public SelectorRuntime() {
     
    }
    
    /**
     * @param m
     * @param context
     * @return boolean
     * @roseuid 40DD688501BE
     */
    public boolean interpret(MessageList m, Dictionary context) {
     return true;
    }
}
