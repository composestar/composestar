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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.FIRE2.model.Message;

/**
 * Keeps a record of all the breadcrumbs
 * 
 * @author Michiel Hendriks
 */
public class DispatchGraph
{
	public static final String REPOSITORY_KEY = "DispatchGraph";

	/**
	 * Contains a map for each concern. The map contains the breadcrumbs with
	 * the selector (as string) as the key.
	 */
	protected Map crumbs;

	public DispatchGraph()
	{
		crumbs = new HashMap();
	}

	public void addCrumb(Breadcrumb crumb)
	{
		Map concernCrumbs = (Map) crumbs.get(crumb.getConcern());
		if (concernCrumbs == null)
		{
			concernCrumbs = new HashMap();
			crumbs.put(crumb.getConcern(), concernCrumbs);
		}
		concernCrumbs.put(crumb.getMessage().getSelector().getName(), crumb);
	}
	
	public Iterator getCrumbs(Concern concern)
	{
		Map concernCrumbs = (Map) crumbs.get(concern);
		if (concernCrumbs != null)
		{
			return concernCrumbs.values().iterator();
		}
		return null;
	}

	public Breadcrumb getCrumb(Concern concern, Message msg)
	{
		if (concern == null)
		{
			return null;
		}
		if ((msg == null) || (msg.getSelector() == null))
		{
			return null;
		}		
		return getCrumb(concern, msg.getSelector().getName());
	}

	public Breadcrumb getCrumb(Concern concern, String selector)
	{
		if (concern == null)
		{
			return null;
		}
		Map concernCrumbs = (Map) crumbs.get(concern);
		if (concernCrumbs != null)
		{
			if (concernCrumbs.containsKey(selector))
			{
				return (Breadcrumb) concernCrumbs.get(selector);
			}
			else if (concernCrumbs.containsKey(Message.UNDISTINGUISHABLE_SELECTOR.getName()))
			{
				return (Breadcrumb) concernCrumbs.get(selector);
			}
		}
		return null;
	}

	public Iterator getAllCrumbs()
	{
		List l = new ArrayList();
		Iterator it = crumbs.values().iterator();
		while (it.hasNext())
		{
			Map m = (Map) it.next();
			l.addAll(m.values());
		}
		return l.iterator();
	}
}
