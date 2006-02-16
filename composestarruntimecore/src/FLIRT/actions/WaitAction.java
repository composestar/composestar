package Composestar.RuntimeCore.FLIRT.Actions;

import Composestar.RuntimeCore.FLIRT.Message.MessageList;
import Composestar.RuntimeCore.FLIRT.Interpreter.FilterRuntime;

import java.util.Dictionary;

/**
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 * $Id: WaitAction.java,v 1.2 2006/02/13 12:01:32 composer Exp $
 * 
 * Not implemented!!
 */
public class WaitAction extends ComposeStarAction 
{
    private FilterRuntime currentFilter;
    MessageList message;
    
    /**
     * @param currentFiliter
     * @param m
     * @roseuid 3F3652D10095
     */
    public WaitAction(FilterRuntime currentFiliter, MessageList m) {
		super(m, true);
		this.currentFilter = currentFilter;
		this.message = m;   
		this.continueMessage = m;
    }
    
    /**
     * @return java.lang.Object
     * @roseuid 3F3652D10098
     */
    public Object execute() {
		synchronized (currentFilter) {
			//while (!currentFilter.canAccept(message, new Dictionary())) {
				//wait();
			//}

			return null;
		}     
    }

	public MessageList getMessageToContinueWith()
	{
		return this.continueMessage;
	}
}
