/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id: AbstractPatternAST.java 26 2006-02-16 23:03:47Z pascal_durr $
 */
package Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules;

import Composestar.Core.RepositoryImplementation.*;

public abstract class AbstractPatternAST extends ContextRepositoryEntity
{
	public Target target;

	public MessageSelectorAST selector;

	/**
	 * @roseuid 404DDA71039C
	 */
	public AbstractPatternAST()
	{
		super();
	}

	/**
	 * @return Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Target
	 * @roseuid 401FAA670136
	 */
	public Target getTarget()
	{
		return target;
	}

	/**
	 * @param targetValue
	 * @roseuid 401FAA670140
	 */
	public void setTarget(Target targetValue)
	{
		this.target = targetValue;
	}

	/**
	 * @return Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.MessageSelect
	 *         or
	 * @roseuid 401FAA670154
	 */
	public MessageSelectorAST getSelector()
	{
		return selector;
	}

	/**
	 * @param selectorValue
	 * @roseuid 401FAA67015E
	 */
	public void setSelector(MessageSelectorAST selectorValue)
	{
		this.selector = selectorValue;
	}
}
