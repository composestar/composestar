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

public class ErrorFilter extends Filter
{
	public String toString() { return "Error";}

    public StatusColumn calc (StatusColumn status, StateTable stateTable, Action component )
    {
        Logic.not(status);
        stateTable.snapshot(status, component);
        Logic.not(status);
        status.finish(false);

        return status;
    }

	
	public ActionNode createNode()
	{
		return new FilterActionNode(toString());
	}

	
}
