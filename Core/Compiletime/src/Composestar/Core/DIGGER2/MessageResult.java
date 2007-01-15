/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2007 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */

package Composestar.Core.DIGGER2;

import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.MessageSelector;
import Composestar.Core.FIRE2.model.Message;

/**
 * A (possible) final result of a message.
 * 
 * @author Michiel Hendriks
 */
public class MessageResult
{
	/**
	 * The breadcrumb related to the result.
	 */
	protected Breadcrumb crumb;

	/**
	 * The trail in the breadcrumb that leads to the resulting message.
	 */
	protected Trail trail;

	public MessageResult(Breadcrumb inCrumb, Trail inTrail)
	{
		crumb = inCrumb;
		trail = inTrail;
	}

	public Breadcrumb getBreadcrumb()
	{
		return crumb;
	}

	public Trail getTrail()
	{
		return trail;
	}

	/**
	 * @return the related concern
	 */
	public Concern getConcern()
	{
		return crumb.getConcern();
	}

	/**
	 * @return the resulting selector for the message. This is either the
	 *         selector from the message of the trail or the selector of the
	 *         message of the breadcrumb (e.g. the entrance message) when the
	 *         trail's message contains a slar selector.
	 */
	public MessageSelector getSelector()
	{
		Message msg = trail.getResultMessage();
		if (msg != null)
		{
			MessageSelector result = msg.getSelector();
			if (result.equals(Message.STAR_SELECTOR))
			{
				return crumb.getMessage().getSelector();
			}
			return result;
		}
		return null;
	}
}
