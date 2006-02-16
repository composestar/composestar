package Composestar.Core.FIRE;

/**
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 * 
 * $Id: DispatchFilter.java,v 1.1 2006/02/13 11:16:55 pascal Exp $
 * 
**/

class DispatchFilter extends Filter
{
	public DispatchFilter()
	{
		acceptContinues = false;
		rejectContinues = true;
		acceptAction = true;
		rejectAction = false;

		//doesMeta = false;
	}
	public String toString() { return "Dispatch";};

	public StatusColumn calc (StatusColumn status, StateTable stateTable, Action component )
	{
		stateTable.snapshot(status, component);
		status.finish(true);

		status.setAllValues(true); // TODO check this out.
		return status;
	}
	

	public ActionNode createNode()
	{
		return new FilterActionNode(toString());
	}


}
