package Composestar.Core.FIRE;

/**
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente. Licensed under LGPL v2.1 or (at your
 * option) any later version. [http://www.fsf.org/copyleft/lgpl.html] $Id:
 * FilterLeaf.java 1515 2006-09-20 12:49:07Z reddog33hummer $
 */

public abstract class FilterLeaf extends FilterComponent
{
	int filterNumber = -1;

	public void setFilterNumber(int number)
	{
		filterNumber = number;
	}

	public int getFilterNumber()
	{
		return filterNumber;
	}

	protected void toTreeString(StringBuffer strPart)
	{
		strPart.append(toString());
	}

	public abstract ActionNode createNode();

}
