package Composestar.Core.FIRE;

/**
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 * 
 * $Id: Iterator.java,v 1.1 2006/02/13 11:16:56 pascal Exp $
 * 
**/

import java.util.LinkedList;
import java.util.Stack;

public abstract class Iterator implements Cloneable
{
	// (friend) datamembers
	Node parent;
	int currentChild;

	// Store PathNodes on the stack.
	Stack stack = new Stack();
	class PathNode
	{
		public Node parent;
		public int child;
	}

	// methods 
	public Iterator() {}

	public Iterator(Node _tree)
	{
		parent = new VoidNode();
		parent.addChild(_tree);
		currentChild = 0;
	}

	protected void push()
	{
		PathNode pn = new PathNode();
		pn.parent = parent;
		pn.child = currentChild;

		stack.push(pn);
	}

	protected void pop()
	{
		PathNode pn = (PathNode) stack.pop ();
		parent = pn.parent;
		currentChild = pn.child;
	}


	public abstract void first();
	public abstract void next();
	public abstract boolean isDone();

	public abstract Node getNode();
	public abstract int getDepth();

	public String toString ()
	{
		return String.valueOf (stack.size());
	}

	public Object clone()
	{
		try 
		{
			Iterator i = (Iterator) super.clone();
			i.stack = (Stack) stack.clone();

			i.parent = parent;
			i.currentChild = currentChild;

			return i;
		} 
		catch (CloneNotSupportedException e)
		{
			return null;
		}
	}

	public void skipTo (Node compareWith)
	{
		next();
		while (!isDone() && !getNode().subsetOfExpression(compareWith)) next();
	}

	public LinkedList getPath()
	{
		LinkedList ll = new LinkedList ();

		push();
		for (int i = 0; i < stack.size(); i++)
		{
			PathNode pn = (PathNode) stack.get(i);
			ll.add((Node) pn.parent.getChild(pn.child));
		}
		pop();

		return ll;
	}
}
