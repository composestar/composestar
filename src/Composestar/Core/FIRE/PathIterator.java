package Composestar.Core.FIRE;

/**
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 * 
 * $Id$
 * 
**/


public class PathIterator extends Iterator
{
	protected PathNode [] path = null;
	protected int childInArray = 0;

	public PathIterator(Iterator itr)
	{
		itr.push(); // OOoooh I modify the original.

		path = new PathNode[itr.stack.size()];
		itr.stack.copyInto(path);
		
		// The stack already contains the 'extra' voidNode.
		parent = path[0].parent;
		currentChild = path[0].child;

		itr.pop(); // But I am gentle, I change it back. Nobody notices.
	}

	public void first()
	{
		stack.empty();
		childInArray = 0;
	}

	public int size ()
	{
		return path.length; 
	}

	public void next()
	{
		push();
		childInArray++;
	
		if (!isDone())
		{
			PathNode pn = path[childInArray];
			parent = pn.parent;
			currentChild = pn.child;
		}
	}

	public boolean hasNext()
	{
		return (childInArray + 1 < size());
	}

	public boolean isDone()
	{
		return (childInArray == size());
	}


	public Node getNode()
	{
		return parent.getChild(currentChild);
	}


	public int getDepth()
	{
		return childInArray;
	}

	public void last()
	{
		while (hasNext()) next();
	}

	public void previous()
	{
		if (childInArray > 0) --childInArray;
		pop();
	}

	public boolean isFirst()
	{
		return (childInArray == 0);
	}
}
