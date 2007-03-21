package Composestar.Visualization.Model.CellViews.FlowChart;

import java.awt.Dimension;

import org.jgraph.graph.CellViewRenderer;
import org.jgraph.graph.VertexRenderer;
import org.jgraph.graph.VertexView;

/**
 * Renders a paralellogram
 * 
 * @author Michiel Hendriks
 */
public class ParallelogramView extends VertexView
{
	private static final long serialVersionUID = -1040005756568328339L;

	protected static final CellViewRenderer RENDERER = new ParallelogramRenderer();

	public ParallelogramView()
	{
		super();
	}

	public ParallelogramView(Object cell)
	{
		super(cell);
	}

	@Override
	public CellViewRenderer getRenderer()
	{
		return RENDERER;
	}

	/**
	 * TODO: implement
	 * 
	 * @author Michiel Hendriks
	 */
	public static class ParallelogramRenderer extends VertexRenderer
	{
		private static final long serialVersionUID = 7099442172305824016L;

		@Override
		public Dimension getPreferredSize()
		{
			Dimension res = super.getPreferredSize();
			return res;
		}
	}
}
