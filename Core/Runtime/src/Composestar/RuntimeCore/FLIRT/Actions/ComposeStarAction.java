package Composestar.RuntimeCore.FLIRT.Actions;

import Composestar.RuntimeCore.FLIRT.Message.MessageList;

/**
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 * $Id$
 * 
 * Parent class of all Actions
 * Actions dictate the result of a message passing by a filter.
 * In particular, an action can just be to pass on to the next filter
 */
public abstract class ComposeStarAction {
    
    /**
     * Says if the message being filtered was accepted  by the filter
     * that generated this action or not
     */
    private boolean accepted;
    
    /**
     * Says if this message should continue to the filtering process or if
     * it should be returned to the sender.
     */
    protected boolean shouldContinue;

	protected MessageList returnableMessage = null;

	protected MessageList continueMessage = null;
    
    /**
     * Constructs a new ComposeStarAction
     * Only classes that extend ComposeStarAction can access it.
     * @param accepted if the filter accepted the message
     * @roseuid 3F3652C703DA
     * @param message
     */
    protected ComposeStarAction(MessageList message, boolean accepted) {
		this.returnableMessage = message;
		this.accepted = accepted;     
    }
    
    /**
     * Says if the message was accepted.
     * Another way to read this is to say that this action is a result of the message
     * being accepted.
     * @return boolean TRUE if the message was accepted, False otherwise.
     * @roseuid 3F3652C703E4
     */
    public boolean wasAccepted() {
		return this.accepted;     
    }
    
    /**
     * Says if the message should continue in the filtering process or not.
     * @return TRUE if it should continue, FALSE otherwise.
     * @roseuid 3F3652C80006
     */
    public boolean getShouldContinue() {
		return this.shouldContinue;     
    }

	public abstract MessageList getMessageToContinueWith();

    /**
     * Execute this action.
     * Does whatever the action is supposed to do. In case the action results in the
     * production of a reply for the sender of the message, this Object is to be 
     * returned. If
     * there is no result to return, null.
     * @return the object that is to be sent to the original sender of the message,
     * null if there is none.
     * @roseuid 3F3652C80007
     */
    public abstract Object execute();

	public void updateMessage( MessageList aMessage ) 
	{
		aMessage.copyFromMessageList( getMessageToContinueWith() );
	}
}
