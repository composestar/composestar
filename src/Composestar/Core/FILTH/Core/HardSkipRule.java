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
public class HardSkipRule extends SkipRule
{

	public HardSkipRule(Parameter left, Parameter right, Parameter newValue)
	{
		super(left, right, newValue);
		identifier = "skip_hard";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Rule#apply()
	 */
	public void apply()
	{

		// System.out.print(" *applying: HardSkip
		// <"+(Action)_left+","+(Action)_right+">*\n");
		if ((left.evaluate() != null) && (left.evaluate().booleanValue()))
		{
			((Action) right).setExecutable(false);
			((Action) right).setReturnValue(newValue.evaluate());
			((Action) right).setExecuted();
			// System.out.print(" * skipping>> "+(Action)_right+" with
			// "+_newValue.evaluate()+"*\n");

		}

	}

}
