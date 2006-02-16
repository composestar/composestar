package Composestar.RuntimeCore.FLIRT.Filtertypes;

import Composestar.RuntimeCore.FLIRT.Message.MessageList;
import Composestar.RuntimeCore.FLIRT.Interpreter.FilterRuntime;
import Composestar.RuntimeCore.FLIRT.Actions.ComposeStarAction;
import Composestar.RuntimeCore.CODER.Model.*;

import java.util.Dictionary;

/**
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 * $Id: FilterTypeRuntime.java,v 1.4 2006/02/13 13:25:11 pascal Exp $
 */
public abstract class FilterTypeRuntime implements DebuggableFilterType
{
    private FilterRuntime filter;
    
    /**
     * @roseuid 40F2ADDE0373
     */
    public FilterTypeRuntime() {
     
    }
    
    /**
     * @param fr
     * @roseuid 40F29D9001D6
     */
    public FilterTypeRuntime(FilterRuntime fr) {
		this.filter = fr;     
    }
    
    /**
     * @param m
     * @param context
     * @return Composestar.Runtime.FLIRT.actions.ComposeStarAction
     * @roseuid 40DFE5A50309
     */
    public abstract ComposeStarAction acceptAction(MessageList originalMessage, MessageList modifiedMessage, Dictionary context);
    
    /**
     * @param m
     * @param context
     * @return Composestar.Runtime.FLIRT.actions.ComposeStarAction
     * @roseuid 40DFE5A80399
     */
    public abstract ComposeStarAction rejectAction(MessageList originalMessage, MessageList modifiedMessage, Dictionary context);
    
    /**
     * @return boolean
     * @roseuid 40F2A80603CC
     */
    public abstract boolean shouldContinueAfterAccepting();
    
    /**
     * @return Composestar.Runtime.FLIRT.interpreter.FilterRuntime
     * @roseuid 40F2AE0600F9
     */
    public FilterRuntime getFilter() {
		return this.filter;     
    }
}
