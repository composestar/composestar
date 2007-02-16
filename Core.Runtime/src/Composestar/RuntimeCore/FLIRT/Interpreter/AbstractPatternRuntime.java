package Composestar.RuntimeCore.FLIRT.Interpreter;

import java.util.Dictionary;

import Composestar.RuntimeCore.FLIRT.Message.Message;

/**
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente. Licensed under LGPL v2.1 or (at your
 * option) any later version. [http://www.fsf.org/copyleft/lgpl.html] $Id:
 * AbstractPatternRuntime.java 1518 2006-09-20 13:13:30Z reddog33hummer $
 */
public abstract class AbstractPatternRuntime extends ReferenceEntityRuntime
{
	public TargetRuntime theTargetRuntime;

	public SelectorRuntime theSelectorRuntime;

	/**
	 * @roseuid 40DD6885013C
	 */
	public AbstractPatternRuntime()
	{

	}

	/**
	 * @param m
	 * @param context
	 * @return boolean
	 * @roseuid 40DD68850146
	 */
	public boolean interpret(Message m, Dictionary context)
	{
		return true;
	}
}
