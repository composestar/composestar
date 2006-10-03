/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2006 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */

package Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules;

/**
 * Base class for literal elements in a condition expression. This class is
 * called CondLiteral and not ConditionLiteral in order to prevent confusion
 * with an old class with the same name.
 * 
 * @author Michiel Hendriks
 */
public abstract class CondLiteral extends ConditionExpression
{
	public CondLiteral()
	{
		super();
	}

	public boolean isBinary()
	{
		return false;
	}

	public boolean isUnary()
	{
		return false;
	}

	public boolean isLiteral()
	{
		return true;
	}

}
