/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2007 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */

package Composestar.Visualization.Model;

import Composestar.Core.Master.CompileHistory;

/**
 * Is responsible for view construction/caching.
 * 
 * @author Michiel Hendriks
 */
public class ViewManager
{
	protected CompileHistory history;
	
	protected ProgramView programView;
	
	public ViewManager(CompileHistory inHistory)
	{
		history = inHistory;
	}
	
	public ProgramView getProgramView()
	{
		if (programView == null)
		{
			programView = new ProgramView(history);
		}
		return programView;
	}
}
