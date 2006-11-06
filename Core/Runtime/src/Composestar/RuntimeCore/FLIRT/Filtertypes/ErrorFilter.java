package Composestar.RuntimeCore.FLIRT.Filtertypes;

import Composestar.RuntimeCore.FLIRT.Message.MessageList;
import Composestar.RuntimeCore.FLIRT.Actions.ErrorAction;
import Composestar.RuntimeCore.FLIRT.Exception.ErrorFilterException;
import Composestar.RuntimeCore.FLIRT.Actions.ContinueToNextFilterAction;
import Composestar.RuntimeCore.FLIRT.Actions.ComposeStarAction;

import java.util.Dictionary;

/**
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente. Licensed under LGPL v2.1 or (at your
 * option) any later version. [http://www.fsf.org/copyleft/lgpl.html] $Id:
 * ErrorFilter.java 2012 2006-10-12 12:56:38Z reddog33hummer $ Model the error
 * filter This filter will create an ErrorAction when rejecting a message. When
 * executed, the error action is to throw an exception
 */
public class ErrorFilter extends FilterTypeRuntime
{

	/**
	 * @param context
	 * @return Composestar.Runtime.FLIRT.actions.ComposeStarAction
	 * @roseuid 40DFE39F01E2
	 */
	public ComposeStarAction acceptAction(MessageList originalMessage, MessageList modifiedMessage, Dictionary context)
	{
		return new ContinueToNextFilterAction(originalMessage, true);
	}

	/**
	 * @param context
	 * @return Composestar.Runtime.FLIRT.actions.ComposeStarAction
	 * @roseuid 40DFE39F0214
	 */
	public ComposeStarAction rejectAction(MessageList originalMessage, MessageList modifiedMessage, Dictionary context)
	{
		return new ErrorAction(originalMessage, true, new ErrorFilterException("Compose* error filter exception"));
	}

	/**
	 * @return boolean
	 * @roseuid 40F2A8E900B1
	 */
	public boolean shouldContinueAfterAccepting()
	{
		return true;
	}
}
