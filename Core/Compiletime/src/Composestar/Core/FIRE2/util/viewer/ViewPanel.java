/*
 * Created on 17-aug-2006
 *
 */
package Composestar.Core.FIRE2.util.viewer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Vector;

import javax.swing.JPanel;

import Composestar.Core.FIRE2.model.ExecutionModel;
import Composestar.Core.FIRE2.model.ExecutionState;
import Composestar.Core.FIRE2.model.ExecutionTransition;
import Composestar.Core.FIRE2.model.FlowModel;
import Composestar.Core.FIRE2.model.FlowNode;
import Composestar.Core.FIRE2.model.FlowTransition;

public class ViewPanel extends JPanel
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5738123083267682871L;

	private LinkedList unProcessed;

	private HashMap nodeMap;

	private HashMap labelCounter;

	private Node rootNode;

	private final static int RADIUS = 40;

	private final static int MARGIN = 10;

	private final static int ARROW_HEAD_XOFFSET = -10;

	private final static int ARROW_HEAD_YOFFSET = -5;

	private int height = 0;

	private final static Edge[] EmptyEdgeArray = {};

	public ViewPanel(ExecutionModel model)
	{
		super();

		initialize(model);
	}

	public ViewPanel(FlowModel model)
	{
		super();

		initialize(model);
	}

	private void initialize()
	{
		super.setBackground(Color.WHITE);
	}

	private void initialize(FlowModel model)
	{
		initialize();

		unProcessed = new LinkedList();
		nodeMap = new HashMap();
		labelCounter = new HashMap();

		Vector edges = new Vector();

		FlowNode startNode = model.getStartNode();

		rootNode = new Node(startNode);
		nodeMap.put(startNode, rootNode);
		unProcessed.add(startNode);

		while (!unProcessed.isEmpty())
		{
			FlowNode node = (FlowNode) unProcessed.removeFirst();
			process(node);
			addLabels(node);
		}

		calculatePositions();
	}

	private void process(FlowNode flowNode)
	{
		Vector primaryEdges = new Vector();
		Vector secondaryEdges = new Vector();

		Node node = (Node) nodeMap.get(flowNode);

		Iterator outTransitions = flowNode.getTransitions();
		while (outTransitions.hasNext())
		{
			FlowTransition transition = (FlowTransition) outTransitions.next();

			FlowNode nextFlowNode = transition.getEndNode();
			if (!nodeMap.containsKey(nextFlowNode))
			{
				Node newNode = new Node(nextFlowNode);

				nodeMap.put(nextFlowNode, newNode);
				unProcessed.add(nextFlowNode);

				Edge edge = new Edge(node, newNode);
				primaryEdges.add(edge);
			}
			else
			{
				Node nextNode = (Node) nodeMap.get(nextFlowNode);

				Edge edge = new Edge(node, nextNode);
				secondaryEdges.add(edge);
			}
		}

		node.primaryEdges = (Edge[]) primaryEdges.toArray(EmptyEdgeArray);
		node.secondaryEdges = (Edge[]) secondaryEdges.toArray(EmptyEdgeArray);
	}

	private void initialize(ExecutionModel model)
	{
		initialize();

		rootNode = new Node((ExecutionState) null);

		unProcessed = new LinkedList();
		nodeMap = new HashMap();
		labelCounter = new HashMap();

		Vector edges = new Vector();

		Iterator it = model.getEntranceStates();
		while (it.hasNext())
		{
			Object obj = it.next();
			Node node = new Node((ExecutionState) obj);
			nodeMap.put(obj, node);

			unProcessed.add(obj);

			Edge edge = new Edge(rootNode, node);
			edges.add(edge);
		}

		rootNode.primaryEdges = (Edge[]) edges.toArray(new Edge[0]);
		rootNode.secondaryEdges = new Edge[0];

		processExec();

		calculatePositions();
	}

	private void processExec()
	{
		while (!unProcessed.isEmpty())
		{
			ExecutionState state = (ExecutionState) unProcessed.removeFirst();
			processExecState(state);
			addLabels(state.getFlowNode());
		}
	}

	private void addLabels(FlowNode node)
	{
		Iterator iter = node.getNames();
		while (iter.hasNext())
		{
			addCount(iter.next());
		}
	}

	private void addCount(Object obj)
	{
		Integer i = (Integer) labelCounter.get(obj);

		if (i == null)
		{
			labelCounter.put(obj, 1);
		}
		else
		{
			labelCounter.put(obj, i.intValue() + 1);
		}
	}

	private String getLabel(FlowNode flowNode)
	{
		if (flowNode == null)
		{
			return "";
		}

		String currentLabel = "";
		int currentCount = Integer.MAX_VALUE;

		Iterator iterator = flowNode.getNames();
		while (iterator.hasNext())
		{
			Object obj = iterator.next();
			Integer i = (Integer) labelCounter.get(obj);

			if (i < currentCount)
			{
				currentLabel = (String) obj;
				currentCount = i;
			}
		}

		return currentLabel;
	}

	public void highlightNodes(Collection executionStates)
	{
		// first remove highlight:
		Collection nodes = nodeMap.values();
        for (Object node1 : nodes) {
            Node node = (Node) node1;
            node.highlighted = false;
        }

        // then add highlight:
        Iterator iter = executionStates.iterator();
        while (iter.hasNext())
		{
			ExecutionState state = (ExecutionState) iter.next();
			Node node = (Node) nodeMap.get(state);
			node.highlighted = true;
		}

		repaint();
	}

	private void processExecState(ExecutionState state)
	{
		Vector primaryEdges = new Vector();
		Vector secondaryEdges = new Vector();

		Node node = (Node) nodeMap.get(state);

		Iterator outTransitions = state.getOutTransitions();
		while (outTransitions.hasNext())
		{
			ExecutionTransition transition = (ExecutionTransition) outTransitions.next();

			ExecutionState nextState = transition.getEndState();
			if (!nodeMap.containsKey(nextState))
			{
				Node newNode = new Node(nextState);

				nodeMap.put(nextState, newNode);
				unProcessed.add(nextState);

				Edge edge = new Edge(node, newNode);
				primaryEdges.add(edge);
			}
			else
			{
				Node nextNode = (Node) nodeMap.get(nextState);

				Edge edge = new Edge(node, nextNode);
				secondaryEdges.add(edge);
			}
		}

		node.primaryEdges = (Edge[]) primaryEdges.toArray(EmptyEdgeArray);
		node.secondaryEdges = (Edge[]) secondaryEdges.toArray(EmptyEdgeArray);
	}

	private void calculatePositions()
	{
		int width = calculatePosition(rootNode, 0, 0);
		this.height += 4 * (MARGIN + RADIUS);

		this.setPreferredSize(new Dimension(width, height));
	}

	private int calculatePosition(Node node, int xOffset, int yOffset)
	{
		node.yPos = yOffset + MARGIN + RADIUS;

		node.xPos = xOffset + MARGIN + RADIUS;

		if (node.primaryEdges.length == 0)
		{
			return 2 * (MARGIN + RADIUS);
		}

		int width = 0;
		int newYOffset = yOffset + 2 * (MARGIN + RADIUS);
        for (Edge primaryEdge : node.primaryEdges) {
            width += calculatePosition(primaryEdge.endNode, xOffset + width, newYOffset);
        }

        this.height = Math.max(yOffset, this.height);

		return width;
	}

	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);

		g.setFont(g.getFont().deriveFont(10.0f));

		paintNode(g, rootNode);
	}

	private void paintNode(Graphics g, Node node)
	{
		if (node.highlighted)
		{
			g.setColor(Color.GREEN);
			g.fillOval(node.xPos - RADIUS, node.yPos - RADIUS, 2 * RADIUS + 1, 2 * RADIUS + 1);
			g.setColor(Color.BLACK);
		}

		g.drawOval(node.xPos - RADIUS, node.yPos - RADIUS, 2 * RADIUS, 2 * RADIUS);

        for (Edge primaryEdge : node.primaryEdges) {
            paintNode(g, primaryEdge.endNode);
        }

        // paint label:
		FontMetrics metrics = g.getFontMetrics();
		String label = getLabel(node.flowNode);
		int width = metrics.stringWidth(label);
		if (width > 2 * RADIUS)
		{
			width = 2 * RADIUS;
		}
		g.drawString(label, node.xPos - width / 2, node.yPos);

		// paint edges:
		paintEdges(g, node);
	}

	private void paintEdges(Graphics g, Node node)
	{
        for (Edge primaryEdge : node.primaryEdges) {
            paintEdge(g, primaryEdge);
        }

        for (Edge secondaryEdge : node.secondaryEdges) {
            paintEdge(g, secondaryEdge);
        }
    }

	private void paintEdge(Graphics g, Edge edge)
	{
		int xDiff = edge.endNode.xPos - edge.startNode.xPos;
		int yDiff = edge.endNode.yPos - edge.startNode.yPos;

		double l = Math.sqrt(xDiff * xDiff + yDiff * yDiff);

		int deltaX = (int) (RADIUS / l * xDiff);
		int deltaY = (int) (RADIUS / l * yDiff);

		int x1 = edge.startNode.xPos + deltaX;
		int x2 = edge.endNode.xPos - deltaX;

		int y1 = edge.startNode.yPos + deltaY;
		int y2 = edge.endNode.yPos - deltaY;

		g.drawLine(x1, y1, x2, y2);

		// paint head:
		int deltaXH1 = (int) (ARROW_HEAD_XOFFSET / l * xDiff);
		int deltaYH1 = (int) (ARROW_HEAD_XOFFSET / l * yDiff);

		int deltaXH2 = (int) (-ARROW_HEAD_YOFFSET / l * yDiff);
		int deltaYH2 = (int) (ARROW_HEAD_YOFFSET / l * xDiff);

		int xh1 = x2 + deltaXH1 + deltaXH2;
		int yh1 = y2 + deltaYH1 + deltaYH2;

		int xh2 = x2 + deltaXH1 - deltaXH2;
		int yh2 = y2 + deltaYH1 - deltaYH2;

		int[] xPoints = { xh1, xh2, x2 };
		int[] yPoints = { yh1, yh2, y2 };

		g.fillPolygon(xPoints, yPoints, 3);
	}

	private class Node
	{
		/**
		 * the edges that make up the main tree structure
		 */
		private Edge[] primaryEdges;

		/**
		 * The other edges
		 */
		private Edge[] secondaryEdges;

		private int xPos;

		private int yPos;

		private boolean highlighted;

		private ExecutionState state;

		private FlowNode flowNode;

		public Node(ExecutionState state)
		{
			this.state = state;
			if (state != null)
			{
				this.flowNode = state.getFlowNode();
			}
		}

		public Node(FlowNode node)
		{
			this.flowNode = node;
		}

		public int treeWidth()
		{
			int width = 1;

            for (Edge primaryEdge : primaryEdges) {
                width += primaryEdge.endNode.treeWidth();
            }

            return width;
		}
	}

	private class Edge
	{
		private Node startNode;

		private Node endNode;

		public Edge(Node startNode, Node endNode)
		{
			this.startNode = startNode;
			this.endNode = endNode;
		}

	}
}
