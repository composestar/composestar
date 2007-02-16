package Composestar.RuntimeCore.FLIRT.Interpreter;

import java.util.Dictionary;

import Composestar.RuntimeCore.FLIRT.Message.Message;

/**
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente. Licensed under LGPL v2.1 or (at your
 * option) any later version. [http://www.fsf.org/copyleft/lgpl.html] $Id:
 * MatchingTypeRuntime.java 1518 2006-09-20 13:13:30Z reddog33hummer $
 */
public abstract class MatchingTypeRuntime extends ReferenceEntityRuntime
{

	public MatchingPartRuntime parentMatchingPart = null;

	/**
	 * @roseuid 40DD688500C4
	 */
	public MatchingTypeRuntime()
	{

	}

	/**
	 * @param m
	 * @param context
	 * @return boolean
	 * @roseuid 40DD688500CE
	 */
	public boolean interpret(Message m, Dictionary context)
	{
		return true;
	}
}
