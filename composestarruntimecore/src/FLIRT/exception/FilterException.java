package Composestar.RuntimeCore.FLIRT.Exception;

import Composestar.RuntimeCore.FLIRT.Interpreter.FilterRuntime;

/**
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 * $Id: FilterException.java,v 1.2 2006/02/13 12:01:32 composer Exp $
 * 
 * General filter exception.
 * Contains information about the filter that generated the exception.
 */
public class FilterException extends FilterModuleException {
    /**
	 * 
	 */
	private static final long serialVersionUID = 767206737271784347L;
	private FilterRuntime f;
    
    /**
     * Constructs a Filter Exception with an accompanying message
     * @param caption the message that goes with the exception
     * @roseuid 3F36529F026A
     */
    public FilterException(String caption) {
        super(caption);
        f = null;     
    }
    
    /**
     * Constructs a Filter Exception.
     * @roseuid 3F36529F0242
     */
    public FilterException() {
        f = null;     
    }
    
    /**
     * Sets the filter in which this exception occurred for later retrieval.
     * @param filter The filter in which the exception occurred.
     * @roseuid 3F36529F02D8
     */
    public void setFilter(FilterRuntime filter) {
        f = filter;     
    }
    
    /**
     * Returns the filter in which the exception occurred.
     * @return Filter
     * @roseuid 3F36529F0346
     */
    public FilterRuntime getFilter() {
        return f;     
    }
}
