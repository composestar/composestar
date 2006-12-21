/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */
package Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules;

/**
 * The Not operator
 * 
 * @author Michiel Hendriks
 */
public class Not extends UnaryOperator
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3703389311192256887L;

	public Not()
	{
		super();
	}
	
	public int simulateResult()
	{
		int res = operand.simulateResult();
		switch (res)
		{
			case RESULT_FALSE:
				return RESULT_TRUE;
			case RESULT_TRUE:
				return RESULT_FALSE;
			default:
				return res;
		}
	}
}
