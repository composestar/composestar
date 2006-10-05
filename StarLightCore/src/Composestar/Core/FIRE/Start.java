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

public class Start extends FilterLeaf
{
	public String toString() { if (Debug.getMode() > 2) return "START()"; else return "start"; }

	public StatusColumn calculateStatus(StatusColumn status, StateTable stateTable)
	{
		stateTable.snapshot(status, this);
		return status;
	}

	public ActionNode createNode()
	{	
		return new FilterActionNode("start");
	}

}
