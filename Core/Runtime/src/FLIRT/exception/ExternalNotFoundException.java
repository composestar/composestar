package Composestar.RuntimeCore.FLIRT.Exception;

/**
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 * $Id$
 * 
 * Exception thrown while interpreting a pattern when the pattern specifies a
 * selector not existing in the type definition of the current target.
 * For example, inner.nonExistingMethod.
 */
public class ExternalNotFoundException extends ComposestarRuntimeException 
{
	private static final long serialVersionUID = -1745635381520680402L;
    
	/**
	 * Default Constructor
	 * @roseuid 3F3652A20051
	 */
	public ExternalNotFoundException() 
	{
	}

	public ExternalNotFoundException(String internaltype, String internalname, String concern)
	{
		super("The external '"+internalname+"' of type '"+internaltype+"' in concern '"+concern+"' is not found");
	}

	/**
	 * Constructor with underlying cause.
	 */
	public ExternalNotFoundException(ExternalNotFoundException cause)
	{
		super(cause.getMessage(),cause);
	}
}
