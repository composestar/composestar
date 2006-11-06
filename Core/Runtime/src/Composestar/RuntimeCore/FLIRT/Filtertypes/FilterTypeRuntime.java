package Composestar.RuntimeCore.FLIRT.Filtertypes;

import Composestar.RuntimeCore.FLIRT.Message.MessageList;
import Composestar.RuntimeCore.FLIRT.Message.Message;
import Composestar.RuntimeCore.FLIRT.Interpreter.FilterRuntime;
import Composestar.RuntimeCore.FLIRT.Actions.ComposeStarAction;

import java.util.Dictionary;

/**
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente. Licensed under LGPL v2.1 or (at your
 * option) any later version. [http://www.fsf.org/copyleft/lgpl.html] $Id:
 * FilterTypeRuntime.java 2032 2006-10-12 15:08:13Z reddog33hummer $
 */
public abstract class FilterTypeRuntime
{
	private FilterRuntime filter;

	/**
	 * @roseuid 40F2ADDE0373
	 */
	public FilterTypeRuntime()
	{

	}

	/**
	 * @param fr
	 * @roseuid 40F29D9001D6
	 */
	public FilterTypeRuntime(FilterRuntime fr)
	{
		this.filter = fr;
	}

	/**
	 * @param m
	 * @param context
	 * @return Composestar.Runtime.FLIRT.actions.ComposeStarAction
	 * @roseuid 40DFE5A50309
	 * @param originalMessage
	 * @param modifiedMessage
	 */
	public abstract ComposeStarAction acceptAction(MessageList originalMessage, MessageList modifiedMessage,
			Dictionary context);

	/**
	 * @param m
	 * @param context
	 * @return Composestar.Runtime.FLIRT.actions.ComposeStarAction
	 * @roseuid 40DFE5A80399
	 * @param modifiedMessage
	 * @param originalMessage
	 */
	public abstract ComposeStarAction rejectAction(MessageList originalMessage, MessageList modifiedMessage,
			Dictionary context);

	/**
	 * @return boolean
	 * @roseuid 40F2A80603CC
	 */
	public abstract boolean shouldContinueAfterAccepting();

	/**
	 * @return Composestar.Runtime.FLIRT.interpreter.FilterRuntime
	 * @roseuid 40F2AE0600F9
	 */
	public FilterRuntime getFilter()
	{
		return this.filter;
	}

	protected void replaceInner(MessageList originalMessages, MessageList modifiedMessages)
	{
		java.util.LinkedList mod = modifiedMessages.getMessages();

		for (int i = 0; i < mod.size(); i++)
		{
			Message modifiedMessage = (Message) mod.get(i);

			if (modifiedMessage.getTarget().equals("inner"))
			{
				modifiedMessage.setTarget(originalMessages.getInner());
			}
		}

	}

	protected void replaceWildcards(MessageList originalMessages, MessageList modifiedMessages)
	{
		java.util.LinkedList org = originalMessages.getMessages();
		java.util.LinkedList mod = modifiedMessages.getMessages();

		if (org.size() != mod.size() && modifiedMessages.hasWildcards()) throw new Composestar.RuntimeCore.FLIRT.Exception.ComposestarRuntimeException(
				"Ambiguous wildcards in selector");

		if (modifiedMessages.hasWildcards())
		{
			// so org.size == mod.size
			for (int i = 0; i < org.size(); i++)
			{
				Message originalMessage = (Message) org.get(i);
				Message modifiedMessage = (Message) mod.get(i);

				if (modifiedMessage.getTarget().equals("*"))
				{
					modifiedMessage.setTarget(originalMessage.getTarget());
				}

				if (modifiedMessage.getSelector().equals("*"))
				{
					modifiedMessage.setSelector(originalMessage.getSelector());
				}
			}
		}

		for (int i = 0; i < mod.size(); i++)
		{
			Message modifiedMessage = (Message) mod.get(i);
			if (modifiedMessage.getTarget() instanceof String
					&& modifiedMessage.getInternal((String) modifiedMessage.getTarget()) != null)
			{
				modifiedMessage.setTarget(modifiedMessage.getInternal((String) modifiedMessage.getTarget()));
			}
			else if (modifiedMessage.getTarget() instanceof String
					&& modifiedMessage.getExternal((String) modifiedMessage.getTarget()) != null)
			{
				modifiedMessage.setTarget(modifiedMessage.getExternal((String) modifiedMessage.getTarget()));
			}
		}
	}
}
