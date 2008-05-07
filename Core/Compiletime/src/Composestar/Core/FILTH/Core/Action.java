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

import java.util.LinkedList;

import Composestar.Core.SANE.FilterModuleSuperImposition;

/**
 * Represents an superimposed filter module in the dependency graph
 */
public class Action implements Parameter
{
	/**
	 * Identifier for this name, the fully qualified name of the superimposed
	 * filter module
	 */
	private String name;

	/**
	 * The superimposed filtermodule
	 */
	private FilterModuleSuperImposition fmsi;

	private Boolean rvalue;

	private boolean present;

	private boolean executed;

	private boolean executable;

	protected LinkedList<Rule> rules = new LinkedList<Rule>();

	public Action(String inname, Boolean inrvalue, boolean inpresent)
	{
		name = inname;
		rvalue = inrvalue;
		present = inpresent;
		if (present)
		{
			setExecutable(true);
		}
	}

	public Action(FilterModuleSuperImposition infmsi, Boolean inrvalue, boolean inpresent)
	{
		this(infmsi.getFilterModule().getQualifiedName(), inrvalue, inpresent);
		fmsi = infmsi;
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

	public LinkedList<Rule> getRules()
	{
		return rules;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return name;
	}

	/**
	 * @return the action identifier
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * @return the associated filter module super imposition
	 */
	public FilterModuleSuperImposition getFilterModuleSuperImposition()
	{
		return fmsi;
	}

	@Deprecated
	public static void insert(Action a, Graph g)
	{
		g.addEdge(new Edge("pre_soft", g.getRoot(), new Node(a)));
	}

	public static Node lookup(Action a, Graph g)
	{
		Node current;
		for (Object o : g.getNodes())
		{
			current = (Node) o;
			if (current.getAction() != null && current.getAction().equals(a))
			{
				return current;
			}
		}
		return null;
	}

	/**
	 * @param inname
	 * @param g
	 * @return
	 * @deprecated use {@link Graph#findNodeByName(String)}
	 */
	@Deprecated
	public static Node lookupByName(String inname, Graph g)
	{
		Node current;
		for (Object o : g.getNodes())
		{
			current = (Node) o;
			/* the root element is only a string, we skip it */
			if (current.getAction() == null)
			{
				continue;
			}

			if (((Action) current.getAction()).getName().equals(inname))
			{
				return current;
			}
		}
		return null;
	}

	public Boolean evaluate()
	{
		if (isExecuted())
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
