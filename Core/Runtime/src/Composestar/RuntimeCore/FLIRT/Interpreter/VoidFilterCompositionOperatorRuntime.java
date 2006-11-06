package Composestar.RuntimeCore.FLIRT.Interpreter;

import Composestar.RuntimeCore.FLIRT.Message.MessageList;

import java.util.Dictionary;

/**
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente. Licensed under LGPL v2.1 or (at your
 * option) any later version. [http://www.fsf.org/copyleft/lgpl.html] $Id:
 * VoidFilterCompositionOperatorRuntime.java 1518 2006-09-20 13:13:30Z
 * reddog33hummer $
 */
public class VoidFilterCompositionOperatorRuntime extends FilterCompositionOperatorRuntime implements Interpretable
{
	private FilterRuntime previous;

	/**
	 * @roseuid 40DDDE5200E4
	 */
	public VoidFilterCompositionOperatorRuntime()
	{

	}

	/**
	 * @param previous
	 * @roseuid 40DD59C5007E
	 */
	public VoidFilterCompositionOperatorRuntime(FilterRuntime previous)
	{
		this.previous = previous;
	}

	/**
	 * @param m
	 * @param context
	 * @return boolean
	 * @roseuid 40DD96AB0090
	 */
	public boolean interpret(MessageList m, Dictionary context)
	{
		return (this.previous.interpret(m, context));
	}
}
