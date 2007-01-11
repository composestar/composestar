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

	protected Map inputCrumbs;

	protected Map outputCrumbs;

	public ConcernCrumbs(Concern inConcern)
	{
		concern = inConcern;
		inputCrumbs = new HashMap();
		outputCrumbs = new HashMap();
	}

	public void addCrumb(Breadcrumb crumb)
	{
		switch (crumb.getFilterPosition())
		{
			case FireModel.OUTPUT_FILTERS:
				// TODO:
				throw new UnsupportedOperationException();
			default:
				inputCrumbs.put(crumb.getMessage().getSelector().getName(), crumb);
		}
	}
	
	public Breadcrumb getInputCrumb(String selector)
	{
		if (inputCrumbs.containsKey(selector))
		{
			return (Breadcrumb) inputCrumbs.get(selector);
		}
		// return the "+" crumb otherwise
		return (Breadcrumb) inputCrumbs.get(Message.UNDISTINGUISHABLE_SELECTOR.getName());
	}
	
	public Iterator getInputCrumbs()
	{
		return inputCrumbs.values().iterator();
	}
	
	public Breadcrumb getOutputCrumb(String selector)
	{
		//TODO:
		throw new UnsupportedOperationException();
	}
	
	public Iterator getOutputCrumbs()
	{
		return outputCrumbs.values().iterator();
	}
}
