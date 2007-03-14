/*
 * Copyright (c) 2004, Jeremy Jedynak, NetCentric Technology Inc All rights reserved.
 *
 * Copyright (c) 2004, Sven Luzar All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *  - Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer. - Redistributions in
 * binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution. - Neither the name of JGraph nor
 * the names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 */
package Composestar.Visualization.Layout;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.jgraph.JGraph;
import org.jgraph.graph.AbstractCellView;
import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.CellView;
import org.jgraph.graph.EdgeView;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.GraphLayoutCache;
import org.jgraph.graph.PortView;
import org.jgraph.graph.VertexView;
import org.jgraph.plugins.layouts.JGraphLayoutAlgorithm;

/**
 * Arranges the nodes with the Spring Embedded Layout Algorithm. <br>
 * The algorithm takes O(|V|^2 * |E|) time. Jeremy Jedynak NetCentric Technology
 * Inc www.NetCentricInc.com <br>
 * This layout algorithm is derived from the one written by Sven Luzar. It fixes
 * a bug in how the "u" and "v" vectors were stored and retrieved, which
 * frequently caused IllegalArgumentExceptions and NullPointerExceptions,
 * depending upon your luck. <br>
 * Also, the original run method has been broken into several methods. Two new
 * constructors and methods were added to support two constraints: the graph
 * rendering frame and maxIterations. Most variable names have been modified for
 * readability. <br>
 * It now properly lays out the graph according to the algorithm. Work still
 * needs to be done to better space vertices so that they do not overlap and so
 * that edges do not pass through vertices. The new "step" parameter of the run
 * method is currently not used. All that being said, this is a good starting
 * point to understand the algorithm and make those changes. <br>
 * <br>
 * 
 * @author <a href="mailto:Jeremy.Jedynak@NetCentricInc.com">Jeremy Jedynak </a>
 * @author <a href="mailto:Sven.Luzar@web.de">Sven Luzar </a>
 * @author <a href="mailto:rmc@telemidia.puc-rio.br">Rogerio Coelho </a>
 * @author <a href="mailto:elmuerte@drunksnipers.com">Michiel Hendriks</a>
 * @version 1.2 Last Modification : 2007-03-12 By : Michiel Hendriks. Adjusted
 *          the algorithm to take into account grouped cells. The child cells of
 *          the input cells will be modified so that the "group cell" will move
 *          the the correct location. The child cells will not be layed out
 *          individually (unless they are in the cells to be modified).
 */
public class SpringEmbeddedLayoutAlgorithm extends JGraphLayoutAlgorithm
{
	/**
	 * Key for an attribute. The value for this key is a Rectangle object and
	 * specifies the disposement.
	 */
	public static final String SPRING_EMBEDDED_DISP = "SpringEmbeddedDisp";

	/**
	 * Key for an attribute. The value for this key is a Rectangle object and
	 * specifies the calculated position.
	 */
	public static final String SPRING_EMBEDDED_POS = "SpringEmbeddedPos";

	public static final String SPRING_EMBEDDED_ORIGPOS = "SpringEmbeddedOrigPos";

	private Rectangle myFrame;

	private int myMaxIterations = -1;

	/**
	 * If true then it will take into account the subgraphs. It will translate
	 * the views in the subgraph of the selected views. For this to work correct
	 * you should only pass the groups you want to layout and not the subgraphs.
	 */
	private boolean groupAware;

	public SpringEmbeddedLayoutAlgorithm()
	{
		this(true);
	}

	public SpringEmbeddedLayoutAlgorithm(boolean inGroupAware)
	{
		myFrame = new Rectangle(0, 0, 500, 500); // It's ok 500,500
		myMaxIterations = 130; // It's ok 30
		groupAware = inGroupAware;
	}

	public SpringEmbeddedLayoutAlgorithm(Rectangle frame, int maxIterations)
	{
		this(frame, maxIterations, true);
	}

	public SpringEmbeddedLayoutAlgorithm(Rectangle frame, int maxIterations, boolean inGroupAware)
	{
		if (frame == null)
		{
			throw new IllegalArgumentException("frame may not be null");
		}
		if (frame.width <= 0)
		{
			throw new IllegalArgumentException("frame width must be positive");
		}

		if (frame.height <= 0)
		{
			throw new IllegalArgumentException("frame height must be positive");
		}
		else
		{
			myFrame = frame;
		}

		if (maxIterations < 0)
		{
			throw new IllegalArgumentException("maxIterations must be a positive integer");
		}
		else
		{
			myMaxIterations = maxIterations;
		}
		groupAware = inGroupAware;
	}

