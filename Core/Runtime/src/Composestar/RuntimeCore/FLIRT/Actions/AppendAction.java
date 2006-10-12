package Composestar.RuntimeCore.FLIRT.Actions;

import Composestar.RuntimeCore.FLIRT.Message.Message;
import Composestar.RuntimeCore.FLIRT.Message.MessageList;
import Composestar.RuntimeCore.FLIRT.MessageHandlingFacility;
import Composestar.RuntimeCore.Utils.Debug;

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
public class AppendAction extends ComposeStarAction 
{
   
	/**
	 * Message(s) to be prepended
	 */
	MessageList modifiedMessage;
    
	/**
	 * Arguments of the message
	 */
	Object args[];
    
	/**
	 * Constructs a Dispatch Action with all the necessary information to do
	 * the invocation.
	 * @param target the instance to which the message is to be directed
	 * @param selector method to send the message to
	 * @param args arguments of the message
	 * @param accepted
	 * @roseuid 3F3652C9038D
	 */
	public AppendAction( MessageList originalMessage, MessageList modifiedMessage, Object[] args) 
	{
		super(originalMessage, true);
		this.modifiedMessage = modifiedMessage;
		this.args = args;
		shouldContinue = true;  
		this.continueMessage = originalMessage;
		if(Debug.SHOULD_DEBUG) 
		{
			Debug.out( Debug.MODE_DEBUG, "FLIRT", "Constructing a PrependAction with these messagelists:" );
			Debug.out( Debug.MODE_DEBUG, "FLIRT", originalMessage.toString() );
			Debug.out( Debug.MODE_DEBUG, "FLIRT", modifiedMessage.toString() );
		}

		
	}
    
    /**
     * Generate the new continueMessage
     * @see dotNetComposeStar.util.Invoker#invoke
     * @return what ever the message dispatched returned.
     * @roseuid 3F3652C90392
     */
    public Object execute() {
		if(Debug.SHOULD_DEBUG) 
		{
			Debug.out( Debug.MODE_DEBUG, "FLIRT", "Executing AppendAction" );
			Debug.out( Debug.MODE_DEBUG, "FLIRT", "ContinueAction before:" );
			Debug.out( Debug.MODE_DEBUG, "FLIRT", continueMessage.toString() );
		}

		//java.util.Iterator iter = modifiedMessage.
		java.util.Iterator iter =  modifiedMessage.getIterator();
		while( iter.hasNext() )
		{
			continueMessage.append( (Message) iter.next() );
		}

		if(Debug.SHOULD_DEBUG) 
		{
			Debug.out( Debug.MODE_DEBUG, "FLIRT", "ContinueAction after:" );
			Debug.out( Debug.MODE_DEBUG, "FLIRT", continueMessage.toString() );
		}

		return null;
    }



	public MessageList getMessageToContinueWith()
	{
		return this.continueMessage;
	}

	public void updateMessage( MessageList aMessage ) 
	{
		java.util.LinkedList messages = getMessageToContinueWith().getMessages();
		java.util.Iterator i = messages.iterator();
		while( i.hasNext() ) 
		{
			Message m = (Message) i.next();
			aMessage.append( m );
		}
	}
	
}
