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
import java.util.LinkedList;

public class Node
{
	private LinkedList incomingEdges = new LinkedList();

	private LinkedList outgoingEdges = new LinkedList();

	private Object _element;

	public Node(Object element)
	{
		_element = element;
	}

	public Object getElement()
	{
		return _element;
	}

	public void addIncomingEdge(Edge e)
	{
		incomingEdges.add(e);
	}

	public void addOutgoingEdge(Edge e)
	{
		outgoingEdges.add(e);
	}

	public LinkedList getIncomingEdges()
	{
		return incomingEdges;
	}

	public LinkedList getOutgoingEdges()
	{
		return outgoingEdges;
	}
}
