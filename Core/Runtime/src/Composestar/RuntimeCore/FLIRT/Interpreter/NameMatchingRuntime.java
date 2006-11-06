package Composestar.RuntimeCore.FLIRT.Interpreter;

import java.util.Dictionary;

import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.MessageSelector;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Target;
import Composestar.RuntimeCore.FLIRT.Message.Message;
import Composestar.RuntimeCore.Utils.Debug;

/**
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente. Licensed under LGPL v2.1 or (at your
 * option) any later version. [http://www.fsf.org/copyleft/lgpl.html] $Id:
 * NameMatchingRuntime.java 2072 2006-10-15 12:44:56Z elmuerte $
 */
public class NameMatchingRuntime extends MatchingTypeRuntime
{

	/**
	 * @roseuid 40DD6884020D
	 */
	public NameMatchingRuntime()
	{

	}

	/**
	 * @param m
	 * @param context
	 * @return boolean
	 * @roseuid 40DD96920333
	 */
	public boolean interpret(Message m, Dictionary context)
	{
		// TODO WM: Iterate over messages or at least over internals/externals
		if (Debug.SHOULD_DEBUG)
		{
			Debug.out(Debug.MODE_INFORMATION, "FLIRT", "\t\t\tInterpreting NAME MatchingPartRuntime...");
		}
		resolveTarget(m.getInternals(), m.getExternals(), m, context);
		return resolveSelector(m, context);
	}

	/**
	 * @param m
	 * @param context
	 * @return boolean
	 * @roseuid 40DDFDF702CD
	 */
	public boolean resolveSelector(Message m, Dictionary context)
	{
		String ct_selector = ((MessageSelector) this.parentMatchingPart.theSelectorRuntime.getReference()).getName();
		if (Debug.SHOULD_DEBUG)
		{
			Debug.out(Debug.MODE_INFORMATION, "FLIRT", "\t\t\t\tResolving selector '" + ct_selector + "'...");
		}
		String message_selector = m.getSelector();

		Object target = context.get("target");
		if (Debug.SHOULD_DEBUG)
		{
			Debug.out(Debug.MODE_INFORMATION, "FLIRT", "\t\t\t\tTarget: " + target);
		}

		if (ct_selector.equals("*")) // *.*
		{
			// fixme Olaf:Shouldn't [dontcare.*] be [*.*]?
			if (Debug.SHOULD_DEBUG)
			{
				Debug.out(Debug.MODE_INFORMATION, "FLIRT", "\t\t\t\tFound '[dontcare.*]'.");
			}
			return true;
		}

		// *.something, match to the current
		if (Debug.SHOULD_DEBUG)
		{
			Debug.out(Debug.MODE_INFORMATION, "FLIRT", "\t\t\t\tFound '[dontcare.something]'.");
		}
		return message_selector.equals(ct_selector);
	}

	/**
	 * @param internals
	 * @param externals
	 * @param m
	 * @param context
	 * @roseuid 40DDFE10002A
	 */
	public void resolveTarget(Dictionary internals, Dictionary externals, Message m, Dictionary context)
	{
		String target = ((Target) this.parentMatchingPart.theTargetRuntime.getReference()).getName();
		if (Debug.SHOULD_DEBUG)
		{
			Debug.out(Debug.MODE_INFORMATION, "FLIRT", "\t\t\t\tResolving target '" + target + "'...");
		}

		if (target.equals("*"))
		{
			if (Debug.SHOULD_DEBUG)
			{
				Debug.out(Debug.MODE_INFORMATION, "FLIRT", "\t\t\t\tFound '*'.");
			}
		}
		else if (target.equalsIgnoreCase("inner"))
		{
			if (Debug.SHOULD_DEBUG)
			{
				Debug.out(Debug.MODE_INFORMATION, "FLIRT", "\t\t\t\tFound 'inner'.");
			}
			context.put("target", m.getInner());
		}
		else if (target.equals(m.getTarget().getClass().getName()))
		{
			if (Debug.SHOULD_DEBUG)
			{
				Debug.out(Debug.MODE_INFORMATION, "FLIRT", "\t\t\t\tFound target '" + m.getTarget().getClass() + "'.");
			}
			context.put("target", m.getTarget());
		}
	}
}
