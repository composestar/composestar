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

import Composestar.Core.DIGGER2.Breadcrumb;
import Composestar.Visualization.Model.CpsJGraph;
import Composestar.Visualization.Model.CpsView;
import Composestar.Visualization.Model.Cells.TrailEdge;

/**
 * @author Michiel Hendriks
 */
public class OpenFilterActionView extends ActiveGraphAction
{
	private static final long serialVersionUID = -5896033582021383768L;

	public OpenFilterActionView()
	{
		super("Open filter action view");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Visualization.UI.Actions.ActiveGraphAction#execute(Composestar.Visualization.Model.CpsJGraph)
	 */
	@Override
	public void execute(CpsJGraph activeGraph)
	{
		if (activeGraph.getSelectionCell() instanceof TrailEdge)
		{
			TrailEdge edge = (TrailEdge) activeGraph.getSelectionCell();
			Breadcrumb crumb = edge.getTrail().getOwner();
			CpsView view = controller.getViewManager().getFilterActionView(crumb.getConcern(),
					crumb.getMessage().getSelector());
			graphProvider.openGraph(view.getGraph());
		}
	}

}
