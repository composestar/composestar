package Composestar.RuntimeCore.FLIRT.Exception;

/**
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 * $Id: ErrorFilterException.java,v 1.1 2006/02/16 23:15:54 pascal_durr Exp $
 * 
 * This exception models the error that comes from the rejection of a message by
 * the Error Filter. It is meant to be thrown during the message filtering process.
 */
public class ErrorFilterException extends FilterException 
{

	private static final long serialVersionUID = 2425589382372887859L;

	/**
	 * Default constructor
	 */
	public ErrorFilterException() 
	{
	}

	public ErrorFilterException(String message) 
	{
		super(message);
	}

	/**
	 * Constructor with underlying cause.
	 */
	public ErrorFilterException(ErrorFilterException cause)
	{
		super(cause.getMessage(),cause);
	}
}
