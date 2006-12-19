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

import java.util.Stack;

public class ChildIterator extends NodeIterator
{
	public ChildIterator(Node _node)
	{
		super(_node);
	}

	public ChildIterator(NodeIterator itr)
	{
		stack = (Stack) itr.stack.clone();
		parent = itr.parent;
		currentChild = itr.currentChild;
	}

	public void first()
	{
		currentChild = 0;
	}

	public int size()
	{
		return parent.numberOfChildren();
	}

	public void next()
	{
		currentChild++;
	}

	public boolean hasNext()
	{
		return (currentChild + 1 < size());
	}

	public boolean canStepInto()
	{
		return parent.getChild(currentChild).hasChildren();
	}

	public void stepInto()
	{
		push();
		parent = parent.getChild(currentChild);
		currentChild = 0; // Cannot call first(), because this method is
		// virtual.
		// And there is no way, to set it to non-virtual.
	}

	public boolean canStepBack()
	{
		return (!stack.isEmpty());
	}

	public void stepBack()
	{
		pop();
		currentChild = 0;
	}

	public boolean isDone()
	{
		return (currentChild == size());
	}

	public Node getNode()
	{
		return parent.getChild(currentChild);
	}

	public int getDepth()
	{
		return stack.size();
	}
}
