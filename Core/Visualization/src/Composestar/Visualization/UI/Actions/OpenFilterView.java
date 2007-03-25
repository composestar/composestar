/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2007 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */

package Composestar.Visualization.UI.Actions;

import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Visualization.Model.CpsJGraph;
import Composestar.Visualization.Model.Cells.ConcernVertex;

/**
 * @author Michiel Hendriks
 */
public class OpenFilterView extends ActiveGraphAction
{
	private static final long serialVersionUID = -2894040476064725010L;

	public OpenFilterView()
	{
		super("Open filter view");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Visualization.UI.Actions.ActiveGraphAction#execute(Composestar.Visualization.Model.CpsJGraph)
	 */
	@Override
	public void execute(CpsJGraph activeGraph)
	{
		if (activeGraph.getSelectionCell() instanceof ConcernVertex)
		{
			Concern concern = ((ConcernVertex) activeGraph.getSelectionCell()).getConcern();
			graphProvider.openGraph(controller.getViewManager().getFilterView(concern).getGraph());
		}

	}

}
