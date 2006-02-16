package Composestar.RuntimeCore.FLIRT.Exception;

/**
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 * $Id: FilterModuleException.java,v 1.2 2006/02/13 12:01:32 composer Exp $
 * 
 * Exception at the filter module level.
 * This exception can occur, for example, when a call to a non existing condition 
 * is made.
 */
public class FilterModuleException extends ComposeStarException {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 4647418377837580856L;

	/**
     * @inheritDoc
     * @param message
     * @roseuid 3F3652A0003A
     */
    public FilterModuleException(String message) {
		super(message);     
    }
    
    /**
     * Default constructor
     * @roseuid 3F3652A00012
     */
    public FilterModuleException() {
     
    }
}