	public void setFrame(Rectangle frame)
	{
		if (frame == null)
		{
			throw new IllegalArgumentException("Frame may not be null");
		}
		else if (frame.width <= 0)
		{
			throw new IllegalArgumentException("frame width must be positive");
		}
		else if (frame.height <= 0)
		{
			throw new IllegalArgumentException("frame height must be positive");
		}

		myFrame = frame;
	}

	public void setGroupAware(boolean inval)
	{
		groupAware = inval;
	}

	public boolean isGroupAware()
	{
		return groupAware;
	}

	/**
	 * Returns the name of this algorithm in human readable form.
	 */
	public String toString()
	{
		return "Spring Embedded";
	}

	public void setMaxIterations(int maxIterations)
	{
		if (maxIterations < 0)
		{
			throw new IllegalArgumentException("maxIterations must be a positive integer");
		}

		myMaxIterations = maxIterations;
	}

	/**
	 * Replace all cells inside the frame because have cells with valeu negative
	 * for x and y
	 * 
	 * @param vertices : list all vertices
	 * @param frameRectangle : Rectangle where x and y have the minX and minY
	 *            the all cells in the graph.
	 */
	private void replaceInsideFrame(List vertices, Rectangle2D frameRectangle)
	{
		for (int i = 0; i < vertices.size(); i++)
		{
			VertexView vertex = (VertexView) vertices.get(i);
			Rectangle2D vPos = getVertexPosition(vertex, SPRING_EMBEDDED_POS);
			Rectangle2D newRandomPosition = new Rectangle2D.Double(vPos.getX() - frameRectangle.getX(), vPos.getY()
					- frameRectangle.getY(), vPos.getWidth(), vPos.getHeight());
			updateVertexPosition(vertex, SPRING_EMBEDDED_POS, newRandomPosition);
		}
	}

	/**
	 * The implementation of the layout algorithm.
	 * 
	 * @param graph : JGraph instance
	 * @param dynamicCells : List of all nodes the layout should move
	 * @param staticCells : List of node the layout should not move but allow
	 *            for
	 */
	public void run(JGraph graph, Object[] dynamicCells, Object[] staticCells)
	{

		// ---------------------------------------------------------------------------
		// initial work
		// ---------------------------------------------------------------------------
		GraphLayoutCache layoutCache = graph.getGraphLayoutCache();
		List<VertexView> vertices = new ArrayList<VertexView>(); // vertices
		List<EdgeView> edges = new ArrayList<EdgeView>(); // Edges

		// Take all cells
		CellView[] cellviews = layoutCache.getMapping(dynamicCells, false);

		// System.out.println("Number of Cells = " + cellviews.length);

		partitionCells(cellviews, vertices, edges);

		// System.out.println("Number of vertices = " + vertices.size());
		// System.out.println("Number of Edges = " + edges.size());

		double frameWidth = myFrame.getWidth(); // Width of the selectionFrame
		double frameHeight = myFrame.getHeight(); // Height of the
		// selectionFrame
		double frameArea = frameWidth * frameHeight; // area of the
		// selectionFrame
		/*
		 * System.out.println( " W = " + FrameWidth ) ; System.out.println( " H = " +
		 * FrameHeight ) ; System.out.println( " Area = " + FrameArea ) ;
		 */
		randomizeVertexPositions(vertices);

		// ---------------------------------------------------------------------------
		// start the iterations
		// ---------------------------------------------------------------------------

		// calculate the field length for the area
		double areaFieldLength = Math.sqrt(frameArea / (vertices.size()));

		List<VertexView> verticesWithOutEdges = findVertexWithoutEdges(edges, vertices);

		for (int loop = 0; loop < myMaxIterations; loop++)
		{
			calculateRepulsiveForces(vertices, areaFieldLength);
			calculateAttractiveForces(edges, verticesWithOutEdges, areaFieldLength);
			calculateNewPositions(vertices, loop);
		}

		// calculate the stretch factor and the movement factor
		// to fit the calculated frame to the selected Frame
		Rectangle2D calculateFrame = calculateNewFrame(vertices);
		replaceInsideFrame(vertices, calculateFrame);

		calculateFrame.setFrame(0, 0, 900, 900);// this is necessary because
		// the frame have w=500 and h=500

		double stretchX = (myFrame.width) / (calculateFrame.getWidth());

		double stretchY = (myFrame.height) / (calculateFrame.getHeight());

		int movementX = (int) (myFrame.x - calculateFrame.getX());

		int movementY = (int) (myFrame.y - calculateFrame.getY());

		Map viewMap = drawGraph(layoutCache.getMapping(dynamicCells, false), movementX, stretchX, movementY, stretchY);

		layoutCache.edit(viewMap, null, null, null);
	}

