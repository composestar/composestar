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

import Composestar.Core.RepositoryImplementation.DeclaredRepositoryEntity;

/**
 * A concern is any implementation unit; this can be a Compose* Concern, but
 * also a class, a package, a primitive type, an assembly.
 */
public class Concern extends DeclaredRepositoryEntity
{
	public PlatformRepresentation platformRepr;

	public Signature signature;

	/**
	 * @modelguid {D3EFC8F1-94CE-4BD0-8994-9801CF5ED163}
	 * @roseuid 401FAA570165
	 */
	public Concern()
	{
		super();
	}

	/**
	 * @roseuid 40237FEC009F
	 */
	public PlatformRepresentation getPlatformRepresentation()
	{
		return platformRepr;
	}

	/**
	 * @roseuid 40237FFF0300
	 * @param pr
	 */
	public void setPlatformRepresentation(PlatformRepresentation pr)
	{
		platformRepr = pr;
	}

	/**
	 * @roseuid 404C49F80196
	 * @param sig
	 */
	public void setSignature(Signature sig)
	{
		signature = sig;
	}

	/**
	 * @roseuid 404C4A31012A
	 */
	public Signature getSignature()
	{
		return signature;
	}

	public Object clone() throws CloneNotSupportedException
	{
		Concern newObject;
		newObject = (Concern) super.clone();

		// At this point, the newObject shares all data with the object
		// running clone. If you want newObject to have its own
		// copy of data, you must clone this data yourself.

		newObject.signature = null;
		return newObject;
	}
}
