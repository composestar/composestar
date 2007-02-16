package Composestar.RuntimeCore.FLIRT.Actions;

import Composestar.RuntimeCore.FLIRT.Message.Message;
import Composestar.RuntimeCore.FLIRT.Message.MessageList;
import Composestar.RuntimeCore.Utils.Debug;

/**
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente. Licensed under LGPL v2.1 or (at your
 * option) any later version. [http://www.fsf.org/copyleft/lgpl.html] $Id:
 * PrependAction.java 2072 2006-10-15 12:44:56Z elmuerte $ Models the action to
 * carry out when a Dispatch Filter accepts a message. When executed, it
 * redirects the message to the target specified during filtering of the message
 * in the Filter Specification.
 */
public class PrependAction extends ComposeStarAction
{

	/**
	 * Message(s) to be prepended
	 */
	MessageList modifiedMessage;

	/**
	 * Arguments of the message
	 */
	Object args[];

	private static final Composestar.RuntimeCore.FLIRT.Message.Message[] EmptyMessagesArray = new Composestar.RuntimeCore.FLIRT.Message.Message[0];

	/**
	 * Constructs a Dispatch Action with all the necessary information to do the
	 * invocation.
	 * 
	 * @param target the instance to which the message is to be directed
	 * @param selector method to send the message to
	 * @param args arguments of the message
	 * @param accepted
	 * @roseuid 3F3652C9038D
	 * @param originalMessage
	 * @param modifiedMessage
	 */
	public PrependAction(MessageList originalMessage, MessageList modifiedMessage, Object[] args)
	{
		super(originalMessage, true);
		this.modifiedMessage = modifiedMessage;
		this.args = args;
		shouldContinue = true;
		this.continueMessage = originalMessage;
		if (Debug.SHOULD_DEBUG)
		{
			Debug.out(Debug.MODE_DEBUG, "FLIRT", "Constructing a PrependAction with these messagelists:");
			Debug.out(Debug.MODE_DEBUG, "FLIRT", originalMessage.toString());
			Debug.out(Debug.MODE_DEBUG, "FLIRT", modifiedMessage.toString());
		}

	}

	/**
	 * Does nothing particular.
	 * 
	 * @see dotNetComposeStar.util.Invoker#invoke
	 * @return what ever the message dispatched returned.
	 * @roseuid 3F3652C90392
	 */
	public Object execute()
	{
		updateMessage(continueMessage);
		return null;
	}

	public MessageList getMessageToContinueWith()
	{
		return this.continueMessage;
	}

	public void updateMessage(MessageList aMessage)
	{
		if (Debug.SHOULD_DEBUG)
		{
			Debug.out(Debug.MODE_DEBUG, "FLIRT", "Executing PrependAction");
			Debug.out(Debug.MODE_DEBUG, "FLIRT", "Message before:");
			Debug.out(Debug.MODE_DEBUG, "FLIRT", aMessage.toString());
		}

		Message[] messages = EmptyMessagesArray;
		messages = (Message[]) modifiedMessage.getMessages().toArray(messages);
		for (int i = messages.length - 1; i >= 0; i--)
		{
			aMessage.prepend(messages[i]);
		}

		if (Debug.SHOULD_DEBUG)
		{
			Debug.out(Debug.MODE_DEBUG, "FLIRT", "Message after:");
			Debug.out(Debug.MODE_DEBUG, "FLIRT", aMessage.toString());
		}
	}

}
