package Composestar.Core.FIRE;

/**
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 * 
 * $Id: Action.java,v 1.2 2006/03/14 12:53:54 pascal_durr Exp $
 * 
**/

import Composestar.Utils.Debug;

public class Action extends FilterLeaf
{
	Filter filter;

	public Action(Filter _filter) {filter = _filter;};
	public Action(Filter _filter, int filterNumber) 
	{
		filter= _filter;
		setFilterNumber(filterNumber);
	};
	public String toString () {return "action(" + filter + ")";} 
	public Filter getFilter() {return filter;}

	public StatusColumn calculateStatus(StatusColumn status, StateTable stateTable)
	{
		//Debug.out(Debug.MODE_INFORMATION, "FIRE" , "Visiting action: " + filter);
		return filter.calc(status, stateTable, this);
	}



	public ActionNode createNode()
	{
		return filter.createNode();
	}




}
