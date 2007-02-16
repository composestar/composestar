package Composestar.RuntimeCore.FLIRT.Filtertypes;

import java.util.Dictionary;

import Composestar.RuntimeCore.FLIRT.Actions.ComposeStarAction;
import Composestar.RuntimeCore.FLIRT.Message.MessageList;

/**
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente. Licensed under LGPL v2.1 or (at your
 * option) any later version. [http://www.fsf.org/copyleft/lgpl.html] $Id:
 * Substitution.java 1518 2006-09-20 13:13:30Z reddog33hummer $
 */
public class Substitution extends FilterTypeRuntime
{

	/**
	 * @param m
	 * @param context
	 * @return Composestar.Runtime.FLIRT.actions.ComposeStarAction
	 * @roseuid 40DFE17B02F2
	 */
	public ComposeStarAction acceptAction(MessageList originalMessage, MessageList modifiedMessage, Dictionary context)
	{
		return null;
	}

	/**
	 * @param m
	 * @param context
	 * @return Composestar.Runtime.FLIRT.actions.ComposeStarAction
	 * @roseuid 40DFE17B0324
	 */
	public ComposeStarAction rejectAction(MessageList originalMessage, MessageList modifiedMessage, Dictionary context)
	{
		return null;
	}

	/**
	 * @return boolean
	 * @roseuid 40F2A8E90274
	 */
	public boolean shouldContinueAfterAccepting()
	{
		return true;
	}
}
