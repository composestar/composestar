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
import java.util.Iterator;
import java.util.LinkedList;

public class Action implements Parameter
{
	private String name;

	private Boolean rvalue;

	private boolean present;

	private boolean executed = false;

	private boolean executable = false;

	protected LinkedList rules = new LinkedList();

	public Action(String inname, Boolean inrvalue, boolean present)
	{
		name = inname;
		rvalue = inrvalue;
		if (present)
		{
			this.setExecutable(true);
		}
	}

	public Boolean getReturnValue()
	{
		return rvalue;
	}

	public void setReturnValue(Boolean inrvalue)
	{
		rvalue = inrvalue;
	}

	public boolean isPresent()
	{
		return present;
	}

	public boolean isExecuted()
	{
		return executed;
	}

	public void setExecuted()
	{
		executed = true;
	}

	public boolean isExecutable()
	{
		return executable;
	}

	public void setExecutable(boolean inexecutable)
	{
		executable = inexecutable;
	}

	public void addRule(Rule rule)
	{
		rules.add(rule);
	}

	public LinkedList getRules()
	{
		return rules;
	}

	public String toString()
	{
		return name;
	}

	public String getName()
	{
		return name;
	}

	public static void insert(Action a, Graph g)
	{
		g.addEdge(new Edge("pre_soft", g.getRoot(), new Node(a)));
	}

	public static Node lookup(Action a, Graph g)
	{
		Node current;
		for (Iterator i = g.getNodes().iterator(); i.hasNext();)
		{
			current = (Node) i.next();
			if (current.getElement().equals(a))
			{
				return current;
			}
		}
		return null;
	}

	public static Node lookupByName(String inname, Graph g)
	{
		Node current;
		for (Iterator i = g.getNodes().iterator(); i.hasNext();)
		{
			current = (Node) i.next();
			/* the root element is only a string, we skip it */
			if (current.getElement() instanceof java.lang.String)
			{
				continue;
			}

			if (((Action) current.getElement()).getName().equals(inname))
			{
				return current;
			}
		}
		return null;
	}

	public Boolean evaluate()
	{
		if (this.isExecuted())
		{
			return getReturnValue();
		}
		return null;
	}

	public void execute()
	{
		executed = true;
		// TO DO: execute the superimposed behaviour
	}

}
