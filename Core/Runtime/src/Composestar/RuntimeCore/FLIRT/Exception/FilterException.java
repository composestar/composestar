package Composestar.RuntimeCore.FLIRT.Exception;

import Composestar.RuntimeCore.FLIRT.Interpreter.FilterRuntime;

/**
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente. Licensed under LGPL v2.1 or (at your
 * option) any later version. [http://www.fsf.org/copyleft/lgpl.html] $Id:
 * FilterException.java 2032 2006-10-12 15:08:13Z reddog33hummer $ General
 * filter exception. Contains information about the filter that generated the
 * exception.
 */
public class FilterException extends FilterModuleException
{

	private static final long serialVersionUID = 767206737271784347L;

	private FilterRuntime f = null;

	/**
	 * Default constructor
	 * 
	 * @roseuid 3F36529F0242
	 */
	public FilterException()
	{}

	/**
	 * Constructs a Filter Exception with an accompanying message
	 * 
	 * @param caption the message that goes with the exception
	 * @roseuid 3F36529F026A
	 * @param message
	 */
	public FilterException(String message)
	{
		super(message);
	}

	public FilterException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public FilterException(Throwable cause)
	{
		super(cause);
	}

	/**
	 * Sets the filter in which this exception occurred for later retrieval.
	 * 
	 * @param filter The filter in which the exception occurred.
	 * @roseuid 3F36529F02D8
	 */
	public void setFilter(FilterRuntime filter)
	{
		f = filter;
	}

	/**
	 * Returns the filter in which the exception occurred.
	 * 
	 * @return Filter
	 * @roseuid 3F36529F0346
	 */
	public FilterRuntime getFilter()
	{
		return f;
	}
}
