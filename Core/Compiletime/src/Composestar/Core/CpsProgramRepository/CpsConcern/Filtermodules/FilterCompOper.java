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

/**
 * the composition operator between two filters, currently this is always the
 * ';' (or the VoidFilterElementOperator for the last filter element)
 */
public abstract class FilterCompOper extends ContextRepositoryEntity
{
	public FilterAST rightArgument;

	public FilterCompOper()
	{
		super();
	}

	/**
	 * @return Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Filter
	 */
	public FilterAST getRightArgument()
	{
		return rightArgument;
	}

	/**
	 * @param rightArgumentValue
	 */
	public void setRightArgument(FilterAST rightArgumentValue)
	{
		this.rightArgument = rightArgumentValue;
	}
}
