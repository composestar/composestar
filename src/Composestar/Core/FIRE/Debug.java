package Composestar.Core.FIRE;

/**
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente. Licensed under LGPL v2.1 or (at your
 * option) any later version. [http://www.fsf.org/copyleft/lgpl.html] $Id:
 * Debug.java 2282 2006-10-25 14:12:31Z arjanderoo $
 */

public class Debug
{
	private static int currentMode = 0;

	public static void setMode(int mode)
	{
		currentMode = mode;
	}

	public static int getMode()
	{
		return currentMode;
	}

	public static void out(int mode, String strOut)
	{
		if (currentMode >= mode)
		{
			System.out.println(strOut);
		}
	}

	/*
	 * Although this is a very clever way of using a constructor and destructor
	 * :-). There is a small chance that the finalize() method is called too
	 * late. In my test cases no problems appeared.
	 */

	public Debug(int mode, String action)
	{

	// Debug.out (modeUsed, currentAction + " start");
	}
}
