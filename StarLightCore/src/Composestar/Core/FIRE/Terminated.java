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

public class Terminated extends FilterLeaf
{
	public String toString() {return "TERMINATED()"; }

	public StatusColumn calculateStatus(StatusColumn status, StateTable stateTable)
	{
		System.out.println ("TERMINATED Reached. Should be removed.");
		System.exit(-1);
		return null;
/*
		stateTable.snapshot(status,this);

		return status;
		*/
	}

	public ActionNode createNode()
	{	
		return new FilterActionNode("terminated");
	}

}
