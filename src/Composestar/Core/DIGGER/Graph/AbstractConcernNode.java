/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2006 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */
package Composestar.Core.DIGGER.Graph;

import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.DIGGER.DIGGER;
import Composestar.Utils.Debug;

/**
 * Base for concern nodes
 * 
 * @author Michiel Hendriks
 */
public abstract class AbstractConcernNode extends Node
{
	protected Concern concern;

	public AbstractConcernNode(Graph inGraph, Concern inConcern)
	{
		super(inGraph, inConcern.getQualifiedName());
		concern = inConcern;
	}

	public void setOwner(Node inOwner)
	{
		Debug.out(Debug.MODE_ERROR, DIGGER.MODULE_NAME, "Tried to set owner for a concern node: " + label);
	}
}
