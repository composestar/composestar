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

public class SoftPreRule extends Rule
{
	public SoftPreRule(Parameter left, Parameter right)
	{
		super(left, right);
		identifier = "pre_soft";
	}

	public void apply()
	{

	}
}
