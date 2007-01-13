/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2007 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */

package Composestar.Core.DIGGER2;

import java.util.List;

/**
 * Exception thrown in case of a recursive filter definition.
 * 
 * @author Michiel Hendriks
 */
public class RecursiveFilterException extends Exception
{
	protected int vars;

	protected List trace;

	/**
	 * Unconditional recursive filter definition.
	 * 
	 * @param inMessage
	 * @param inTrace
	 */
	public RecursiveFilterException(String inMessage, List inTrace)
	{
		super(inMessage);
		trace = inTrace;
	}

	/**
	 * Conditional recursive filter definition that depends inVars conditionals
	 * 
	 * @param inMessage
	 * @param inTrace
	 * @param inVars
	 */
	public RecursiveFilterException(String inMessage, List inTrace, int inVars)
	{
		this(inMessage, inTrace);
		vars = inVars;
	}

	/**
	 * Return the message trace.
	 * 
	 * @return
	 */
	public List getTrace()
	{
		return trace;
	}

	/**
	 * Return the number of (estimated) conditions the recursion depends on.
	 * 
	 * @return
	 */
	public int numVars()
	{
		return vars;
	}
}
