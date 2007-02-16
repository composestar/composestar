/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */
package Composestar.Core.CpsProgramRepository.CpsConcern.SuperImposition;

import Composestar.Core.CpsProgramRepository.CpsConcern.References.SelectorReference;
import Composestar.Core.RepositoryImplementation.ContextRepositoryEntity;

public class Binding extends ContextRepositoryEntity
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6223274022819701514L;

	public SelectorReference selector;

	/**
	 * @roseuid 401FAA5602B8
	 */
	public Binding()
	{
		super();
	}

	/**
	 * @return Composestar.Core.CpsProgramRepository.CpsConcern.References.SelectorReferenc
	 *         e
	 * @roseuid 401FAA5602B9
	 */
	public SelectorReference getSelector()
	{
		return selector;
	}

	/**
	 * @param selectorValue
	 * @roseuid 401FAA5602C2
	 */
	public void setSelector(SelectorReference selectorValue)
	{
		this.selector = selectorValue;
	}
}