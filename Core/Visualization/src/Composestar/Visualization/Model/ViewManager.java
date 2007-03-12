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
}
