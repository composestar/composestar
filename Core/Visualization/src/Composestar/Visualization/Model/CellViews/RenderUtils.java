/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2007 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */

package Composestar.Visualization.Model.CellViews;

import java.awt.Polygon;
import java.awt.geom.Point2D;

/**
 * @author Michiel Hendriks
 */
public final class RenderUtils
{
	private RenderUtils()
	{}

	/**
	 * Compute the intersection of two line segments. From perfuse -
	 * http://prefuse.org/
	 * 
	 * @param a1x the x-coordinate of the first endpoint of the first line
	 * @param a1y the y-coordinate of the first endpoint of the first line
	 * @param a2x the x-coordinate of the second endpoint of the first line
	 * @param a2y the y-coordinate of the second endpoint of the first line
	 * @param b1x the x-coordinate of the first endpoint of the second line
	 * @param b1y the y-coordinate of the first endpoint of the second line
	 * @param b2x the x-coordinate of the second endpoint of the second line
	 * @param b2y the y-coordinate of the second endpoint of the second line
	 * @param intersect a Point in which to store the intersection point
	 * @return the intersection code. One of {@link #NO_INTERSECTION},
	 *         {@link #COINCIDENT}, or {@link #PARALLEL}.
	 */
	public static int intersectLineLine(double a1x, double a1y, double a2x, double a2y, double b1x, double b1y,
			double b2x, double b2y, Point2D intersect)
	{
		double uaT = (b2x - b1x) * (a1y - b1y) - (b2y - b1y) * (a1x - b1x);
		double ubT = (a2x - a1x) * (a1y - b1y) - (a2y - a1y) * (a1x - b1x);
		double uB = (b2y - b1y) * (a2x - a1x) - (b2x - b1x) * (a2y - a1y);

		if (uB != 0)
		{
			double ua = uaT / uB;
			double ub = ubT / uB;

			if (0 <= ua && ua <= 1 && 0 <= ub && ub <= 1)
			{
				intersect.setLocation(a1x + ua * (a2x - a1x), a1y + ua * (a2y - a1y));
				return 1;
			}
			else
			{
				return 0;
			}
		}
		else
		{
			return (uaT == 0 || ubT == 0 ? 1 : 2);
		}
	}

	/**
	 * Calculates an intersection of a line with a polygon
	 * 
	 * @param begin Begin point of the line
	 * @param end End point of the line
	 * @param poly The polygon to test against
	 * @param intersect Will contain the result of the closest intersection
	 *            point
	 * @return true when there's an intersection.
	 */
	public static boolean intersectLinePolygon(Point2D begin, Point2D end, Polygon poly, Point2D intersect)
	{
		Point2D pt = new Point2D.Double();
		boolean res = false;
		double dist = Double.MAX_VALUE;
		for (int i = 0; i < poly.npoints; i++)
		{
			double x1 = poly.xpoints[i];
			double y1 = poly.ypoints[i];
			int i2 = (i + 1) % poly.npoints;
			double x2 = poly.xpoints[i2];
			double y2 = poly.ypoints[i2];
			if (intersectLineLine(begin.getX(), begin.getY(), end.getX(), end.getY(), x1, y1, x2, y2, pt) != 0)
			{
				if (begin.distance(pt) < dist)
				{
					intersect.setLocation(pt);
					dist = begin.distance(pt);
					res = true;
				}
			}
		}
		return res;
	}

	/**
	 * Creates a parallelogram.
	 * 
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param hSkew
	 * @return
	 */
	public static Polygon getParallelogram(int x, int y, int width, int height, int hSkew, int vSkew)
	{
		// 1....2
		// ......
		// ......
		// 4....3
		int[] xcoords = { x + hSkew, x + width, x + width - hSkew, x };
		int[] ycoords = { y, y + vSkew, y + height, y + height - vSkew };
		return new Polygon(xcoords, ycoords, 4);
	}

	/**
	 * Create a diamon
	 * 
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @return
	 */
	public static Polygon getDiamond(int x, int y, int width, int height)
	{
		int[] xcoords = { x + width / 2, x + width, x + width / 2, x };
		int[] ycoords = { y, y + height / 2, y + height, y + height / 2 };
		return new Polygon(xcoords, ycoords, 4);
	}
}