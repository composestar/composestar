package Composestar.Core.FILTH.Core;

/*
 * Created on 2-sep-2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */

/**
 * @author nagyist To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public abstract class Rule
{
	protected String identifier;

	protected Parameter _left, _right;

	public Rule(Parameter left, Parameter right)
	{
		_left = left;
		_right = right;
		if (_right instanceof Action) // the second arg. must be action
										// always...
		{
			((Action) _right).addRule(this);
		}
	}

	public String getIdentifier()
	{
		return identifier;
	}

	public Parameter getLeft()
	{
		return _left;
	}

	public Parameter getRight()
	{
		return _right;
	}

	public void insert(Graph g)
	{
		g.addEdge(new Edge(getIdentifier(), Action.lookup((Action) this.getLeft(), g), Action.lookup((Action) this
				.getRight(), g)));
	}

	public abstract void apply();
}
