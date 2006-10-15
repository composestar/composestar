package Composestar.RuntimeCore.FLIRT.Actions;

import Composestar.RuntimeCore.FLIRT.Interpreter.FilterRuntime;
import Composestar.RuntimeCore.FLIRT.Message.MessageList;

/**
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 * $Id$
 * 
 * Not implemented!!
 */
public class WaitAction extends ComposeStarAction 
{
    MessageList message;
    
    /**
     * @param currentFilter
     * @param m
     * @roseuid 3F3652D10095
     */
    public WaitAction(FilterRuntime currentFilter, MessageList m) {
		super(m, true);
        FilterRuntime currentFilter1 = currentFilter;
        this.message = m;
		this.continueMessage = m;
    }
    
    /**
     * @return java.lang.Object
     * @roseuid 3F3652D10098
     */
    public Object execute() {
		/*synchronized (currentFilter) {
			//while (!currentFilter.canAccept(message, new Dictionary())) {
				//wait();
			//}

			return null;
		}     */
        return null;
    }

	public MessageList getMessageToContinueWith()
	{
		return this.continueMessage;
	}
}
