package Composestar.Core.FILTH.Core;

/*
 * Created on 2-sep-2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */

/**
 * @author nagyist
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
import java.util.*;

public class Graph
{
	private Node _root;

	private LinkedList nodes = new LinkedList();

	private LinkedList edges = new LinkedList();

	public void setRoot(Node root)
	{
		_root = root;
		nodes.add(root);
	}

	public Node getRoot()
	{
		return _root;
	}

	public LinkedList getNodes()
	{
		return nodes;
	}

	public LinkedList getEdges()
	{
		return edges;
	}

	// public void addNode(Node node){ nodes.add(node);}
	public void addEdge(Edge edge)
	{
		/* adding the edge to the graph */
		edges.add(edge);
		/* adding the nodes of the edge to graph */
		if (!nodes.contains(edge.getLeft()))
		{
			nodes.add(edge.getLeft());
		}
		if (!nodes.contains(edge.getRight()))
		{
			nodes.add(edge.getRight());
		}
	}

}
