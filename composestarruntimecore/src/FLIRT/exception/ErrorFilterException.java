package Composestar.RuntimeCore.FLIRT.Exception;

/**
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 * $Id: ErrorFilterException.java,v 1.2 2006/02/13 12:01:32 composer Exp $
 * 
 * This exception models the error that comes from the rejection of a message
 * by the Error Filter.
 * It is meant to be thrown during the message filtering process
 */
public class ErrorFilterException extends FilterException {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 2425589382372887859L;

	/**
     * @inheritDoc
     * @param m
     * @roseuid 3F36529E03E5
     */
    public ErrorFilterException(String m) {
        super(m);     
    }
    
    /**
     * Default constructor
     * @roseuid 3F36529E03C7
     */
    public ErrorFilterException() {
     
    }
}
