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

public class Or extends BinaryOperator
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3187940919491607828L;

	public Or()
	{
		super();
	}
	
	public int simulateResult()
	{
		int resl = left.simulateResult();
		if (resl == RESULT_TRUE)
		{
			return RESULT_TRUE;
		}
		int resr = right.simulateResult();
		if (resr == RESULT_TRUE)
		{
			return RESULT_TRUE;
		}		
		if (resl == RESULT_FALSE && resl==RESULT_FALSE)
		{
			return RESULT_FALSE;		
		}
		return resl+resr;
	}
}
