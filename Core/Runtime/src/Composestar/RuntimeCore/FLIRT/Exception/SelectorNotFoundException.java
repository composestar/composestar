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
public class SelectorNotFoundException extends InvalidPatternExpressionException 
{

	private static final long serialVersionUID = 1930274399872802346L;

	/**
	 * Name of the method to which the message was directed
	 */
	private String selector = null;
    
	/**
	 * Default Constructor
	 * @roseuid 3F3652A20051
	 */
	public SelectorNotFoundException() 
	{
	}
    
	/**
	 * @inheritDoc
	 * @param message
	 * @roseuid 3F3652A20083
	 */
	public SelectorNotFoundException(String message) 
	{
		super(message);     
	}

	public SelectorNotFoundException(SelectorNotFoundException cause)
	{
		super(cause.getMessage(),cause);
	}

	public String getSelector() 
	{
		return selector;
	}

	public void setSelector(String selector) 
	{
		this.selector = selector;
	}
}
