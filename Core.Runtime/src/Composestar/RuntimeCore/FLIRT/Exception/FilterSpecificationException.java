package Composestar.RuntimeCore.FLIRT.Exception;

/**
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente. Licensed under LGPL v2.1 or (at your
 * option) any later version. [http://www.fsf.org/copyleft/lgpl.html] $Id:
 * FilterSpecificationException.java 1518 2006-09-20 13:13:30Z reddog33hummer $
 * Represents an exception when interpreting the specification of a filter. For
 * example, a message pattern pointing to a non existing internal or external.
 */
public class FilterSpecificationException extends ComposestarRuntimeException
{

	private static final long serialVersionUID = -511183999904793943L;

	/**
	 * @inheritDoc
	 * @roseuid 3F3652A001A3
	 */
	public FilterSpecificationException()
	{}

	/**
	 * @inheritDoc
	 * @param message
	 * @roseuid 3F3652A001D5
	 */
	public FilterSpecificationException(String message)
	{
		super(message);
	}

	public FilterSpecificationException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public FilterSpecificationException(Throwable cause)
	{
		super(cause);
	}
}
