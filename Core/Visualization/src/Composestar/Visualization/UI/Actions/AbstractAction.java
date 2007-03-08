/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2007 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */

package Composestar.Visualization.UI.Actions;

import java.awt.event.ActionEvent;

import org.jgraph.JGraph;

/**
 * @author Michiel Hendriks
 */
public abstract class AbstractAction implements java.awt.event.ActionListener
{
	/**
	 * Execute an action on the currently active graph. Return true if the
	 * action was succesfully executed.
	 * 
	 * @param activeGraph
	 * @return
	 */
	public abstract boolean execute(JGraph activeGraph);

	public void actionPerformed(ActionEvent e)
	{
		
	}
}
