package Composestar.RuntimeCore.FLIRT.Filtertypes;

import java.util.Dictionary;

import Composestar.RuntimeCore.FLIRT.Actions.ComposeStarAction;
import Composestar.RuntimeCore.FLIRT.Actions.ContinueToNextFilterAction;
import Composestar.RuntimeCore.FLIRT.Actions.DispatchAction;
import Composestar.RuntimeCore.FLIRT.Message.MessageList;


/**
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 * $Id$
 * 
 * Dispatch Filter
 * This filter redirects messages that accepts to the objects (internals or 
 * externals)
 * defined in its specification.
 */
public class Dispatch extends FilterTypeRuntime
{
    
    /**
     * @param m
     * @param context
     * @return Composestar.Runtime.FLIRT.actions.ComposeStarAction
     * @roseuid 40DFDDA502B4
     */
    public ComposeStarAction acceptAction(MessageList originalMessage, MessageList modifiedMessage, Dictionary context) {
		/*
		if(modifiedMessage.getSelector().equals("*"))
		{
			modifiedMessage.setSelector(originalMessage.getSelector());
		}

		// If we dispatch to inner we should dispatch to the inner object
		// In all other cases hand it over to the message handlingfacilty
		if(modifiedMessage.getTarget().equals("inner"))
		{
			modifiedMessage.setTarget(originalMessage.getInner());
			return new DispatchToInnerAction(originalMessage,true,originalMessage.getInner(),modifiedMessage.getSelector(), originalMessage.getArguments());
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

		//TODO: move dispatch to inner logic to DispatchAction
		// Do not replace inner

		replaceWildcards( originalMessage, modifiedMessage );

		return new DispatchAction(originalMessage, true, modifiedMessage, originalMessage.getArguments());     
    }
    
    /**
     * @param m
     * @param context
     * @return Composestar.Runtime.FLIRT.actions.ComposeStarAction
     * @roseuid 40DFDDA502DC
     */
    public ComposeStarAction rejectAction(MessageList originalMessage, MessageList modifiedMessage, Dictionary context) {
		return new ContinueToNextFilterAction(originalMessage, false);     
    }
    
    /**
     * @return boolean
     * @roseuid 40F2A8E803D1
     */
    public boolean shouldContinueAfterAccepting() {
		return false;     
    }
}