	private void partitionCells(CellView[] cells, List<VertexView> vertices, List<EdgeView> edges)
	{
		// Partition the list of cells into list of vertices and list of edges
		for (int loop = 0; loop < cells.length; loop++)
		{
			CellView cell = cells[loop];

			if (cell == null)
			{
				// Skip over null cells
				continue;
			}
			else if (cell instanceof EdgeView)
			{
				EdgeView edge = (EdgeView) cell;
				edges.add(edge);
			}
			else if (cell instanceof VertexView)
			{
				VertexView vertex = (VertexView) cell;
				vertices.add(vertex);
			}
		}
	}

	private void randomizeVertexPositions(List<VertexView> vertices)
	{
		int vertW, vertH, frameW, frameH, newX, newY = 0;
		Rectangle newRandomPosition = null;

		// Fill the initial positions with random positions
		Random random = new Random();
		for (int loop = 0; loop < vertices.size(); loop++)
		{
			VertexView vertex = (VertexView) vertices.get(loop);
			Rectangle2D bounds = vertex.getBounds();
			updateVertexPosition(vertex, SPRING_EMBEDDED_ORIGPOS, bounds);
			vertW = (int) bounds.getWidth();
			vertH = (int) bounds.getHeight();

			frameW = myFrame.width;
			frameH = myFrame.height;

			newX = random.nextInt(frameW);
			// Verify if newX is out of MyFrame
			while (newX < 0)
			{
				newX = random.nextInt(frameW);
			}

			newY = random.nextInt(frameH);
			// Verify if newY is out of MyFrame
			while (newY < 0)
			{
				newY = random.nextInt(frameW);
			}

			// System.out.println("Vertex = " + vertex.getCell().toString() + "
			// x = " + newX + " y = " + newY + " w = " + vertW + " h = "
			// +vertH);
			newRandomPosition = new Rectangle(newX, newY, vertW, vertH);
			updateVertexPosition(vertex, SPRING_EMBEDDED_POS, newRandomPosition);
		}
	}

	private void calculateRepulsiveForces(List<VertexView> vertices, double areaFieldLength)
	{
		// ---------------------------------------------------------------------------
		// calculate the repulsive forces
		// ---------------------------------------------------------------------------

		// calculate the repulsive forces
		for (int vCount = 0; vCount < vertices.size(); vCount++)
		{
			VertexView v = (VertexView) vertices.get(vCount);

			Rectangle2D vPos = getVertexPosition(v, SPRING_EMBEDDED_POS);

			// System.out.println( "vertex " + loop + " get: vPos = " + vPos ) ;

			// Each vertex has two vectors: pos and disp
			Rectangle vDisp = new Rectangle(0, 0);
			for (int uCount = 0; uCount < vertices.size(); uCount++)
			{
				VertexView u = (VertexView) vertices.get(uCount);

				if (u != v)
				{
					// delta is short hand for the difference
					// vector between the positions of the two vertices

					Rectangle2D uPos = getVertexPosition(u, SPRING_EMBEDDED_POS);

					// System.out.println( "vertex " + uCount + " get: uPos = "
					// + uPos ) ;

					Rectangle delta = new Rectangle();
					delta.x = (int) (vPos.getX() - uPos.getX());
					delta.y = (int) (vPos.getY() - uPos.getY());

					double fr = fr(norm(delta), areaFieldLength);
					// System.out.println("FR = " + fr);

					// fr = fr - fr*0.20;
					// System.out.println("FR = " + fr);

					double deltaNormX = delta.x / norm(delta);
					double dispX = deltaNormX * fr;
					double deltaNormY = delta.y / norm(delta);
					double dispY = deltaNormY * fr;

					vDisp.x = vDisp.x + (int) dispX;
					vDisp.y = vDisp.y + (int) dispY;

				}
			}

			updateVertexPosition(v, SPRING_EMBEDDED_DISP, vDisp);
		}
	}

