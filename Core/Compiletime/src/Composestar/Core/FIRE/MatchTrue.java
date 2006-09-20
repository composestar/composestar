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

public class MatchTrue extends FilterLeaf
{
	Symbol symbol = null;
	
	public String toString () 
	{
		return "matchTrue()";
	} 

	public StatusColumn calculateStatus(StatusColumn status, StateTable stateTable)
	{
		return status;
	}

	public ActionNode createNode()
	{	
		return new FilterActionNode("MatchTrue");
	}


}	
