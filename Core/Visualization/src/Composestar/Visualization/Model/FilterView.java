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

import java.util.Map;

import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.Master.CompileHistory;
import Composestar.Utils.Logging.CPSLogger;
import Composestar.Visualization.Model.Cells.FilterConcernVertex;
import Composestar.Visualization.Model.Cells.FilterModuleConcernVertex;

/**
 * The filter view. Show filter usage for a single concern.
 * 
 * @author Michiel Hendriks
 */
public class FilterView extends View
{
	protected static final CPSLogger logger = CPSLogger.getCPSLogger("VizCom.View.FilterView");

	protected Map<Concern, FilterModuleConcernVertex> cells;

	protected FilterConcernVertex focusVertex;

	public FilterView(CompileHistory data, Concern focusConcern)
	{
		super();
		focusVertex = new FilterConcernVertex(focusConcern);
		layout.insert(focusVertex);

		// TODO: add neighbouring cells with edges
	}
}
