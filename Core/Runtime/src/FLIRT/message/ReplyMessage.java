package Composestar.RuntimeCore.FLIRT.Message;

/**
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 * $Id$
 * 
 * Models the reified message that is handed over to the ACTcallbacks
 * It allows for the replacement of the return value of the message
 */
public class ReplyMessage extends ReifiedMessage {
    
    /**
     * Constructs a new ReplyMessage out of a message and its return value
     * @param m message to reify
     * @param returnValue return value of the message
     * @roseuid 3F36532D0192
     */
    public ReplyMessage(Message m, Object returnValue) {
        super(m);
        super.returnValue = returnValue;     
    }
    
    /**
     * Replaces the return value with a new one.
     * No checks are done to see if the new return value fills the
     * interface requirements of the message (that is, if it is a valid type)
     * @param o new return value
     * @roseuid 3F36532D0195
     */
    public void setReturnObject(Object o) {
        super.returnValue = o;     
    }
}
