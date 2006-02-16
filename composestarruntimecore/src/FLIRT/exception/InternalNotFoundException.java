package Composestar.RuntimeCore.FLIRT.Exception;

/**
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 * $Id: InternalNotFoundException.java,v 1.2 2006/02/13 12:01:32 composer Exp $
 * 
 * Exception thrown while interpreting a pattern when the pattern specifies a
 * selector not existing in the type definition of the current target. (For example
 * inner.nonExistingMethod)
 */
public class InternalNotFoundException extends ComposeStarException {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 6314339156012912306L;

	public InternalNotFoundException(String internaltype, String internalname, String concern) {
        super("The internal: "+internalname+" of type "+internaltype+" in concern "+concern+" is not found!");
    }
    
    /**
     * Default Constructor
     * @roseuid 3F3652A20051
     */
    public InternalNotFoundException() {
     
    }
}
