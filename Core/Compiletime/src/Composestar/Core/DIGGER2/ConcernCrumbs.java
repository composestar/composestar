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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.FIRE2.model.Message;

/**
 * Stores the input and output crumbs for a given concern.
 * 
 * @author Michiel Hendriks
 */
public class ConcernCrumbs implements Serializable
{
	private static final long serialVersionUID = -3745374019301536089L;

	/**
	 * The concern for this instance
	 */
	protected Concern concern;

	/**
	 * Mapping of selector to crumbs for input filters
	 */
	protected Map<String, Breadcrumb> inputCrumbs;

	/**
	 * Mapping of selector to crumbs for output filters
	 */
	protected Map<String, Breadcrumb> outputCrumbs;

	public ConcernCrumbs(Concern inConcern)
	{
		concern = inConcern;
		inputCrumbs = new HashMap<String, Breadcrumb>();
		outputCrumbs = new HashMap<String, Breadcrumb>();
	}

	/**
	 * Add a breadcrumb
	 * 
	 * @param crumb
	 */
	public void addCrumb(Breadcrumb crumb)
	{
		switch (crumb.getFilterPosition())
		{
			case Output:
				// TODO:
				throw new UnsupportedOperationException();
			default:
				inputCrumbs.put(crumb.getMessage().getSelector(), crumb);
		}
	}

	/**
	 * Get a breadcrumb for a given selector
	 * 
	 * @param selector
	 * @return
	 */
	public Breadcrumb getInputCrumb(String selector)
	{
		if (inputCrumbs.containsKey(selector))
		{
			return inputCrumbs.get(selector);
		}
		// return the "+" crumb otherwise
		return inputCrumbs.get(Message.UNDISTINGUISHABLE_SELECTOR);
	}

	/**
	 * @return the input crumbs
	 */
	public Iterator<Breadcrumb> getInputCrumbs()
	{
		return inputCrumbs.values().iterator();
	}

	/**
	 * @param selector
	 * @return the output crubs for the given selector
	 */
	public Breadcrumb getOutputCrumb(String selector)
	{
		// TODO:
		throw new UnsupportedOperationException();
	}

	/**
	 * @return the output crumbs
	 */
	public Iterator<Breadcrumb> getOutputCrumbs()
	{
		return outputCrumbs.values().iterator();
	}
}
