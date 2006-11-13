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
public class HardPreRule extends Rule
{

	public HardPreRule(Parameter left, Parameter right)
	{
		super(left, right);
		identifier = "pre_hard";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Rule#apply()
	 */
	public void apply()
	{
		// System.out.print(" *applying: HardPre
		// <"+(Action)_left+","+(Action)_right+">*\n");
		if (!((Action) left).isExecuted())
		{
			((Action) right).setExecutable(false);
		}
	}

}
