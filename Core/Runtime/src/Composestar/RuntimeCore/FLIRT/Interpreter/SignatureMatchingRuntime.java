package Composestar.RuntimeCore.FLIRT.Interpreter;

import java.util.Dictionary;

import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.CpsProgramRepository.Signature;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.MessageSelector;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Target;
import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.RuntimeCore.FLIRT.Message.Message;
import Composestar.RuntimeCore.Utils.Debug;
import Composestar.RuntimeCore.Utils.Invoker;

/**
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente. Licensed under LGPL v2.1 or (at your
 * option) any later version. [http://www.fsf.org/copyleft/lgpl.html] $Id:
 * SignatureMatchingRuntime.java 2072 2006-10-15 12:44:56Z elmuerte $
 */
public class SignatureMatchingRuntime extends MatchingTypeRuntime
{

	/**
	 * @roseuid 40DD688401A9
	 */
	public SignatureMatchingRuntime()
	{

	}

	/**
	 * @param m
	 * @param context
	 * @return boolean
	 * @roseuid 40DD96900127
	 */
	public boolean interpret(Message m, Dictionary context)
	{
		// TODO WM: Iterate over messages
		if (Debug.SHOULD_DEBUG)
		{
			Debug.out(Debug.MODE_INFORMATION, "FLIRT", "\t\t\tInterpreting SIGNATURE MatchingPartRuntime '"
					+ m.getSelector() + "'...");
		}
		String ct_selector = ((MessageSelector) this.parentMatchingPart.theSelectorRuntime.getReference()).getName();
		if (Debug.SHOULD_DEBUG)
		{
			Debug.out(Debug.MODE_INFORMATION, "FLIRT", "\t\t\t\tResolving selector '" + ct_selector + "'...");
		}
		String message_selector = m.getSelector();

		String ct_target = ((Target) this.parentMatchingPart.theTargetRuntime.getReference()).getName();
		// if(Debug.SHOULD_DEBUG)
		// Debug.out(Debug.MODE_DEBUG,"FLIRT","\t\t\t\tFound target:
		// "+ct_target);

		// <*.*> and <*.something> always match a signature.
		if (ct_target.equals("*"))
		{
			return true;
		}

		// If needed we do an inner replacement.
		if (ct_target.equals(Target.INNER))
		{
			if (Debug.SHOULD_DEBUG)
			{
				Debug.out(Debug.MODE_INFORMATION, "FLIRT", "\t\t\t\tResolving target '" + m.getInner().getClass()
						+ "'...");
			}
			// checking if {ct_target | inner} has the method message_selector.
			return Invoker.getInstance().objectHasMethod(m.getInner(), message_selector, context);
		}
		else
		{
			return targetHasMethod(ct_target, message_selector, context);
		}
	}

	protected boolean targetHasMethod(String ct_target, String m_selector, Dictionary context)
	{
		DataStore ds = DataStore.instance();

		Concern c = (Concern) ds.getObjectByID(ct_target);

		if (c == null)
		{
			FilterModuleRuntime fmr = (FilterModuleRuntime) context.get("ConditionResolver");
			Object internal = fmr.getInternal(ct_target);
			if (internal != null)
			{
				c = (Concern) ds.getObjectByID(internal.getClass().getName());
				if (Debug.SHOULD_DEBUG)
				{
					Debug.out(Debug.MODE_INFORMATION, "FLIRT", "\t\t\t\tFound internal to resolve signature for: "
							+ c.getName());
				}
			}
			else
			{
				Object external = fmr.getExternal(ct_target);
				if (external != null)
				{
					c = (Concern) ds.getObjectByID(external.getClass().getName());
					if (Debug.SHOULD_DEBUG)
					{
						Debug.out(Debug.MODE_INFORMATION, "FLIRT", "\t\t\t\tFound external to resolve signature for: "
								+ c.getName());
					}
				}
			}
		}
		else
		{
			if (Debug.SHOULD_DEBUG)
			{
				Debug.out(Debug.MODE_INFORMATION, "FLIRT", "\t\t\t\tFound concern to resolve signature for: "
						+ c.getName());
			}
		}

		if (c != null) // Strange, if we got no information.
		{
			Signature sig = c.getSignature();
			if (sig != null)
			{
				if (Debug.SHOULD_DEBUG)
				{
					Debug.out(Debug.MODE_INFORMATION, "FLIRT", "\t\t\t\tMessage matches the signature: "
							+ sig.hasMethod(m_selector));
				}
				return (sig.hasMethod(m_selector));
			}
		}
		return false;
	}
}
