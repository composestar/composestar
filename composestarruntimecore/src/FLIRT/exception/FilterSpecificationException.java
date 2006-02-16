package Composestar.RuntimeCore.FLIRT.Exception;

/**
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 * $Id: FilterSpecificationException.java,v 1.2 2006/02/13 12:01:32 composer Exp $
 * 
 * Represents an exception ocurred when interpreting the specification of a filter.
 * for example a message pattern pointing to a non existing internal or external.
 */
public class FilterSpecificationException extends ComposeStarException {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = -511183999904793943L;

	/**
     * @inheritDoc
     * @param message
     * @roseuid 3F3652A001D5
     */
    public FilterSpecificationException(String message) {
		super(message);     
    }
    
    /**
     * @inheritDoc
     * @roseuid 3F3652A001A3
     */
    public FilterSpecificationException() {
     
    }
}
