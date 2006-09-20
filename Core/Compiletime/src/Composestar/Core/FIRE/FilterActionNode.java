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


public class FilterActionNode extends ActionNode
{
	protected String filterName = "";

	// ctor
	public FilterActionNode(String filterName)
	{
		setFilterName(filterName);
	}

        public void setFilterName (String _filterName)
        {
                filterName = _filterName;
        }

        public String getFilterName ()
        {
                return filterName;
        }

	/////////////
	// Functions with knowledge about the children.
	/////////////
	public boolean isLeaf ()
	{
		return !hasChildren();
	}

	public String toString ()
	{
		return "filter: " + filterName + getMessageString();
	}

	protected boolean subsetOfSingle (Node rhs)
	{
		if (!super.subsetOfSingle(rhs)) return false;
		
		if (rhs instanceof FilterActionNode)
		{
			return ("".equals(((FilterActionNode)rhs).getFilterName()) ||
					((FilterActionNode)rhs).getFilterName().equals(getFilterName()));
		}

		return true;
	}

/*
	public String toString()
	{

		String str = super.toString();
		str += "-> " + filterName + "\n";
		return str;
	}
	*/


}
