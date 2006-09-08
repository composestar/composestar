package Composestar.RuntimeCore.FLIRT.Interpreter;

import Composestar.RuntimeCore.FLIRT.Message.Message;

import java.util.Dictionary;

/**
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 * $Id: AbstractPatternRuntime.java 361 2006-06-25 19:24:10Z wminnen $
 */
public abstract class AbstractPatternRuntime extends ReferenceEntityRuntime
{
    public TargetRuntime theTargetRuntime;
    public SelectorRuntime theSelectorRuntime;
    
    /**
     * @roseuid 40DD6885013C
     */
    public AbstractPatternRuntime() {
     
    }
    
    /**
     * @param m
     * @param context
     * @return boolean
     * @roseuid 40DD68850146
     */
    public boolean interpret(Message m, Dictionary context) {
     return true;
    }
}
