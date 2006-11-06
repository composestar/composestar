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

import java.util.LinkedList;

public class ActionNode extends Node implements Cloneable
{

	protected FilterComponent fc = null;

	// Datamembers
	protected LinkedList conditions = new LinkedList();

	protected Symbol target = null;

	protected Symbol selector = null;

	protected String preferedSelector = "";

	private static final Symbol[] EmptySymbolList = {};

	// ///// Target /////////////
	public void setTarget(Symbol targetSymbol)
	{
		target = targetSymbol;
	}

	public void setTarget(String targetString)
	{
		setTarget(stringToSymbol(targetString, 1));
	}

	public Symbol getTarget()
	{
		return target;
	}

	// ///// Selector /////////////
	public void setSelector(Symbol selectorSymbol)
	{
		selector = selectorSymbol;
	}

	// Also prefered selector set. For signatureMatching.
	public void setSelector(String selectorString)
	{
		preferedSelector = selectorString;
		setSelector(stringToSymbol(selectorString, 2));

	}

	public Symbol getSelector()
	{
		return selector;
	}

	public String getPreferedSelector()
	{
		return preferedSelector;
	}

	// ///// Conditions /////////////

	public Object clone() throws CloneNotSupportedException
	{
		ActionNode n = (ActionNode) super.clone();
		n.conditions = new LinkedList();
		// n.target = null;
		return n;
	}

	public void addCondition(Symbol s)
	{
		if (!conditions.contains(s)) conditions.add(s);
	}

	public void addCondition(String str)
	{
		addCondition(stringToSymbol(str, 0));
	}

	public void setConditions(Symbol[] conditionArray)
	{
		for (int i = 0; i < conditionArray.length; i++)
		{
			addCondition(conditionArray[i]);
		}
	}

	public boolean containsCondition(Symbol s)
	{
		return conditions.contains(s);
	}

	public Symbol[] getConditions()
	{
		return (Symbol[]) conditions.toArray(EmptySymbolList);
	}

	protected Symbol stringToSymbol(String symbolString, int symbolType)
	{
		SymbolTable st = SymbolTable.getInstance();
		Symbol symbol = st.getSymbol(symbolString, symbolType);

		if (symbol == null && symbolType == 1 && fireInfo != null)
		{
			symbol = fireInfo.getConcernSymbol(symbolString);
		}

		if (symbol == null && (symbolType == 1 || symbolType == 2))
		{
			// System.out.println ("Returning star instead of symbol: " +
			// symbolString);
			return st.getSymbol("*", symbolType);
		}
		/*
		 * if (symbol == null) { Exception e = new Exception ("stringToSymbol
		 * cannot find the given String in the symbolTable");
		 * e.printStackTrace(); }
		 */

		return symbol;
	}

	public void addSymbol(Symbol s)
	{
		switch (s.getType())
		{
			case 0:
				conditions.add(s);
				break;
			case 1:
				setTarget(s);
				break;
			case 2:
				setSelector(s);
				break;
			default:
				throw new RuntimeException("FIRE.addSymbol:Symbol type not known " + s.toString());
		}
	}

	public void setFilterComponent(FilterComponent _fc)
	{
		fc = _fc;
	}

	public FilterComponent getFilterComponent()
	{
		return fc;
	}

	public String toString()
	{
		return "ActionNode";
	}

	// Checks if the rhs is an subset of the lhs.
	// rhs E lhs
	// ------------------------------
	// rhs = *, --> match
	// rhs = null --> match
	// rhs = a, lhs = a --> match
	protected boolean subsetOfSingle(Node rhs)
	{
		if (rhs instanceof ActionNode)
		{
			ActionNode act = (ActionNode) rhs;

			if (isPerfectMatch())
			{
				if (act.getTarget() != null && !act.getTarget().equals(getTarget())) return false;
				if (act.getSelector() != null && !act.getSelector().equals(getSelector())) return false;
			}
			else
			{
				if (act.getTarget() != null && !act.getTarget().getName().equals("*")
						&& !act.getTarget().equals(getTarget())) return false;
				if (act.getSelector() != null && !act.getSelector().getName().equals("*")
						&& !act.getSelector().equals(getSelector())) return false;
			}

			// Lazy way to fix it
			if (checkConditions() && !act.conditions.isEmpty())
			{
				if (act.conditions.size() != conditions.size()) return false;

				for (int i = 0; i < act.conditions.size(); i++)
				{
					if (!conditions.contains(act.conditions.get(i))) return false;
				}
			}

		}

		return true;
	}

	protected String getMessageString()
	{
		String target = (getTarget() == null ? "" : getTarget().toString());
		String selector = (getSelector() == null ? "" : getSelector().toString());

		// if (getTarget() != null || getSelector() != null || conditions.size()
		// > 0)
		// {
		return ' ' + target + '.' + selector + ' ' + conditions.toString();
		// }

		// return "";
	}
}
