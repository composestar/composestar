package Composestar.RuntimeCore.FLIRT.Actions;

import Composestar.RuntimeCore.FLIRT.Message.Message;
import Composestar.RuntimeCore.FLIRT.Message.MessageList;
import Composestar.RuntimeCore.FLIRT.MessageHandlingFacility;
import Composestar.RuntimeCore.Utils.Debug;
import Composestar.RuntimeCore.Utils.Invoker;

/**
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 * $Id: DispatchAction.java 361 2006-06-25 19:24:10Z wminnen $
 * 
 * Models the action to carry out when a Dispatch Filter accepts a message.
 * When executed, it redirects the message to the target specified during
 * filtering of the message in the Filter Specification.
 */
public class DispatchAction extends ComposeStarAction 
{
   
	/**
	 * Instance to which redirect the message
	 */
	//Object target;
    
	/**
	 * Selector to which send the message.
	 */
	//String selector;
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
	public DispatchAction(MessageList m, boolean accepted, MessageList modifiedMessage, Object[] args) 
	{
		super(m, accepted);
		this.modifiedMessage = modifiedMessage;
		//this.selector = selector;
		//this.target = target;
		this.args = args;
		shouldContinue = false;  
		this.continueMessage = m;
		if(Debug.SHOULD_DEBUG) 
		{
			Debug.out( Debug.MODE_DEBUG, "FLIRT", "Constructing a DispatchAction with these messagelists:" );
			Debug.out( Debug.MODE_DEBUG, "FLIRT", m.toString() );
			Debug.out( Debug.MODE_DEBUG, "FLIRT", modifiedMessage.toString() );
		}

		
	}
    
    /**
     * Dispatches the message and returns what comes from the invocation.
     * This is actually delegated to the Invoker in the until package
     * @see dotNetComposeStar.util.Invoker#invoke
     * @return what ever the message dispatched returned.
     * @roseuid 3F3652C90392
     */
    public Object execute() {
		if(Debug.SHOULD_DEBUG) Debug.out(Debug.MODE_INFORMATION,"FLIRT","Dispatching in Multiple Dispatch mode");
		for(int i=0; i<args.length; i++)
		{
			if(Debug.SHOULD_DEBUG) Debug.out(Debug.MODE_INFORMATION,"FLIRT","\tWith argument["+i+"] = "+args[i]);
		}

		if(Debug.SHOULD_DEBUG) Debug.out(Debug.MODE_DEBUG,"FLIRT","ContinueMessage:\n"+continueMessage.toShortString() );
		if(Debug.SHOULD_DEBUG) Debug.out(Debug.MODE_DEBUG,"FLIRT","ModifiedMessage:\n"+modifiedMessage.toShortString() );

		//this.continueMessage.setTarget(modifiedMessage.getTarget());
		//this.continueMessage.setSelector(modifiedMessage.getSelector());
		this.continueMessage.replace( modifiedMessage );
		
		if(Debug.SHOULD_DEBUG) Debug.out(Debug.MODE_DEBUG,"FLIRT","ContinueMessage after replace:\n"+continueMessage.toShortString() );

		Object returnValue = null;
		java.util.Iterator ms = modifiedMessage.getIterator();
		while( ms.hasNext() ) 
		{
			Message m = (Message) ms.next();

			if( m.getTarget().equals( "inner" ) ) 
			{
				m.setTarget( m.getInner() );

				if(Debug.SHOULD_DEBUG) 
				{
					Debug.out(Debug.MODE_INFORMATION,"FLIRT","Processing item in message list");
					Debug.out(Debug.MODE_INFORMATION,"FLIRT","Target:   "+m.getTarget().ToString());
					Debug.out(Debug.MODE_INFORMATION,"FLIRT","Selector: "+m.getSelector());
				}

				if(Debug.SHOULD_DEBUG) Debug.out(Debug.MODE_INFORMATION,"FLIRT","Dispatching to inner: "+m.getTarget().GetType().ToString()+"."+m.getSelector()+" ==> "+m.getTarget().GetType().ToString());
				for(int i=0; i<args.length; i++)
				{
					if(Debug.SHOULD_DEBUG) Debug.out(Debug.MODE_INFORMATION,"FLIRT","\tWith argument["+i+"] = "+args[i]);
				}
				if(m.STATE == Message.MESSAGE_CONSTRUCTOR) // Found a constructor call!
				{
					if(Debug.SHOULD_DEBUG) Debug.out(Debug.MODE_INFORMATION,"FLIRT","Encountered a constructor call, returning null: "+m.getTarget().GetType().ToString()+"()");
					return null;
				}

				returnValue = Invoker.getInstance().invoke( m.getTarget(), m.getSelector(), m.getArguments() );     
			}  
			else 
			{
				if(Debug.SHOULD_DEBUG) 
				{
					Debug.out(Debug.MODE_INFORMATION,"FLIRT","Processing item in message list");
					Debug.out(Debug.MODE_INFORMATION,"FLIRT","Target:   "+m.getTarget().ToString());
					Debug.out(Debug.MODE_INFORMATION,"FLIRT","Selector: "+m.getSelector());
				}

				//switch(this.continueMessage.STATE)
				switch(m.STATE)
				{
					case Message.MESSAGE_NONSTATIC_NONSTATIC_VOID :
						MessageHandlingFacility.handleVoidMethodCall(this.continueMessage.getSender(),m.getTarget(),m.getSelector(),this.args);
						break;
					case Message.MESSAGE_NONSTATIC_NONSTATIC_RETURN :
						returnValue = MessageHandlingFacility.handleReturnMethodCall(this.continueMessage.getSender(),m.getTarget(),m.getSelector(),this.args);
						break;
					case Message.MESSAGE_STATIC_NONSTATIC_VOID :
						MessageHandlingFacility.handleVoidMethodCall(this.continueMessage.getSender(),m.getTarget(),m.getSelector(),this.args);
						break;
					case Message.MESSAGE_STATIC_NONSTATIC_RETURN :
						returnValue = MessageHandlingFacility.handleReturnMethodCall(this.continueMessage.getSender(),m.getTarget(),m.getSelector(),this.args);
						break;
				}
			}
			//return null;

		}

		return returnValue;

    }

	public MessageList getMessageToContinueWith()
	{
		return this.continueMessage;
	}
	
}
