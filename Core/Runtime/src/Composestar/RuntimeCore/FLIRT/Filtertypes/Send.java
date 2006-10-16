package Composestar.RuntimeCore.FLIRT.Filtertypes;

import Composestar.RuntimeCore.FLIRT.Actions.*;
import Composestar.RuntimeCore.FLIRT.Message.MessageList;

import java.util.Dictionary;

/**
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 * $Id$
 * 
 * Models the Send filter
 * Not implemented because it is an output filter, and those are not implemented.
 */
public class Send extends FilterTypeRuntime
{
    
    /**
     * @roseuid 3F3652C20009
     */
    public Send() {
     
    }
    
    /**
     * @param m
     * @param context
     * @return Composestar.Runtime.FLIRT.actions.ComposeStarAction
     * @roseuid 40DFE17B0248
     */
    public ComposeStarAction acceptAction(MessageList originalMessage, MessageList modifiedMessage, Dictionary context) {
		/*
		if(modifiedMessage.getSelector().equals("*"))
		{
			modifiedMessage.setSelector(originalMessage.getSelector());
		}
		if(modifiedMessage.getTarget().equals("inner"))
		{
			modifiedMessage.setTarget(originalMessage.getInner());
		}
		else if(modifiedMessage.getTarget().equals("*"))
		{
			modifiedMessage.setTarget(originalMessage.getTarget());
		}
		else if(modifiedMessage.getTarget() instanceof String && originalMessage.getInternal((String)modifiedMessage.getTarget()) != null)
		{
			modifiedMessage.setTarget(originalMessage.getInternal((String)modifiedMessage.getTarget()));
		}
		else if(modifiedMessage.getTarget() instanceof String && originalMessage.getExternal((String)modifiedMessage.getTarget()) != null)
		{
			modifiedMessage.setTarget(originalMessage.getExternal((String)modifiedMessage.getTarget()));
		}
		*/
		replaceInner( originalMessage, modifiedMessage );
		replaceWildcards( originalMessage, modifiedMessage );
		return new SendAction(modifiedMessage, true);
    }
    
    /**
     * @param m
     * @param context
     * @return Composestar.Runtime.FLIRT.actions.ComposeStarAction
     * @roseuid 40DFE17B0270
     */
    public ComposeStarAction rejectAction(MessageList originalMessage, MessageList modifiedMessage, Dictionary context) {
       return new ContinueToNextFilterAction(originalMessage, false);
    }
    
    /**
     * @return boolean
     * @roseuid 40F2A8E901F2
     */
    public boolean shouldContinueAfterAccepting() {
     return true;
    }
}