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


public class DepthFirstIterator extends ChildIterator 
{
	protected boolean done;

	public DepthFirstIterator(Node _node)
	{
		super(_node);
	}

	public DepthFirstIterator(NodeIterator itr)
	{
		super(itr);
	}


	public void first()
	{
		done = false;

		while (canStepBack()) super.stepBack();
		super.first();
	}
	
	public void stepBack()
	{
		pop();
		nextHorizontal();
	}

	public void nextHorizontal()
	{
		if (hasNext()) super.next();
		else if (canStepBack()) stepBack();
		else done = true;
	}

	public void next()
	{
		if (canStepInto()) stepInto();
		else nextHorizontal();
	}

	public boolean isDone()
	{
		return done;
	}

	public Object clone() throws CloneNotSupportedException
	{
		DepthFirstIterator i = (DepthFirstIterator) super.clone();
		i.done = done;
		return i;
	}
}
