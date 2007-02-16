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

/**
 * all 'native' implementations (including .NET objects and primitive data
 * types) should subclass and/or be wrapped by me. we can't reason about the
 * implementation here.
 */
public class PrimitiveConcern extends Concern
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7730136747082190101L;

	/**
	 * @roseuid 404C4B660391
	 */
	public PrimitiveConcern()
	{
		super();
	}

	public Object clone() throws CloneNotSupportedException
	{
		PrimitiveConcern newObject = (PrimitiveConcern) super.clone();

		// At this point, the newObject shares all data with the object
		// running clone. If you want newObject to have its own
		// copy of data, you must clone this data yourself.

		return newObject;
	}

	public String toString()
	{
		return "PrimitiveConcern[" + platformRepr + "]";
	}
}