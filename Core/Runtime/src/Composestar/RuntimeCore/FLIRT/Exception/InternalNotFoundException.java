package Composestar.RuntimeCore.FLIRT.Exception;

/**
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente. Licensed under LGPL v2.1 or (at your
 * option) any later version. [http://www.fsf.org/copyleft/lgpl.html] $Id:
 * InternalNotFoundException.java 2032 2006-10-12 15:08:13Z reddog33hummer $
 * Exception thrown while interpreting a pattern when the pattern specifies a
 * selector not existing in the type definition of the current target. For
 * example, inner.nonExistingMethod.
 */
public class InternalNotFoundException extends ComposestarRuntimeException
{

	private static final long serialVersionUID = 6314339156012912306L;

	/**
	 * Default Constructor
	 * 
	 * @roseuid 3F3652A20051
	 */
	public InternalNotFoundException()
	{}

	public InternalNotFoundException(String internaltype, String internalname, String concern)
	{
		super("Internal '" + internalname + "' of type '" + internaltype + "' in concern '" + concern
				+ "' is not found");
	}

	/**
	 * Constructor with underlying cause.
	 * 
	 * @param cause
	 */
	public InternalNotFoundException(InternalNotFoundException cause)
	{
		super(cause.getMessage(), cause);
	}
}
