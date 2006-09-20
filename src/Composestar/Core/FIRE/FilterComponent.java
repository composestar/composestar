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


public abstract class FilterComponent
{
	public void printTree()
	{
		System.out.println (toTreeString());
	}
	
	public String toTreeString()
	{
		StringBuffer str = new StringBuffer("");
		toTreeString (str);
		return str.toString();
	}
	
	public abstract StatusColumn calculateStatus(StatusColumn status, StateTable stateTable);

	protected abstract void toTreeString(StringBuffer strPart);
}
