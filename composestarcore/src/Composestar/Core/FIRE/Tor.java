package Composestar.Core.FIRE;

/**
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 * 
 * $Id: Tor.java,v 1.2 2006/02/13 11:53:08 pascal Exp $
 * 
**/
import Composestar.Utils.*;

public class Tor extends FilterComposite
{
	public String toString () {return "TOR";} 
	
	public StatusColumn calculateStatus(StatusColumn status, StateTable stateTable)
	{
		//Debug.out(Debug.MODE_INFORMATION, "FIRE" , "TOR");
		
	//	Debug.out (3, "TOR start ");
		//        a | | (b | c)
		//
		//        [ TOR ]
		//
		// a / / b    (a & ~b)\ \ c

		// a
		StatusColumn a = (StatusColumn) status.clone();

		// b
		fc1.calculateStatus((StatusColumn) status, stateTable);
		StatusColumn b = (StatusColumn) status.clone();

		// (a & ~b)
		Logic.not((StatusColumn) status);
		Logic.and((StatusColumn) status, a);

		fc2.calculateStatus((StatusColumn) status, stateTable);

		// (b | c)
		Logic.or((StatusColumn) status, b);

	//	Debug.out (3, "TOR stop ");
		return status;
	}
}
