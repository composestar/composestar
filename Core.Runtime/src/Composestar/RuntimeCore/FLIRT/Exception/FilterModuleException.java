package Composestar.RuntimeCore.FLIRT.Exception;

/**
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente. Licensed under LGPL v2.1 or (at your
 * option) any later version. [http://www.fsf.org/copyleft/lgpl.html] $Id:
 * FilterModuleException.java 1518 2006-09-20 13:13:30Z reddog33hummer $
 * Exception at the filter module level. This exception can occur, for example,
 * when a call to a non existing condition is made.
 */
public class FilterModuleException extends ComposestarRuntimeException
{

	private static final long serialVersionUID = 4647418377837580856L;

	/**
	 * Default constructor
	 * 
	 * @roseuid 3F3652A00012
	 */
	public FilterModuleException()
	{}

	/**
	 * @inheritDoc
	 * @param message
	 * @roseuid 3F3652A0003A
	 */
	public FilterModuleException(String message)
	{
		super(message);
	}

	public FilterModuleException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public FilterModuleException(Throwable cause)
	{
		super(cause);
	}
}
