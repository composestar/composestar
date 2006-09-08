package Composestar.RuntimeCore.FLIRT.Actions;

import Composestar.RuntimeCore.FLIRT.Message.MessageList;
import Composestar.RuntimeCore.FLIRT.Exception.ErrorFilterException;

import java.util.Dictionary;

/**
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 * $Id: ErrorAction.java 117 2006-03-07 12:47:39Z oohlaf $
 * 
 * Models the action that is produced when a message is rejected by an Error Filter
 * The action defined is the throw of a ErrorFilterException.
 */
public class ErrorAction extends ComposeStarAction {
    
    /**
     * The exception to be thrown when the action is executed
     */
    private ErrorFilterException theErrorFilterException;
    
    /**
     * Constructs an error action
     * The error action takes the exception to be thrown, and throws it when it's
     * executed.
     * @param accepted says if the filter accepted the message or not
     * @param exception the exception to throw when the action is executed
     * @roseuid 3F3652CA0027
     */
    public ErrorAction(MessageList m, boolean accepted, ErrorFilterException exception) {
		super(m, accepted);
		theErrorFilterException = exception;
		this.continueMessage = m;
    }
    
    /**
     * Executes the action by throwing an exception
     * @return allways return null.
     * @roseuid 3F3652CA0031
     */
    public Object execute() {
		throw theErrorFilterException;     
    }

	public MessageList getMessageToContinueWith()
	{
		return this.continueMessage;
	}
}