	private void calculateAttractiveForces(List<EdgeView> edges, List<VertexView> verticesWithoutEdges,
			double areaFieldLength)
	{
		// ---------------------------------------------------------------------------
		// calculate the attractive forces
		// ---------------------------------------------------------------------------
		double dispX = 0, dispY = 0;
		EdgeView e = null;

		// Verify if have edges
		if (edges.size() == 0)
		{
			return;
		}

		for (int cellCount = 0; cellCount < edges.size(); cellCount++)
		{
			e = (EdgeView) edges.get(cellCount);

			if (e.getSource() != null && e.getTarget() != null && e.getSource() != e.getTarget())
			{
				// extract the used fields

				CellView v = ((PortView) e.getSource()).getParentView();
				CellView u = ((PortView) e.getTarget()).getParentView();

				// Source and Target equal
				if (v == u)
				{
					continue;
				}

				Rectangle2D vPos = getVertexPosition(v, SPRING_EMBEDDED_POS);
				Rectangle2D uPos = getVertexPosition(u, SPRING_EMBEDDED_POS);

				// System.out.println( "vertex get: vPos = " + vPos ) ;
				// System.out.println( "vertex get: uPos = " + uPos ) ;

				if (vPos == null || uPos == null)
				{
					continue;
				}

				Rectangle2D vDisp = getVertexPosition(v, SPRING_EMBEDDED_DISP);
				Rectangle2D uDisp = getVertexPosition(u, SPRING_EMBEDDED_DISP);

				// System.out.println( "vertex get: vDisp = " + vDisp ) ;
				// System.out.println( "vertex get: uDisp = " + uDisp ) ;

				if (vDisp == null || uDisp == null)
				{
					continue;
				}

				// calculate the delta
				Rectangle delta = new Rectangle();
				delta.x = (int) (vPos.getX() - uPos.getX());
				delta.y = (int) (vPos.getY() - uPos.getY());

				// calculate the attractive forces
				double fa = fa(norm(delta), areaFieldLength);
				// System.out.println("FA = " + fa);

				double deltaNormX = delta.x / norm(delta);
				double deltaNormY = delta.y / norm(delta);
				dispX = deltaNormX * fa;
				dispY = deltaNormY * fa;

				vDisp.setFrame(vDisp.getX() - dispX, vDisp.getY() - dispY, vDisp.getWidth(), vDisp.getHeight());

				uDisp.setFrame(uDisp.getX() + dispX, uDisp.getY() + dispY, uDisp.getWidth(), uDisp.getHeight());

				// System.out.println( "Cell update: vDisp = " +
				// v.getCell().toString() + " rec = " + vDisp ) ;
				// System.out.println( "Cell update: uDisp = " +
				// u.getCell().toString() + " rec = " + uDisp ) ;

				// store the new values
				updateVertexPosition(v, SPRING_EMBEDDED_DISP, vDisp);
				updateVertexPosition(u, SPRING_EMBEDDED_DISP, uDisp);
			}
		}

		// vertices without edges
		for (int cellCount = 0; cellCount < verticesWithoutEdges.size(); cellCount++)
		{
			VertexView vertex = (VertexView) verticesWithoutEdges

			.get(cellCount);

			int vertW = (int) vertex.getBounds().getWidth();
			int vertH = (int) vertex.getBounds().getHeight();
			Rectangle newRandomPosition = null;

			// Fill the initial positions with random positions
			Random random = new Random();

			// Take the new position inside the Frame
			int newX = random.nextInt(500);

			// Verify if newX is outSide of MyFrame
			while (newX < 0)
			{
				newX = random.nextInt((int) vertex.getBounds().getCenterX());
			}

			// Take the new position inside the Frame
			int newY = random.nextInt(500);
			// Verify if newY is outSide of MyFrame
			while (newY < 0)
			{
				newY = random.nextInt((int) vertex.getBounds().getCenterY());
			}

			// System.out.println("Vertex = " + vertex.getCell().toString() + "
			// x = " + newX + " y = " + newY + " w = " + vertW + " h = "
			// +vertH);
			newRandomPosition = new Rectangle(newX, newY, vertW, vertH);
			updateVertexPosition(vertex, SPRING_EMBEDDED_POS, newRandomPosition);
		}
	}

