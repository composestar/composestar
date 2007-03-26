/**
 * 
 */
package Composestar.Visualization.UI.Actions;

import java.awt.Rectangle;

import javax.swing.Action;

import org.jgraph.plugins.layouts.JGraphLayoutAlgorithm;

import Composestar.Visualization.Layout.SpringEmbeddedLayoutAlgorithm;
import Composestar.Visualization.Model.CpsJGraph;

/**
 * @author Michiel Hendriks
 */
public class LayoutAction extends ActiveGraphAction
{
	private static final long serialVersionUID = 1678508313397718304L;

	protected JGraphLayoutAlgorithm algorithm = new SpringEmbeddedLayoutAlgorithm(new Rectangle(0, 0, 750, 750), 30);

	public LayoutAction()
	{
		super();
		putValue(Action.NAME, algorithm.toString());
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
		if (layoutCells.length > 1)
		{
			JGraphLayoutAlgorithm.applyLayout(activeGraph, algorithm, layoutCells);
		}
	}

}
