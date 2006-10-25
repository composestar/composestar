/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */

package Composestar.Core.Master;

/**
 * Main entry point for the CompileTime. The Master class holds coreModules
 * and executes them in the order they are added.
 */
public abstract class Master
{	
	public static final String RESOURCES_KEY = "Composestar.Core.Master.CommonResources";
	
	public abstract void run();		
}
