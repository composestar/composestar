package Composestar.Visualization.Model.CellViews.FlowChart;

import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Polygon;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.jgraph.graph.AbstractCellView;
import org.jgraph.graph.CellView;
import org.jgraph.graph.CellViewRenderer;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.VertexRenderer;
import org.jgraph.graph.VertexView;

import Composestar.Visualization.Model.CpsGraphConstants;
import Composestar.Visualization.Model.CellViews.RenderUtils;

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

		/**
		 * The width of the lanes.
		 */
		protected int skew;

		@Override
		public Dimension getPreferredSize()
		{
			Dimension res = super.getPreferredSize();
			res.setSize(res.getWidth() * 1.1, res.getHeight());
			return res;
		}

		@Override
		protected void installAttributes(CellView view)
		{
			super.installAttributes(view);
			skew = CpsGraphConstants.getSkew(view.getAllAttributes());
		}

		@Override
		protected void resetAttributes()
		{
			super.resetAttributes();
			skew = CpsGraphConstants.DEFAULT_SKEW;
		}

		@Override
		public Point2D getPerimeterPoint(VertexView view, Point2D source, Point2D p)
		{
			Point2D center = AbstractCellView.getCenterPoint(view);
			Rectangle2D bounds = view.getBounds();
			Polygon parallel = RenderUtils.getParallelogram((int) bounds.getX(), (int) bounds.getY(), (int) bounds
					.getWidth(), (int) bounds.getHeight(), CpsGraphConstants.getSkew(view.getAllAttributes()), 0);
			Point2D res = view.getAllAttributes().createPoint();
			RenderUtils.intersectLinePolygon(p, center, parallel, res);
			return res;
		}

		@Override
		public void paint(Graphics g)
		{
			Graphics2D g2 = (Graphics2D) g;

			// construct the diamond
			Dimension dim = getSize();
			int width = dim.width - borderWidth;
			int height = dim.height - borderWidth;
			Polygon parallel = RenderUtils.getParallelogram(0, 0, width, height, skew, 0);

			if (isOpaque())
			{
				g2.setColor(getBackground());
				if (gradientColor != null && !preview)
				{
					setOpaque(false);
					g2.setPaint(new GradientPaint(0, 0, getBackground(), getWidth(), getHeight(), gradientColor, true));
				}
				g2.fillPolygon(parallel);
			}

			boolean origSelected = selected;
			try
			{
				setBorder(null);
				setOpaque(false);
				selected = false;
				super.paint(g);
			}
			finally
			{
				selected = origSelected;
			}

			if (bordercolor != null)
			{
				g2.setColor(bordercolor);
				g2.setStroke(new BasicStroke(borderWidth));
				g2.drawPolygon(parallel);
			}
			if (selected)
			{
				g2.setStroke(GraphConstants.SELECTION_STROKE);
				g2.setColor(highlightColor);
				g2.drawPolygon(parallel);
			}
		}

		@Override
		public Insets getInsets(Insets insets)
		{
			Insets ni = super.getInsets(insets);
			ni.left += skew / 2;
			ni.right += skew / 2;
			return ni;
		}
	}
}
