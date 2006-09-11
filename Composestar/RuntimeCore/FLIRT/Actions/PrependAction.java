package Composestar.RuntimeCore.FLIRT.Actions;

import Composestar.RuntimeCore.FLIRT.Message.Message;

/**
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 * $Id: PrependAction.java 525 2006-09-07 09:18:53Z reddog33hummer $
 * 
 * Models the action to carry out when a Dispatch Filter accepts a message.
 * When executed, it redirects the message to the target specified during
 * filtering of the message in the Filter Specification.
 */
public class PrependAction extends ComposeStarAction 
{
		/*
		 * Target and selector that need to be prepended
		 */
		protected String _selector;
		protected Object _target;
	    
		public PrependAction(Object target, String selector) 
		{
			this._target = target;
			this._selector = selector;
		}
		
		public void execute(Message message){
			message.prependMessage(_target,_selector);
		}
}

