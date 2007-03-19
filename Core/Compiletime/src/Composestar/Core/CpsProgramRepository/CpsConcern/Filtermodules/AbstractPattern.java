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

public abstract class AbstractPattern extends ContextRepositoryEntity
{
	public Target target;

	public MessageSelector selector;

	public AbstractPatternAST apAST;

	/**
	 * @deprecated
	 */
	public AbstractPattern()
	{
		super();
	}

	public AbstractPattern(AbstractPatternAST anapAST)
	{
		super();
		apAST = anapAST;
		descriptionFileName = apAST.getDescriptionFileName();
		descriptionLineNumber = apAST.getDescriptionLineNumber();
		selector = new MessageSelector(anapAST.getSelector());
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
		target = targetValue;
	}

	/**
	 * @return Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.MessageSelect
	 *         or
	 */
	public MessageSelector getSelector()
	{
		return selector;
	}

	/**
	 * @param selectorValue
	 */
	public void setSelector(MessageSelector selectorValue)
	{
		this.selector = selectorValue;
	}

	public String asSourceCode()
	{
		return apAST.asSourceCode();
	}
}
