/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2004-2006 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */
package Composestar.Core.SECRET3;

import java.io.Serializable;
import java.util.List;

import Composestar.Core.CpsRepository2.TypeSystem.CpsSelector;
import Composestar.Core.FIRE2.model.ExecutionTransition;
import Composestar.Core.SECRET3.Model.ConflictRule;
import Composestar.Core.SECRET3.Model.Resource;

/**
 * A detected conflict. Conflicts are associated with a selector to a filterset,
 * which is related to a concern.
 */
public class Conflict implements Serializable
{
	private static final long serialVersionUID = -7017897791526259274L;

	/**
	 * The resource that contains a violation
	 */
	private Resource resource;

	/**
	 * The rule that was violated
	 */
	private ConflictRule rule;

	// FIXME: implement this in a serializable way, extended transitions/states
	// shouldn't not be serialized and are also not runtime safe.
	/**
	 * The trace of execution transitions in the execution model of the
	 * concern+selector that lead to the violation of a rule
	 */
	private transient List<ExecutionTransition> trace;

	/**
	 * The used selector which resulted in a violation
	 */
	private CpsSelector selector;

	/**
	 * The sequence of operations that lead to the violation
	 */
	private List<String> operations;

	public void setResource(Resource inresource)
	{
		resource = inresource;
	}

	/**
	 * @see #resource
	 */
	public Resource getResource()
	{
		return resource;
	}

	/**
	 * @param intrace the trace of the violation
	 */
	public void setTrace(List<ExecutionTransition> intrace)
	{
		trace = intrace;
	}

	/**
	 * @see #trace
	 */
	public List<ExecutionTransition> getTrace()
	{
		return trace;
	}

	/**
	 * @see #rule
	 * @param inrule
	 */
	public void setRule(ConflictRule inrule)
	{
		rule = inrule;
	}

	/**
	 * @see #rule
	 * @return
	 */
	public ConflictRule getRule()
	{
		return rule;
	}

	/**
	 * @see #selector
	 * @param sel
	 */
	public void setSelector(CpsSelector sel)
	{
		selector = sel;
	}

	/**
	 * @see #selector
	 * @return
	 */
	public CpsSelector getSelector()
	{
		return selector;
	}

	/**
	 * @see #operations
	 * @param ops
	 */
	public void setOperations(List<String> ops)
	{
		operations = ops;
	}

	/**
	 * @see #operations
	 * @return
	 */
	public List<String> getOperations()
	{
		return operations;
	}
}
