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
import java.util.Iterator;
import java.util.LinkedList;

public class Action implements Parameter
{
	String _name;

	public Action(String name, Boolean rvalue, boolean present)
	{
		_name = name;
		_rvalue = rvalue;
		if (present)
		{
			this.setExecutable(true);
		}
	}

	private Boolean _rvalue;

	public Boolean getReturnValue()
	{
		return _rvalue;
	}

	public void setReturnValue(Boolean rvalue)
	{
		_rvalue = rvalue;
	}

	private boolean _present;

	public boolean isPresent()
	{
		return _present;
	}

	private boolean _executed = false;

	public boolean isExecuted()
	{
		return _executed;
	}

	public void setExecuted()
	{
		_executed = true;
	}

	private boolean _executable = false;

	public boolean isExecutable()
	{
		return _executable;
	}

	public void setExecutable(boolean executable)
	{
		_executable = executable;
	}

	protected LinkedList _rules = new LinkedList();

	public void addRule(Rule rule)
	{
		_rules.add(rule);
	}

	public LinkedList getRules()
	{
		return _rules;
	}

	public String toString()
	{
		return _name;
	}

	public String getName()
	{
		return _name;
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

	public static Node lookupByName(String name, Graph g)
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

			if (((Action) current.getElement()).getName().equals(name))
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
		_executed = true;
		// TO DO: execute the superimposed behaviour
	}

}
