/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id: CMaster.java,v 1.1 2006/09/01 15:31:20 johantewinkel Exp $
 */

package Composestar.C.MASTER;

import Composestar.Core.Master.Master;

/**
 * Main entry point for the CompileTime. The Master class holds coreModules and
 * executes them in the order they are added.
 */
public class CMaster extends Master
{

	/**
	 * Compose* main function. Creates the Master object. Adds the desired
	 * modules and then calls run on each of them in the order that they where
	 * added.
	 * 
	 * @param args
	 */
	public static void main(String[] args)
	{
		if (args.length == 0)
		{
			System.out.println("Usage: java -jar ComposestarC.jar <config file>");
			return;
		}
		main(CMaster.class, args);
	}
}
