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

public class Tnot extends FilterComposite
{
	public String toString () {return "NOT";} 

	public StatusColumn calculateStatus(StatusColumn status, StateTable stateTable)
	{
		fc1.calculateStatus(status, stateTable);
		Logic.not(status);
		return status;
	}
}