	/**
	 * Take all vertices without edges and put in List vertexWithoutEdges.
	 * 
	 * @param edges : List the all edges
	 * @param vertices : List the all vertices
	 * @return vertexWithoutEdges : List of vertices without edge
	 */
	private List<VertexView> findVertexWithoutEdges(List<EdgeView> edges, List<VertexView> vertices)
	{
		List<VertexView> vertexWithoutEdges = new ArrayList<VertexView>();

		// Load all vertices
		for (int i = 0; i < vertices.size(); i++)
		{
			vertexWithoutEdges.add(vertices.get(i));
		}

		// Remove all vertices of edges
		for (int i = 0; i < edges.size(); i++)
		{
			EdgeView e = (EdgeView) edges.get(i);
			CellView source = e.getSource().getParentView();
			CellView target = e.getTarget().getParentView();

			vertexWithoutEdges.remove(source);
			vertexWithoutEdges.remove(target);

			if (groupAware)
			{
				// TODO: this can probably be improved
				removeWithParents(vertexWithoutEdges, source.getParentView());
				removeWithParents(vertexWithoutEdges, target.getParentView());
			}
		}

		return vertexWithoutEdges;
	}

	private void removeWithParents(List<VertexView> vertices, CellView vertex)
	{
		while (vertex != null)
		{
			vertices.remove(vertex);
			vertex = vertex.getParentView();
		}
	}

	private void calculateNewPositions(List<VertexView> vertices, int curIteration)
	{
		// ---------------------------------------------------------------------------
		// calculate the new positions
		// ---------------------------------------------------------------------------

		// limit the maximum displacement to the temperature buttonText
		// and then prevent from being displacement outside frame
		double temperature = Math.sqrt(Math.pow(myFrame.width, 2) + Math.pow(myFrame.height, 2))
				* ((((double) myMaxIterations) / (curIteration + 1)) / myMaxIterations);

		for (int vCount = 0; vCount < vertices.size(); vCount++)
		{
			VertexView v = (VertexView) vertices.get(vCount);
			Rectangle2D vDisp = getVertexPosition(v, SPRING_EMBEDDED_DISP);
			Rectangle2D vPos = getVertexPosition(v, SPRING_EMBEDDED_POS);

			// System.out.println( "vertex " + v.toString() + "get: vDisp = " +
			// vDisp ) ;
			// System.out.println( "vertex " + v.toString() + "get: vDisp = " +
			// vPos ) ;

			double dispNormX = vDisp.getX() / norm(vDisp);
			double minX = Math.min(Math.abs(vDisp.getX()), temperature);

			double dispNormY = vDisp.getY() / norm(vDisp);
			double minY = Math.min(Math.abs(vDisp.getY()), temperature);

			vPos.setFrame(vPos.getX() + dispNormX * minX, vPos.getY() + dispNormY * minY, vPos.getWidth(), vPos
					.getHeight());

			/*
			 * double maxX = Math.max(-W / 2, vPos.x); double maxY = Math.max(-L /
			 * 2, vPos.y); double minX2 = Math.min(W / 2, maxX); double minY2 =
			 * Math.min(L / 2, maxY); vPos.x = (int)minX2; vPos.y = (int)minY2;
			 */

			// System.out.println( "cell update: vPos = " + vPos ) ;
			updateVertexPosition(v, SPRING_EMBEDDED_POS, vPos);
		}
	}

	private Rectangle2D calculateNewFrame(List<VertexView> vertices)
	{
		double x = 0, y = 0, w = 0, h = 0;
		// find the new positions for the
		// calculated frame
		Rectangle2D calculatedFrame = new Rectangle2D.Double();

		for (int vCount = 0; vCount < vertices.size(); vCount++)
		{
			VertexView v = (VertexView) vertices.get(vCount);
			Rectangle2D vPos = getVertexPosition(v, SPRING_EMBEDDED_POS);

			// System.out.println( "vertex get: vPos = " + vPos ) ;

			x = calculatedFrame.getX();
			y = calculatedFrame.getY();
			w = calculatedFrame.getWidth();
			h = calculatedFrame.getHeight();

			if (vPos.getX() < calculatedFrame.getX())
			{
				x = vPos.getX();
			}
			if (vPos.getY() < calculatedFrame.getY())
			{
				y = vPos.getY();
			}

			double width = vPos.getX() - calculatedFrame.getX();
			if (width > calculatedFrame.getWidth())
			{
				w = width;
			}

			double height = vPos.getY() - calculatedFrame.getY();
			if (height > calculatedFrame.getHeight())
			{
				h = height;
			}

			// System.out.println( "vertex =" + v.getCell());
			// System.out.println("x = " + x + " y = " + y + " w = " + w + " h =
			// " + h);

			calculatedFrame.setFrame(x, y, w, h);

		}
		return (calculatedFrame);
	}

