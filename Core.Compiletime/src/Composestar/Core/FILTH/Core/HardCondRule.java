/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003-2006 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */
package Composestar.Core.FILTH.Core;

/**
 * @author nagyist
 */

public class HardCondRule extends Rule
{
	public HardCondRule(Parameter left, Parameter right)
	{
		super(left, right);
		identifier = "cond_hard";
	}

	public void apply()
	{
		// System.out.print(" *applying: HardCond
		// <"+(Action)_left+","+(Action)_right+">*\n");
		if ((left.evaluate() == null) || (!((Action) left).isExecuted()) || (!left.evaluate()))
		{
			((Action) right).setExecutable(false);
		}
	}
}
