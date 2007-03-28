/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2007 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */

package Composestar.Visualization.Model;

import java.util.HashMap;
import java.util.Map;

import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.Master.CompileHistory;

/**
 * Is responsible for view construction/caching.
 * 
 * @author Michiel Hendriks
 */
public class ViewManager
{
	protected CompileHistory history;

	protected ProgramView programView;

	protected Map<Concern, FilterView> filterViews;

	public ViewManager(CompileHistory inHistory)
	{
		filterViews = new HashMap<Concern, FilterView>();
		history = inHistory;
	}

	/**
	 * Get the main/program view
	 * 
	 * @return
	 */
	public ProgramView getProgramView()
	{
		if (programView == null)
		{
			programView = new ProgramView(history);
		}
		return programView;
	}

	/**
	 * Get a filter view for a given concern
	 * 
	 * @param concern
	 * @return
	 */
	public FilterView getFilterView(Concern concern)
	{
		if (!filterViews.containsKey(concern))
		{
			filterViews.put(concern, new FilterView(history, concern));
		}
		return filterViews.get(concern);
	}

	/**
	 * @see #getFilterView(Concern)
	 * @param concern
	 * @return could be null when the selector can not be found
	 */
	public FilterView getFilterView(String concern)
	{
		Object o = history.getDataStore().getObjectByID(concern);
		if (o instanceof Concern)
		{
			return getFilterView((Concern) o);
		}
		return null;
	}

	/**
	 * Return a filter action view for the given input
	 * 
	 * @param concern
	 * @param selector
	 * @return
	 */
	public FilterActionView getFilterActionView(Concern concern, String selector)
	{
		return new FilterActionView(history, concern, selector);
	}

	/**
	 * @see #getFilterActionView(Concern, String)
	 * @param concern
	 * @param selector
	 * @return could be null when the selector can not be found
	 */
	public FilterActionView getFilterActionView(String concern, String selector)
	{
		Object o = history.getDataStore().getObjectByID(concern);
		if (o instanceof Concern)
		{
			return getFilterActionView((Concern) o, selector);
		}
		return null;
	}

	/**
	 * @see #getFilterActionView(Concern, String)
	 * @param concernSelector The concern and selector combined, should be in
	 *            the form of: concern.selector
	 * @return could be null when the selector can not be found
	 */
	public FilterActionView getFilterActionView(String concernSelector)
	{
		String concern = concernSelector.substring(0, concernSelector.lastIndexOf('.'));
		String selector = concernSelector.substring(concernSelector.lastIndexOf('.') + 1);
		return getFilterActionView(concern, selector);
	}
}
