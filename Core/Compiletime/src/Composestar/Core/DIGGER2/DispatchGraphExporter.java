/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2007 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */

package Composestar.Core.DIGGER2;

import Composestar.Core.Exception.ModuleException;

/**
 *
 * @author Michiel Hendriks
 */
public abstract class DispatchGraphExporter
{
	protected DispatchGraph graph;
	
	public DispatchGraphExporter(DispatchGraph inGraph)
	{
		graph = inGraph;
	}
	
	public abstract void export() throws ModuleException;
}
