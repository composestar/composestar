/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id: CompilerException.java,v 1.1 2006/02/16 23:04:07 pascal_durr Exp $
 */
package Composestar.Core.COMP;


/**
 * Simple internal Exception class for the SrcCompiler package.
 */
public class CompilerException extends Exception {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 4728512274694103401L;

	/**
     * Default ctor
     * @roseuid 401B9896021B
     */
    public CompilerException() {
     
    }
    
    /**
     * Ctor taking an error message.
     * 
     * @param The error message
     * @param message
     * @roseuid 401B97460055
     */
    public CompilerException(String message) {
        super( message );     
    }
}
