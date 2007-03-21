package Composestar.Visualization.Model.CellViews.FlowChart;

import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Polygon;
import java.awt.geom.Point2D;

import org.jgraph.graph.CellView;
import org.jgraph.graph.CellViewRenderer;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.VertexRenderer;
import org.jgraph.graph.VertexView;

import Composestar.Visualization.Model.CpsGraphConstants;

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
			res.setSize(res.getWidth() * 1.1, res.getHeight() * 1.5);
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
			Point2D pt = super.getPerimeterPoint(view, source, p);
			return pt;
		}


		
// From perfuse - http://prefuse.org/
// TODO: go through the edges of the polygon
		
//	    /**
//	     * Compute the intersection of two line segments.
//	     * @param a1x the x-coordinate of the first endpoint of the first line
//	     * @param a1y the y-coordinate of the first endpoint of the first line
//	     * @param a2x the x-coordinate of the second endpoint of the first line
//	     * @param a2y the y-coordinate of the second endpoint of the first line
//	     * @param b1x the x-coordinate of the first endpoint of the second line
//	     * @param b1y the y-coordinate of the first endpoint of the second line
//	     * @param b2x the x-coordinate of the second endpoint of the second line
//	     * @param b2y the y-coordinate of the second endpoint of the second line
//	     * @param intersect a Point in which to store the intersection point
//	     * @return the intersection code. One of {@link #NO_INTERSECTION},
//	     * {@link #COINCIDENT}, or {@link #PARALLEL}.
//	     */
//	    public static int intersectLineLine(double a1x, double a1y, double a2x,
//	        double a2y, double b1x, double b1y, double b2x, double b2y, 
//	        Point2D intersect)
//	    {
//	        double ua_t = (b2x-b1x)*(a1y-b1y)-(b2y-b1y)*(a1x-b1x);
//	        double ub_t = (a2x-a1x)*(a1y-b1y)-(a2y-a1y)*(a1x-b1x);
//	        double u_b  = (b2y-b1y)*(a2x-a1x)-(b2x-b1x)*(a2y-a1y);
//
//	        if ( u_b != 0 ) {
//	            double ua = ua_t / u_b;
//	            double ub = ub_t / u_b;
//
//	            if ( 0 <= ua && ua <= 1 && 0 <= ub && ub <= 1 ) {
//	                intersect.setLocation(a1x+ua*(a2x-a1x), a1y+ua*(a2y-a1y));
//	                return 1;
//	            } else {
//	                return NO_INTERSECTION;
//	            }
//	        } else {
//	            return ( ua_t == 0 || ub_t == 0 ? COINCIDENT : PARALLEL );
//	        }
//	    }		
		
		
		
		
		
		@Override
		public void paint(Graphics g)
		{
			Graphics2D g2 = (Graphics2D) g;

			// construct the diamond
			Dimension dim = getSize();
			int width = dim.width - borderWidth;
			int height = dim.height - borderWidth;
			int[] xcoords = { 0 + skew, width, width - skew, 0 };
			int[] ycoords = { 0, 0, height, height };
			Polygon parallel = new Polygon(xcoords, ycoords, 4);

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
