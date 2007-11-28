/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2004-2006 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */
package Composestar.Core.CKRET;

import java.io.Serializable;

import Composestar.Core.CKRET.Config.ConflictRule;
import Composestar.Core.CKRET.Config.Resource;

/**
 * A detected conflict
 */
public class Conflict implements Serializable
{
	private static final long serialVersionUID = -7017897791526259274L;

	private Resource resource;

	private String sequence;

	private ConflictRule rule;

	public void setResource(Resource inresource)
	{
		resource = inresource;
	}

	public Resource getResource()
	{
		return resource;
	}

	public void setSequence(String insequence)
	{
		sequence = insequence;
	}

	public String getSequence()
	{
		return sequence;
	}

	public void setRule(ConflictRule inrule)
	{
		rule = inrule;
	}

	public ConflictRule getRule()
	{
		return rule;
	}
}
