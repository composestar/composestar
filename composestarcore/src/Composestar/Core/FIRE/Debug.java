package Composestar.Core.FIRE;

/**
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 * 
 * $Id: Debug.java,v 1.1 2006/02/13 11:16:55 pascal Exp $
 * 
**/

public class Debug
{
	private static int currentMode = 0;

	public static void setMode(int mode){currentMode = mode;}

	public static int getMode () {return currentMode;}

	public static void out (int mode, String strOut) 
	{
		if (currentMode >= mode) System.out.println (strOut);
	}


	/*  
	 * Although this is a very clever way of using a constructor and destructor :-). There
	 * is a small chance that the finalize() method is called too late. 
	 * In my test cases no problems appeared.
	 */
	
	private String currentAction = "";
	private int modeUsed = 0;
	public Debug (int mode, String action)
	{
		modeUsed = mode;
		currentAction = action;

		Debug.out (modeUsed, currentAction + " start");
	}

	public void finalize() throws Throwable
	{
		Debug.out (modeUsed, currentAction + " stop");
        super.finalize();
	}
	


}
