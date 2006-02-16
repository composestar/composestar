package Composestar.RuntimeCore.FLIRT.Exception;

import java.lang.RuntimeException;
/**
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 * $Id: ComposeStarException.java,v 1.3 2006/02/13 13:51:06 composer Exp $
 */
public class ComposeStarException extends RuntimeException {

	/**
     * Constructs a new ComposeStarException carring a String as a message
     * @param message General message to send
     * @roseuid 3F36529E02F5
     */
    public ComposeStarException(String message) {
		super(message);
    }
    
    /**
     * Default constructor
     * @roseuid 3F36529E02C2
     */
    public ComposeStarException() {
		this("");
    }
}
