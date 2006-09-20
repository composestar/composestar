package Composestar.RuntimeCore.FLIRT.Exception;

import Composestar.RuntimeCore.FLIRT.Interpreter.ConditionResolver;

/**
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 * $Id$
 * 
 * Exception that deals with calls to invalid conditions (that is, conditions that
 * are not defined in the enclosing FilterModule).
 * Contains information about the condition resolver used.
 */
public class InvalidConditionException extends FilterSpecificationException 
{

	private static final long serialVersionUID = -721903948631078457L;
	
	private ConditionResolver cr = null;
    
	/**
	 * Constructs a new Invalid Condition Exception
	 * @roseuid 3F3652A002D9
	 */
	public InvalidConditionException() 
	{
	}
    
	/**
	 * Constructs a new Invalid Condition Exception along with the accompanying message
	 * @param message the caption of the exception
	 * @roseuid 3F3652A00301
	 */
	public InvalidConditionException(String message) 
	{
		super(message);     
	}

	/**
	 * Constructor with underlying cause.
	 */
	public InvalidConditionException(InvalidConditionException cause)
	{
		super(cause.getMessage(),cause);
	}
    
	/**
	 * Places the condition resolver thar generated the error
	 * @param cndResolver a class that resolves the Conditions
	 * @roseuid 3F3652A00370
	 */
	public void setConditionResolver(ConditionResolver cndResolver) 
	{
		cr = cndResolver;     
	}
    
	/**
	 * Retrieves the Condition resolver that generated the error
	 * @return the condition resolver
	 * @roseuid 3F3652A10000
	 */
	public ConditionResolver getConditionResolver() 
	{
		return cr;     
	}
}
