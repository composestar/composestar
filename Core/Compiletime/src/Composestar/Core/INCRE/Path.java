package Composestar.Core.INCRE;

import java.util.ArrayList;
import java.util.Iterator;

import Composestar.Core.Exception.ModuleException;

public class Path
{
	private ArrayList nodes = new ArrayList();

	public Path(){}

	/**
	 * Adds a node to the vector containing nodes 
	 */
	public void addNode(Node n)
	{
		this.nodes.add(n);	
	}
	
	public Node getFirstNode(){
		return (Node)this.nodes.get(0);
	}

	public Object follow(Object obj) throws ModuleException
	{
		// follow path and return final object
		if(!nodes.isEmpty())
		{
			Iterator pathnodes = nodes.iterator();
			Object nextobject = obj;

			while(pathnodes.hasNext())
			{
				try 
				{
					Node currentnode = (Node)pathnodes.next();
					nextobject = currentnode.visit(nextobject);
				}
				catch(NullPointerException npe){ 
					return null; 
				}
			}

			return nextobject;
		}
		else
			return null;
	}

	public boolean isEmpty()
	{
		return nodes.isEmpty();
	}
}