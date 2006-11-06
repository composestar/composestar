package Composestar.Core.FIRE;

import java.util.Vector;

/**
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente. Licensed under LGPL v2.1 or (at your
 * option) any later version. [http://www.fsf.org/copyleft/lgpl.html] $Id:
 * Filter.java 1515 2006-09-20 12:49:07Z reddog33hummer $
 */

public abstract class Filter
{
	// Default, Error filter
	public boolean acceptContinues = true; // Continue to next filter;

	public boolean rejectContinues = false; // Stop with processing, when

	// rejected.

	public boolean acceptAction = false; // When accepted, add this to the

	// state list?

	public boolean rejectAction = true; // When rejected, add this to the state

	// list?

	// public boolean doesMeta = false; // All conditions, targets, selectors
	// are unknown?

	public abstract String toString();

	public abstract StatusColumn calc(StatusColumn status, StateTable stateTable, Action action);

	public abstract ActionNode createNode();

	Vector parameters;

	public void setParameters(Vector parameters)
	{
		this.parameters = parameters;
	}

	public Vector getParameters()
	{
		return this.parameters;
	}

}
