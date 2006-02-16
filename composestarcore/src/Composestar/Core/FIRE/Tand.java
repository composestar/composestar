package Composestar.Core.FIRE;

/**
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 * 
 * $Id: Tand.java,v 1.2 2006/02/13 11:53:08 pascal Exp $
 * 
**/

import Composestar.Utils.Debug;

public class Tand extends FilterComposite
{
	public String toString () {return "TAND";} 

	public StatusColumn calculateStatus(StatusColumn status, StateTable stateTable)
	{
		//Debug.out(Debug.MODE_INFORMATION, "FIRE" ,"Visiting Tand");
		fc1.calculateStatus(status, stateTable);
		fc2.calculateStatus(status, stateTable);

		return status;
	}
}
