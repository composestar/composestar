package Composestar.RuntimeCore.FLIRT.Actions;

import Composestar.RuntimeCore.FLIRT.Message.MessageList;

import java.util.Dictionary;

/**
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 * $Id: ContinueToNextFilterAction.java 361 2006-06-25 19:24:10Z wminnen $
 * 
 * Continues to the next filter.
 * This action does nothing, and allows for the continuing of the filtering of the 
 * message
 */
public class ContinueToNextFilterAction extends ComposeStarAction {
    
    /**
     * Constructs a new Continue to Next Filter Action.
     * @param accepted says if the message was accepted by this filter or not.
     * @roseuid 3F3652C80038
     */
    public ContinueToNextFilterAction(MessageList m, boolean accepted) {
		super(m,accepted);
		this.continueMessage = m;
		shouldContinue = true;     
    }
    
    /**
     * Does nothing
     * @return always null
     * @roseuid 3F3652C80043
     */
    public Object execute() {
		return null;
    }

	public MessageList getMessageToContinueWith()
	{
		return this.continueMessage;
	}

	public void updateMessage( MessageList aMessage ) 
	{
		// do nothing
	}
}
