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
import Composestar.Utils.*;

public class Tor extends FilterComposite
{
	public String toString()
	{
		return "TOR";
	}

	public StatusColumn calculateStatus(StatusColumn status, StateTable stateTable)
	{
		// Debug.out(Debug.MODE_INFORMATION, "FIRE" , "TOR");

		// Debug.out (3, "TOR start ");
		// a | | (b | c)
		//
		// [ TOR ]
		//
		// a / / b (a & ~b)\ \ c

		// a
		StatusColumn a;
		try
		{
			a = (StatusColumn) status.clone();
		}
		catch (CloneNotSupportedException e)
		{
			e.printStackTrace();
			a = status;
		}

		// b
		fc1.calculateStatus(status, stateTable);

		StatusColumn b;
		try
		{
			b = (StatusColumn) status.clone();
		}
		catch (CloneNotSupportedException e)
		{
			e.printStackTrace();
			b = status;
		}

		// (a & ~b)
		Logic.not(status);
		Logic.and(status, a);

		fc2.calculateStatus(status, stateTable);

		// (b | c)
		Logic.or(status, b);

		// Debug.out (3, "TOR stop ");
		return status;
	}
}
