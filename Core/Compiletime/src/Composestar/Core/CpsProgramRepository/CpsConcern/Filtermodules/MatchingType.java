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

import Composestar.Core.RepositoryImplementation.*;

/**
 * This is the common supertype for the (currently two) different types of
 * matching the reason for having the subclasses is that we may want to attach
 * different behavior/information to the different kinds of matching. the
 * clients can determine the type by asking for matchType.class().name()
 */
public abstract class MatchingType extends ContextRepositoryEntity
{

	/**
	 * @roseuid 404C4B69031D
	 */
	public MatchingType()
	{
		super();
	}
}
