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

import javax.swing.Icon;

import Composestar.Visualization.Model.CpsJGraph;

/**
 * @author Michiel Hendriks
 */
public abstract class ActiveGraphAction extends VisComAction
{

	public ActiveGraphAction()
	{
		super();
	}

	public ActiveGraphAction(String name, Icon icon)
	{
		super(name, icon);
	}

	public ActiveGraphAction(String name)
	{
		super(name);
	}

	/**
	 * Execute an action on the currently active graph. Return true if the
	 * action was succesfully executed.
	 * 
	 * @param activeGraph
	 * @return
	 */
	public abstract void execute(CpsJGraph activeGraph);

	public void actionPerformed(ActionEvent event)
	{
		CpsJGraph activeGraph = graphProvider.getCurrentGraph();
		if (activeGraph != null)
		{
			execute(activeGraph);
		}
	}
}
