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

	/**
	 * The resulting selector. This can either be the selector of the resulting
	 * message in the trail (when it's substituted) or the selector of the
	 * crumb. In case the crumb has an undistinguishable selector (only used
	 * when the dispatch graph is created in mode 0) the initial selector is
	 * used.
	 */
	protected String selector;

	/**
	 * Default constructor
	 * 
	 * @param inCrumb the breadcrumb this result relates to
	 * @param inTrail the trail (of the breadcrumb) that is followed
	 * @param initialSelector fallback selector used in case of star and
	 *            undistinguishable selector usage
	 */
	public MessageResult(Breadcrumb inCrumb, Trail inTrail, String initialSelector)
	{
		crumb = inCrumb;
		trail = inTrail;

		selector = trail.getResultMessage().getSelector();
		if (Message.UNDISTINGUISHABLE_SELECTOR.equals(selector))
		{
			selector = initialSelector;
		}
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
	 * @return the resulting concern. In case of a dispatch to inner the concern
	 *         of the breadcrumb otherwise it's the target of the concern.
	 */
	public Concern getConcern()
	{
		if (trail.getTargetConcern() != null)
		{
			return trail.getTargetConcern();
		}
		return crumb.getConcern();
	}

	/**
	 * @see MessageResult#selector
	 * @return the resulting selector for the message.
	 */
	public String getSelector()
	{
		return selector;
	}
}
