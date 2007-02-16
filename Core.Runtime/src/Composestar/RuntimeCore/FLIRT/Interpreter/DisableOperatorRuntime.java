package Composestar.RuntimeCore.FLIRT.Interpreter;

import java.util.Dictionary;

import Composestar.RuntimeCore.FLIRT.Message.MessageList;

/**
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente. Licensed under LGPL v2.1 or (at your
 * option) any later version. [http://www.fsf.org/copyleft/lgpl.html] $Id:
 * DisableOperatorRuntime.java 1518 2006-09-20 13:13:30Z reddog33hummer $
 */
public class DisableOperatorRuntime extends EnableOperatorTypeRuntime implements Interpretable
{

	/**
	 * @roseuid 40DD68840267
	 */
	public DisableOperatorRuntime()
	{

	}

	/**
	 * @param m
	 * @param context
	 * @return boolean
	 * @roseuid 40DD968803CE
	 */
	public boolean interpret(MessageList m, Dictionary context)
	{
		return true;
	}
}
