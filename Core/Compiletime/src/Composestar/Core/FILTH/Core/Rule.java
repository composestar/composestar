/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003-2006 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */
package Composestar.Core.FILTH.Core;

/**
 * @author nagyist
 */
public abstract class Rule
{
	protected String identifier;

	protected Parameter left, right;

	public Rule(Parameter inleft, Parameter inright)
	{
		left = inleft;
		right = inright;
		// the second arg. must be action always...
		if (right instanceof Action)
		{
			((Action) right).addRule(this);
		}
	}

	public String getIdentifier()
	{
		return identifier;
	}

	public Parameter getLeft()
	{
		return left;
	}

	public Parameter getRight()
	{
		return right;
	}

	public void insert(Graph g)
	{
		g
				.addEdge(new Edge(getIdentifier(), Action.lookup((Action) getLeft(), g), Action.lookup(
						(Action) getRight(), g)));
	}

	public abstract void apply();
}
