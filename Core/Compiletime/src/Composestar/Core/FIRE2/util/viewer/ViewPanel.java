/*
 * Created on 17-aug-2006
 *
 */
package Composestar.Core.FIRE2.util.viewer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JPanel;

import Composestar.Core.FIRE2.model.ExecutionModel;
import Composestar.Core.FIRE2.model.ExecutionState;
import Composestar.Core.FIRE2.model.ExecutionTransition;
import Composestar.Core.FIRE2.model.FlowModel;
import Composestar.Core.FIRE2.model.FlowNode;
import Composestar.Core.FIRE2.model.FlowTransition;
import Composestar.Core.RepositoryImplementation.DeclaredRepositoryEntity;

public class ViewPanel extends JPanel
{
	private static final long serialVersionUID = -5738123083267682871L;

	private LinkedList<Object> unProcessed;

	private HashMap<Object, Node> nodeMap;

	private HashMap<String, Integer> labelCounter;

	private Node rootNode;

	private static final int RADIUS = 40;

	private static final int MARGIN = 10;

	private static final int ARROW_HEAD_XOFFSET = -10;

	private static final int ARROW_HEAD_YOFFSET = -5;

	private int height = 0;

	private static final Edge[] EmptyEdgeArray = {};

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

		unProcessed = new LinkedList<Object>();
		nodeMap = new HashMap<Object, Node>();
		labelCounter = new HashMap<String, Integer>();

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
		List<Edge> primaryEdges = new ArrayList<Edge>();
		List<Edge> secondaryEdges = new ArrayList<Edge>();

		Node node = nodeMap.get(flowNode);

		for (FlowTransition transition : flowNode.getTransitionsEx())
		{
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
				Node nextNode = nodeMap.get(nextFlowNode);

				Edge edge = new Edge(node, nextNode);
				secondaryEdges.add(edge);
			}
		}

		node.primaryEdges = primaryEdges.toArray(EmptyEdgeArray);
		node.secondaryEdges = secondaryEdges.toArray(EmptyEdgeArray);
	}

	private void initialize(ExecutionModel model)
	{
		initialize();

		rootNode = new Node((ExecutionState) null);

		unProcessed = new LinkedList<Object>();
		nodeMap = new HashMap<Object, Node>();
		labelCounter = new HashMap<String, Integer>();

		List<Edge> edges = new ArrayList<Edge>();

		Iterator<ExecutionState> it = model.getEntranceStates();
		while (it.hasNext())
		{
			ExecutionState obj = it.next();
			Node node = new Node(obj);
			nodeMap.put(obj, node);

			unProcessed.add(obj);

			Edge edge = new Edge(rootNode, node);
			edges.add(edge);
		}

		rootNode.primaryEdges = edges.toArray(new Edge[0]);
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
		for (String s : node.getNamesEx())
		{
			addCount(s);
		}
	}

	private void addCount(String str)
	{
		Integer i = labelCounter.get(str);

		int add = 1;
		if (str.equals("AcceptCallAction") || str.equals("AcceptReturnAction") || str.equals("RejectCallAction")
				|| str.equals("RejectReturnAction"))
		{
			add = 100000;
		}

		if (i == null)
		{
			labelCounter.put(str, Integer.valueOf(add));
		}
		else
		{
			labelCounter.put(str, Integer.valueOf(i.intValue() + add));
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

		for (String str : flowNode.getNamesEx())
		{
			Integer i = labelCounter.get(str);

			if (i < currentCount)
			{
				currentLabel = str;
				currentCount = i;
			}
		}

		return currentLabel;
	}

	public void highlightNodes(Collection<ExecutionState> executionStates)
	{
		// first remove highlight:
		Collection<Node> nodes = nodeMap.values();
		for (Object node1 : nodes)
		{
			Node node = (Node) node1;
			node.highlighted = false;
		}

		// then add highlight:
		Iterator<ExecutionState> iter = executionStates.iterator();
		while (iter.hasNext())
		{
			ExecutionState state = iter.next();
			Node node = nodeMap.get(state);
			node.highlighted = true;
		}

		repaint();
	}

	private void processExecState(ExecutionState state)
	{
		List<Edge> primaryEdges = new ArrayList<Edge>();
		List<Edge> secondaryEdges = new ArrayList<Edge>();

		Node node = nodeMap.get(state);

		for (ExecutionTransition transition : state.getOutTransitionsEx())
		{
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
				Node nextNode = nodeMap.get(nextState);

				Edge edge = new Edge(node, nextNode);
				secondaryEdges.add(edge);
			}
		}

		node.primaryEdges = primaryEdges.toArray(EmptyEdgeArray);
		node.secondaryEdges = secondaryEdges.toArray(EmptyEdgeArray);
	}

	private void calculatePositions()
	{
		int width = calculatePosition(rootNode, 0, 0);
		height += 4 * (MARGIN + RADIUS);

		setPreferredSize(new Dimension(width, height));
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
		for (Edge primaryEdge : node.primaryEdges)
		{
			width += calculatePosition(primaryEdge.endNode, xOffset + width, newYOffset);
		}

		height = Math.max(yOffset, height);

		return width;
	}

	@Override
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

		for (Edge primaryEdge : node.primaryEdges)
		{
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

		// paint name of rep entity
		if (node.flowNode != null && node.flowNode.getRepositoryLink() instanceof DeclaredRepositoryEntity)
		{
			label = ((DeclaredRepositoryEntity) node.flowNode.getRepositoryLink()).getName();
			width = metrics.stringWidth(label);
			if (width > 2 * RADIUS)
			{
				width = 2 * RADIUS;
			}
			g.drawString(label, node.xPos - width / 2, node.yPos + (int) (metrics.getHeight() * 1.25));
		}
		if (node.state != null)
		{
			g.drawString(node.state.getMessage().toString(), node.xPos - width / 2, node.yPos + 2
					* (int) (metrics.getHeight() * 1.25));
			g.drawString(node.state.getSubstitutionMessage().toString(), node.xPos - width / 2, node.yPos + 3
					* (int) (metrics.getHeight() * 1.25));
		}

		// paint edges:
		paintEdges(g, node);
	}

	private void paintEdges(Graphics g, Node node)
	{
		for (Edge primaryEdge : node.primaryEdges)
		{
			paintEdge(g, primaryEdge);
		}

		for (Edge secondaryEdge : node.secondaryEdges)
		{
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

	private static class Node
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
				flowNode = state.getFlowNode();
			}
		}

		public Node(FlowNode node)
		{
			flowNode = node;
		}

		public int treeWidth()
		{
			int width = 1;

			for (Edge primaryEdge : primaryEdges)
			{
				width += primaryEdge.endNode.treeWidth();
			}

			return width;
		}
	}

	private static class Edge
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
