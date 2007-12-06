package Composestar.Core.INCRE;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import Composestar.Core.Exception.ModuleException;

public class Path implements Serializable
{
	private static final long serialVersionUID = 3702399602907107235L;

	private List<Node> nodes = new ArrayList<Node>();

	public Path()
	{}

	/**
	 * Adds a node to the vector containing nodes
	 * 
	 * @param n
	 */
	public void addNode(Node n)
	{
		nodes.add(n);
	}

	public Node getFirstNode()
	{
		return nodes.get(0);
	}

	public Object follow(Object obj) throws ModuleException
	{
		// follow path and return final object
		if (!nodes.isEmpty())
		{
			Iterator<Node> pathnodes = nodes.iterator();
			Object nextobject = obj;

			while (pathnodes.hasNext())
			{
				try
				{
					Node currentnode = pathnodes.next();
					nextobject = currentnode.visit(nextobject);
				}
				catch (NullPointerException npe)
				{
					return null;
				}
			}

			return nextobject;
		}
		else
		{
			return null;
		}
	}

	public boolean isEmpty()
	{
		return nodes.isEmpty();
	}
}
