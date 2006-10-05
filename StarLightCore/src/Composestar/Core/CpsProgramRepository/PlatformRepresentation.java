/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */
package Composestar.Core.CpsProgramRepository;

import Composestar.Core.RepositoryImplementation.SerializableRepositoryEntity;

/**
 * Abstract interface representing the platform dependent implementation of a
 * concern.
 */
public abstract class PlatformRepresentation  implements SerializableRepositoryEntity
{
	private Concern parent = null;

	public void setParentConcern(Concern parent)
	{
		this.parent = parent;
	}
	
	public Concern getParentConcern()
	{
		return this.parent;
	}
}
