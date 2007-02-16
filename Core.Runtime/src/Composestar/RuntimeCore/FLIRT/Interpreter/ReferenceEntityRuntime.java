package Composestar.RuntimeCore.FLIRT.Interpreter;

import Composestar.Core.RepositoryImplementation.RepositoryEntity;

/**
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente. Licensed under LGPL v2.1 or (at your
 * option) any later version. [http://www.fsf.org/copyleft/lgpl.html] $Id:
 * ReferenceEntityRuntime.java 2072 2006-10-15 12:44:56Z elmuerte $
 */
public class ReferenceEntityRuntime
{
	public RepositoryEntity reference;

	/**
	 * @roseuid 40DD8C3C02A2
	 */
	public ReferenceEntityRuntime()
	{

	}

	/**
	 * @param reference
	 * @roseuid 40DD8C010239
	 */
	public ReferenceEntityRuntime(RepositoryEntity reference)
	{
		this.reference = reference;
	}

	/**
	 * @param reference
	 * @roseuid 40DD8BB90086
	 */
	public void setReference(RepositoryEntity reference)
	{
		this.reference = reference;
	}

	/**
	 * @return Composestar.CTCommon.RepositoryImplementation.RepositoryEntity
	 * @roseuid 40DD8BDA0066
	 */
	public RepositoryEntity getReference()
	{
		return this.reference;
	}
}
