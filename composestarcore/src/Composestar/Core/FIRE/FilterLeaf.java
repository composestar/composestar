package Composestar.Core.FIRE;

/**
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 * 
 * $Id: FilterLeaf.java,v 1.1 2006/02/13 11:16:56 pascal Exp $
 * 
**/


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
	
	protected void toTreeString (StringBuffer strPart)
	{
		strPart.append(toString());
	}


	public abstract ActionNode createNode();


}
