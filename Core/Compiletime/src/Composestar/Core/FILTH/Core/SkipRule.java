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
public abstract class SkipRule extends Rule
{
	protected Parameter newValue;

	public SkipRule(Parameter left, Parameter right, Parameter innewValue)
	{
		super(left, right);
		newValue = innewValue;
	}

	public Parameter getNewValue()
	{
		return newValue;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Rule#apply()
	 */
	@Override
	public abstract void apply();
	// TODO Auto-generated method stub

}
