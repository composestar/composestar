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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.FIRE2.model.FireModel;
import Composestar.Core.FIRE2.model.Message;

/**
 * Stores the input and output crumbs
 * 
 * @author Michiel Hendriks
 */
public class ConcernCrumbs
{
	protected Concern concern;

	protected Map<String, Breadcrumb> inputCrumbs;

	protected Map<String, Breadcrumb> outputCrumbs;

	public ConcernCrumbs(Concern inConcern)
	{
		concern = inConcern;
		inputCrumbs = new HashMap<String, Breadcrumb>();
		outputCrumbs = new HashMap<String, Breadcrumb>();
	}

	public void addCrumb(Breadcrumb crumb)
	{
		switch (crumb.getFilterPosition())
		{
			case FireModel.OUTPUT_FILTERS:
				// TODO:
				throw new UnsupportedOperationException();
			default:
				inputCrumbs.put(crumb.getMessage().getSelector(), crumb);
		}
	}

	public Breadcrumb getInputCrumb(String selector)
	{
		if (inputCrumbs.containsKey(selector))
		{
			return inputCrumbs.get(selector);
		}
		// return the "+" crumb otherwise
		return inputCrumbs.get(Message.UNDISTINGUISHABLE_SELECTOR);
	}

	public Iterator<Breadcrumb> getInputCrumbs()
	{
		return inputCrumbs.values().iterator();
	}

	public Breadcrumb getOutputCrumb(String selector)
	{
		// TODO:
		throw new UnsupportedOperationException();
	}

	public Iterator<Breadcrumb> getOutputCrumbs()
	{
		return outputCrumbs.values().iterator();
	}
}
