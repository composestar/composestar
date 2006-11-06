package Composestar.RuntimeCore.FLIRT.Filtertypes;

import Composestar.RuntimeCore.FLIRT.Actions.ComposeStarAction;
import Composestar.RuntimeCore.FLIRT.Message.MessageList;

import java.util.Dictionary;

/**
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente. Licensed under LGPL v2.1 or (at your
 * option) any later version. [http://www.fsf.org/copyleft/lgpl.html] $Id:
 * Wait.java 1518 2006-09-20 13:13:30Z reddog33hummer $ Not yet implemented!
 */
public class Wait extends FilterTypeRuntime
{

	/**
	 * @param m
	 * @param context
	 * @return Composestar.Runtime.FLIRT.actions.ComposeStarAction
	 * @roseuid 40DFE17B03A6
	 */
	public ComposeStarAction acceptAction(MessageList originalMessage, MessageList modifiedMessage, Dictionary context)
	{
		return null;
	}

	/**
	 * @param m
	 * @param context
	 * @return Composestar.Runtime.FLIRT.actions.ComposeStarAction
	 * @roseuid 40DFE17B03CE
	 */
	public ComposeStarAction rejectAction(MessageList originalMessage, MessageList modifiedMessage, Dictionary context)
	{
		return null;
	}

	/**
	 * @return boolean
	 * @roseuid 40F2A8E902F6
	 */
	public boolean shouldContinueAfterAccepting()
	{
		return true;
	}
}
