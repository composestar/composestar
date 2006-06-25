package Composestar.RuntimeCore.FLIRT.Filtertypes;

import Composestar.RuntimeCore.FLIRT.Message.MessageList;
import Composestar.RuntimeCore.FLIRT.Actions.ContinueToNextFilterAction;
import Composestar.RuntimeCore.FLIRT.Actions.MetaAction;
import Composestar.RuntimeCore.FLIRT.Actions.ComposeStarAction;
import Composestar.RuntimeCore.CODER.Model.*;

import java.util.Dictionary;

/**
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 * $Id: Meta.java,v 1.1 2006/02/16 23:15:54 pascal_durr Exp $
 * 
 * Models the Meta filter
 * If a message is accepted, it is reified and offered to a method defined in the
 * filter specification. if the message is rejected, it is passed on to the next 
 * filter.
 */
public class Meta extends FilterTypeRuntime implements DebuggableMetaFilterType
{
    
    /**
     * @param m
     * @param context
     * @return Composestar.Runtime.FLIRT.actions.ComposeStarAction
     * @roseuid 40DFE17B018A
     */
    public ComposeStarAction acceptAction(MessageList originalMessage, MessageList modifiedMessage, Dictionary context) {
		/*
		if(modifiedMessage.getTarget().equals("*"))
		{
			modifiedMessage.setTarget(originalMessage.getTarget());
		}
		else if(modifiedMessage.getTarget().equals("inner"))
		{
			modifiedMessage.setTarget(originalMessage.getInner());
		}
		else if(modifiedMessage.getTarget() instanceof String && originalMessage.getInternal((String)modifiedMessage.getTarget()) != null)
		{
			modifiedMessage.setTarget(originalMessage.getInternal((String)modifiedMessage.getTarget()));
		}
		else if(modifiedMessage.getTarget() instanceof String && originalMessage.getExternal((String)modifiedMessage.getTarget()) != null)
		{
			modifiedMessage.setTarget(originalMessage.getExternal((String)modifiedMessage.getTarget()));
		}
		
		if(modifiedMessage.getSelector().equals("*"))
		{
			modifiedMessage.setSelector(originalMessage.getSelector());
		}
		*/
		replaceInner( originalMessage, modifiedMessage );
		replaceWildcards( originalMessage, modifiedMessage );
		return new MetaAction(originalMessage, originalMessage.reify(), modifiedMessage.getFirstMessage().getTarget(), modifiedMessage.getFirstMessage().getSelector(), true);     
    }
    
    /**
     * @param m
     * @param context
     * @return Composestar.Runtime.FLIRT.actions.ComposeStarAction
     * @roseuid 40DFE17B01BC
     */
    public ComposeStarAction rejectAction(MessageList originalMessage, MessageList modifiedMessage, Dictionary context) {
		return new ContinueToNextFilterAction(originalMessage, false);     
    }
    
    /**
     * @return boolean
     * @roseuid 40F2A8E9016F
     */
    public boolean shouldContinueAfterAccepting() {
     return true;
    }
}
