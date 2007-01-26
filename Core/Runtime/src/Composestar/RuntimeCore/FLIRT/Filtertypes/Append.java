package Composestar.RuntimeCore.FLIRT.Filtertypes;

import java.util.Dictionary;

import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Target;
import Composestar.RuntimeCore.FLIRT.Actions.AppendAction;
import Composestar.RuntimeCore.FLIRT.Actions.ComposeStarAction;
import Composestar.RuntimeCore.FLIRT.Actions.ContinueToNextFilterAction;
import Composestar.RuntimeCore.FLIRT.Message.MessageList;

/**
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente. Licensed under LGPL v2.1 or (at your
 * option) any later version. [http://www.fsf.org/copyleft/lgpl.html] $Id:
 * Append.java 2012 2006-10-12 12:56:38Z reddog33hummer $ Models the Meta filter
 * If a message is accepted, it is reified and offered to a method defined in
 * the filter specification. if the message is rejected, it is passed on to the
 * next filter.
 */
public class Append extends FilterTypeRuntime
{

	/**
	 * @param context
	 * @return Composestar.Runtime.FLIRT.actions.ComposeStarAction
	 * @roseuid 40DFE17B018A
	 */
	public ComposeStarAction acceptAction(MessageList originalMessage, MessageList modifiedMessage, Dictionary context)
	{
		/*
		 * if(modifiedMessage.getTarget().equals("*")) {
		 * modifiedMessage.setTarget(originalMessage.getTarget()); } else
		 * if(modifiedMessage.getTarget().equals(Target.INNER)) {
		 * modifiedMessage.setTarget(originalMessage.getInner()); } else
		 * if(modifiedMessage.getTarget() instanceof String &&
		 * originalMessage.getInternal((String)modifiedMessage.getTarget()) !=
		 * null) {
		 * modifiedMessage.setTarget(originalMessage.getInternal((String)modifiedMessage.getTarget())); }
		 * else if(modifiedMessage.getTarget() instanceof String &&
		 * originalMessage.getExternal((String)modifiedMessage.getTarget()) !=
		 * null) {
		 * modifiedMessage.setTarget(originalMessage.getExternal((String)modifiedMessage.getTarget())); }
		 * if(modifiedMessage.getSelector().equals("*")) {
		 * modifiedMessage.setSelector(originalMessage.getSelector()); }
		 */
		replaceInner(originalMessage, modifiedMessage);
		replaceWildcards(originalMessage, modifiedMessage);
		return new AppendAction(originalMessage, modifiedMessage, originalMessage.getArguments());
	}

	/**
	 * @param context
	 * @return Composestar.Runtime.FLIRT.actions.ComposeStarAction
	 * @roseuid 40DFE17B01BC
	 */
	public ComposeStarAction rejectAction(MessageList originalMessage, MessageList modifiedMessage, Dictionary context)
	{
		return new ContinueToNextFilterAction(originalMessage, false);
	}

	/**
	 * @return boolean
	 * @roseuid 40F2A8E9016F
	 */
	public boolean shouldContinueAfterAccepting()
	{
		return true;
	}
}
