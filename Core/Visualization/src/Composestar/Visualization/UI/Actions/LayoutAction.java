/**
 * 
 */
package Composestar.Visualization.UI.Actions;

import java.awt.Rectangle;

import org.jgraph.plugins.layouts.JGraphLayoutAlgorithm;

import Composestar.Visualization.Layout.SpringEmbeddedLayoutAlgorithm;
import Composestar.Visualization.Model.CpsJGraph;

/**
 * @author Michiel Hendriks
 */
public class LayoutAction extends ActiveGraphAction
{
	public LayoutAction()
	{
		super("Spring");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Visualization.UI.Actions.ActiveGraphAction#execute(Composestar.Visualization.Model.CpsJGraph)
	 */
	@Override
	public void execute(CpsJGraph activeGraph)
	{
		Object[] layoutCells = activeGraph.getCpsView().getLayoutCells();
		JGraphLayoutAlgorithm.applyLayout(activeGraph, new SpringEmbeddedLayoutAlgorithm(new Rectangle(300, 300), 30),
				layoutCells);
	}

}
