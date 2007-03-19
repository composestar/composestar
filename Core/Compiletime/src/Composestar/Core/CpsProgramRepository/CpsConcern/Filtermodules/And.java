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

public class And extends BinaryOperator
{
	private static final long serialVersionUID = -7814675298436028745L;

	public And()
	{
		super();
	}

	public int simulateResult()
	{
		int resl = left.simulateResult();
		if (resl == RESULT_FALSE)
		{
			return RESULT_FALSE;
		}
		int resr = right.simulateResult();
		if (resr == RESULT_FALSE)
		{
			return RESULT_FALSE;
		}
		return resl + resr;
	}

	public String asSourceCode()
	{
		return "(" + left.asSourceCode() + " & " + right.asSourceCode() + ")";
	}
}
