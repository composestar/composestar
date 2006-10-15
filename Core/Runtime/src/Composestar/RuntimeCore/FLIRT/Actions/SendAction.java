package Composestar.RuntimeCore.FLIRT.Actions;

import Composestar.RuntimeCore.FLIRT.Message.MessageList;

/**
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 * $Id$
 * 
 * Models the action to carry out when a Dispatch Filter accepts a message.
 * When executed, it redirects the message to the target specified during
 * filtering of the message in the Filter Specification.
 */
public class SendAction extends ComposeStarAction 
{
	/**
	 * Constructs a Dispatch Action with all the necessary information to do
	 * the invocation.
	 * @param target the instance to which the message is to be directed
	 * @param selector method to send the message to
	 * @param args arguments of the message
	 * @param accepted
	 * @roseuid 3F3652C9038D
     * @param m
	 */
	public SendAction(MessageList m, boolean accepted)
	{
		super(m, accepted);
		shouldContinue = false;
		this.continueMessage = m;
	}
    
	/**
	 * Dispatches the message and returns what comes from the invocation.
	 * This is actually delegated to the Invoker in the util package
	 * @see dotNetComposeStar.util.Invoker#invoke
	 * @return what ever the message dispatched returned.
	 * @roseuid 3F3652C90392
	 */
	public Object execute() 
	{
		return this.continueMessage;
	}

	public MessageList getMessageToContinueWith()
	{
		return this.continueMessage;
	}
	
}
