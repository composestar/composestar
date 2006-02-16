package Composestar.RuntimeCore.FLIRT.Exception;

import Composestar.RuntimeCore.FLIRT.Message.Message;

/**
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 * $Id: MessageNotFilteredException.java,v 1.2 2006/02/13 12:01:32 composer Exp $
 * 
 * Exception thrown when a message traverses all the filters
 * within a filter module and is not filtered by any of them.
 * It contains information about the the message that was being filtered.
 */
public class MessageNotFilteredException extends FilterModuleException {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1014195025208885473L;
	/**
     * Message that was being Filtered
     */
    Message m;
    
    /**
     * @inheritDoc
     * @param caption
     * @roseuid 3F3652A1026D
     */
    public MessageNotFilteredException(String caption) {
        super(caption);
        m = null;     
    }
    
    /**
     * @inheritDoc
     * @roseuid 3F3652A10245
     */
    public MessageNotFilteredException() {
        m = null;     
    }
    
    /**
     * Sets the Message that was not filtered
     * @param aMessage the message that fell of the other side
     * @roseuid 3F3652A102DB
     */
    public void setComposeStarMessage(Message aMessage) {
        m = aMessage;     
    }
    
    /**
     * Gets the message that was not filtered
     * @param aMessage the message that fell of the other side
     * @return Composestar.Runtime.FLIRT.message.Message
     * @roseuid 3F3652A10353
     */
    public Message getComposeStarMessage() {
        return m;     
    }
    
    /**
     * @return java.lang.String
     * @roseuid 3F3652A1037B
     */
    //public String getMessage() {
    //    return super.getMessage() + "Not filtered message was: " +
    //           m.getSelector();     
    //}
}
