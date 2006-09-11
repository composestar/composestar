package Composestar.RuntimeCore.FLIRT.Actions;

import Composestar.RuntimeCore.FLIRT.Message.Message;

/**
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 * $Id: AppendAction.java 361 2006-06-25 19:24:10Z wminnen $
 * 
 * Models the action to carry out when a Dispatch Filter accepts a message.
 * When executed, it redirects the message to the target specified during
 * filtering of the message in the Filter Specification.
 */
public class AppendAction extends ContinueFilterEvaluationAction
{
	/*
	 * Target and selector that need to be appended
	 */
	protected String _selector;
	protected Object _target;
    
	public AppendAction(Object target, String selector) 
	{
		this._target = target;
		this._selector = selector;
	}
	
	public void execute(Message message){
		message.appendMessage(_target,_selector);
	}
}
