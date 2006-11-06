package Composestar.RuntimeCore.FLIRT.Actions;

import Composestar.RuntimeCore.FLIRT.Message.Message;
import Composestar.RuntimeCore.FLIRT.Message.MessageList;
import Composestar.RuntimeCore.Utils.Debug;
import Composestar.RuntimeCore.Utils.Invoker;

/**
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente. Licensed under LGPL v2.1 or (at your
 * option) any later version. [http://www.fsf.org/copyleft/lgpl.html] $Id:
 * DispatchToInnerAction.java 2072 2006-10-15 12:44:56Z elmuerte $ Models the
 * action to carry out when a Dispatch Filter accepts a message. When executed,
 * it redirects the message to the target specified during filtering of the
 * message in the Filter Specification.
 */
public class DispatchToInnerAction extends ComposeStarAction
{

	/**
	 * Instance to which redirect the message
	 */
	Object target;

	/**
	 * Selector to which send the message.
	 */
	String selector;

	/**
	 * Arguments of the message
	 */
	Object args[];

	/**
	 * Constructs a Dispatch Action with all the necessary information to do the
	 * invocation.
	 * 
	 * @param target the instance to which the message is to be directed
	 * @param selector method to send the message to
	 * @param args arguments of the message
	 * @param accepted
	 * @roseuid 3F3652C9038D
	 * @param m
	 */
	public DispatchToInnerAction(MessageList m, boolean accepted, Object target, String selector, Object[] args)
	{
		super(m, accepted);
		this.selector = selector;
		this.target = target;
		this.args = args;
		shouldContinue = false;
		this.continueMessage = m;
	}

	/**
	 * Dispatches the message and returns what comes from the invocation. This
	 * is actually delegated to the Invoker in the util package
	 * 
	 * @return what ever the message dispatched returned.
	 * @roseuid 3F3652C90392
	 */
	public Object execute()
	{
		if (Debug.SHOULD_DEBUG) Debug.out(Debug.MODE_INFORMATION, "FLIRT", "Dispatching to inner: " + target.getClass()
				+ "." + selector + " ==> " + target.getClass());
		for (int i = 0; i < args.length; i++)
		{
			if (Debug.SHOULD_DEBUG) Debug.out(Debug.MODE_INFORMATION, "FLIRT", "\tWith argument[" + i + "] = "
					+ args[i]);
		}
		// if(this.continueMessage.STATE == MessageList.MESSAGE_CONSTRUCTOR) //
		// Found a constructor call!
		// TODO do something sensible here
		if (this.continueMessage.getOrgMessage().STATE == Message.MESSAGE_CONSTRUCTOR) // Found
																						// a
																						// constructor
																						// call!
		{
			if (Debug.SHOULD_DEBUG) Debug.out(Debug.MODE_INFORMATION, "FLIRT",
					"Encountered a constructor call, returning null: " + target.getClass() + "()");
			return null;
		}

		// TODO change this
		// this.continueMessage.setTarget(this.continueMessage.getInner());
		// this.continueMessage.setSelector(selector);
		return Invoker.getInstance().invoke(target, selector, args);
	}

	public MessageList getMessageToContinueWith()
	{
		return this.continueMessage;
	}
}
