/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */
package Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules;

import Composestar.Core.RepositoryImplementation.ContextRepositoryEntity;

public abstract class AbstractPatternAST extends ContextRepositoryEntity
{
	public Target target;

	public MessageSelectorAST selector;

	public AbstractPatternAST()
	{
		super();
	}

	/**
	 * @return Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Target
	 */
	public Target getTarget()
	{
		return target;
	}

	/**
	 * @param targetValue
	 */
	public void setTarget(Target targetValue)
	{
		this.target = targetValue;
	}

	/**
	 * @return Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.MessageSelect
	 *         or
	 */
	public MessageSelectorAST getSelector()
	{
		return selector;
	}

	/**
	 * @param selectorValue
	 */
	public void setSelector(MessageSelectorAST selectorValue)
	{
		this.selector = selectorValue;
	}

	public String asSourceCode()
	{
		return target.asSourceCode() + "." + selector.asSourceCode();
	}
}
