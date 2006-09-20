package Composestar.RuntimeCore.FLIRT.Exception;

/**
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 * $Id$
 * 
 * Exception thrown when a pattern calls for a non-existing target-selector pair
 * within the enclosing filter module at the time of interpretation.
 * For example, *.messageNotImplementedByInnerInternalOrExternal.
 */
public class TargetNotFoundException extends InvalidPatternExpressionException 
{

	private static final long serialVersionUID = -3068254562496318841L;
	/**
	 * Name of the internal, external or pseudo variable that was not found
	 */
	private String target = null;
    
	/**
	 * @inheritDoc
	 * @param message
	 * @roseuid 3F3652A2017E
	 */
	public TargetNotFoundException(String message) 
	{
	}
    
	/**
	 * Default constructor
	 * @roseuid 3F3652A20156
	 */
	public TargetNotFoundException() 
	{
	}

	/**
	 * Constructor with underlying cause.
	 */
	public TargetNotFoundException(TargetNotFoundException cause)
	{
		super(cause.getMessage(),cause);
	}

	public String getTarget() 
	{
		return target;
	}

	public void setTarget(String target) 
	{
		this.target = target;
	}
}
