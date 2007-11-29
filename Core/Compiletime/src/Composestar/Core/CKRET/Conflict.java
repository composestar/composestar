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
import java.util.Collections;
import java.util.List;

import Composestar.Core.CKRET.Config.ConflictRule;
import Composestar.Core.CKRET.Config.Resource;
import Composestar.Core.FIRE2.model.ExecutionTransition;

/**
 * A detected conflict. Conflicts are associated with a selector to a filterset,
 * which is related to a concern.
 */
public class Conflict implements Serializable
{
	private static final long serialVersionUID = -7017897791526259274L;

	private Resource resource;

	private ConflictRule rule;

	// FIXME: implement this in a serializable way, extended transitions/states
	// shouldn't not be serialized and are also not runtime safe.
	private transient List<ExecutionTransition> trace;

	private String selector;

	public void setResource(Resource inresource)
	{
		resource = inresource;
	}

	public Resource getResource()
	{
		return resource;
	}

	public void setTrace(List<ExecutionTransition> intrace)
	{
		trace = intrace;
	}

	public List<ExecutionTransition> getTrace()
	{
		return Collections.unmodifiableList(trace);
	}

	public void setRule(ConflictRule inrule)
	{
		rule = inrule;
	}

	public ConflictRule getRule()
	{
		return rule;
	}

	public void setSelector(String sel)
	{
		selector = sel;
	}

	public String getSelector()
	{
		return selector;
	}
}
