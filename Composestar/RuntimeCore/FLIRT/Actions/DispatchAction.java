package Composestar.RuntimeCore.FLIRT.Actions;

import Composestar.RuntimeCore.FLIRT.Message.Message;

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
public class DispatchAction extends ResetFilterEvaluationAction 
{
	protected Object _target;
	protected String _selector;
	
	/**
	 * Constructs a Dispatch Action with all the necessary information to do
	 * the invocation.
	 * @param target the instance to which the message is to be directed
	 * @param selector method to send the message to
	 * @param args arguments of the message
	 * @param accepted
	 * @roseuid 3F3652C9038D
	 */
	public DispatchAction(String target, String selector)
	{
		_target = target;
		_selector = selector;
	}
    
	public void execute(Message message){
		message.setSelector(_selector);
		message.setTarget(_target);
	}
}
