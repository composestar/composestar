package Composestar.RuntimeCore.FLIRT.Exception;

/**
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 * $Id: InvalidPatternExpressionException.java,v 1.2 2006/02/13 12:01:32 composer Exp $
 * 
 * Exception occurred while interpreting a pattern expression.
 */
public class InvalidPatternExpressionException extends FilterSpecificationException {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = -6577267134447576239L;

	/**
     * @inheritDoc
     * @param message
     * @roseuid 3F3652A100FA
     */
    public InvalidPatternExpressionException(String message) {
        super(message);     
    }
    
    /**
     * Default constructor
     * @roseuid 3F3652A100D2
     */
    public InvalidPatternExpressionException() {
     
    }
}