	private Map<CellView, AttributeMap> drawGraph(CellView[] cells, int movementX, double stretchX, int movementY,
			double stretchY)
	{
		// ---------------------------------------------------------------------------
		// draw the graph
		// ---------------------------------------------------------------------------
		Map<CellView, AttributeMap> viewMap = new Hashtable<CellView, AttributeMap>();

		for (int loop = 0; loop < cells.length; loop++)
		{
			CellView cell = cells[loop];

			if (cell == null)
			{
				continue;
			}
			else if (cell instanceof EdgeView)
			{
				cell.update();
			}
			else if (cell instanceof VertexView)
			{
				// get the current view object
				VertexView vertex = (VertexView) cell;

				// remove the temp objects
				Rectangle2D newPosition = removeVertexPosition(vertex, SPRING_EMBEDDED_POS);

				removeVertexPosition(vertex, SPRING_EMBEDDED_DISP);

				// System.out.println( "vertex" + vertex.getCell() + "children =
				// " + vertex.getChildViews().length+ " newPosition = " +
				// newPosition);

				// update the location to get the correct
				newPosition.setFrame((newPosition.getX() + movementX) * stretchX, (newPosition.getY() + movementY)
						* stretchY, newPosition.getWidth(), newPosition.getHeight());

				if (vertex.isLeaf() && groupAware)
				{
					// update the view
					AttributeMap vertAttrib = new AttributeMap();
					GraphConstants.setBounds(vertAttrib, newPosition);
					vertex.changeAttributes(vertAttrib);
					// The statement above fixes a bug in the original code

					viewMap.put(cells[loop], vertAttrib);
				}
				else
				{
					// is a group, update the group
					Rectangle2D oldPosition = removeVertexPosition(vertex, SPRING_EMBEDDED_ORIGPOS);

					CellView[] p = { vertex };
					CellView[] childs = AbstractCellView.getDescendantViews(p);
					for (CellView v : childs)
					{
						if (!v.isLeaf())
						{
							continue;
						}
						AttributeMap vertAttrib = new AttributeMap();
						Rectangle2D oldBounds = v.getBounds();
						// correction according to original sub position
						double ddx = oldBounds.getX() - oldPosition.getX();
						double ddy = oldBounds.getY() - oldPosition.getY();

						oldBounds.setFrame(newPosition.getX() + ddx, newPosition.getY() + ddy, oldBounds.getWidth(),
								oldBounds.getHeight());
						GraphConstants.setBounds(vertAttrib, oldBounds);
						v.changeAttributes(vertAttrib);
						viewMap.put(v, vertAttrib);
					}
				}
			}
		}

		return viewMap;
	}

	@SuppressWarnings("unchecked")
	private void updateVertexPosition(CellView vert, String posField, Rectangle2D position)
	{
		AttributeMap vertAttrib = vert.getAllAttributes();

		vertAttrib.put(posField, position);

		/*
		 * There is no contractual guarantee that the Attribute Map returned by
		 * CellView.getAllAttributes() or (especially) CellView.getAttributes()
		 * is the same Attribute Map stored within the VertexView, so a
		 * seemingly redundant call to CellView.setAttributes msut be made
		 * below. This fixes a bug in the original code.
		 */
		vert.changeAttributes(vertAttrib);
	}

	private Rectangle2D getVertexPosition(CellView vert, String posField)
	{
		AttributeMap vertAttrib = vert.getAllAttributes();

		Rectangle2D result = (Rectangle2D) vertAttrib.get(posField);

		return result;
	}

	private Rectangle2D removeVertexPosition(CellView vert, String posField)
	{
		AttributeMap vertAttrib = vert.getAllAttributes();

		Rectangle2D result = (Rectangle2D) vertAttrib.remove(posField);

		/*
		 * There is no contractual guarantee that the Attribute Map returned by
		 * CellView.getAllAttributes() or (especially) CellView.getAttributes()
		 * is the same Attribute Map stored within the VertexView, so a
		 * seemingly redundant call to CellView.setAttributes msut be made
		 * below. This fixes a bug in the original code.
		 */
		vert.changeAttributes(vertAttrib);

		return result;
	}

	/**
	 * calculates the attractive forces
	 */
	protected double fa(double x, double k)
	{
		double force = x * x / k;
		return force;
	}

	/**
	 * calculates the repulsive forces
	 */
	protected double fr(double x, double k)
	{
		double force = (k * k) / x;
		return force;
	}

	/**
	 * Calculates the euklidische Norm for the point p.
	 */
	protected double norm(Rectangle2D p)
	{
		double x = p.getX();
		double y = p.getY();
		double norm = Math.sqrt(x * x + y * y);
		return norm;
	}
}
