/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */
package Composestar.Core.CpsProgramRepository.CpsConcern.References;

import Composestar.Core.CpsProgramRepository.Concern;

/**
 * reference to a concern (nb a concern is any implementation unit; this can be
 * a Compose* Concern, but also a class, a package, a primitive type, an
 * assembly)
 */
public class ConcernReference extends Reference
{
	private static final long serialVersionUID = -4669018731045759962L;

	private Concern ref;

	/**
	 * or just concern?
	 * 
	 * @roseuid 401FAA57025F
	 */
	public ConcernReference()
	{
		super();
	}

	/**
	 * @return Composestar.Core.CpsProgramRepository.Concern
	 * @roseuid 404F197A0101
	 */
	public Concern getRef()
	{
		return ref;
	}

	/**
	 * @param refValue
	 * @roseuid 40503C1802C8
	 */
	public void setRef(Concern refValue)
	{
		this.ref = refValue;
		// refValue.addDynObject("REFERENCED",new Object());
		refValue.addDynObject("REFERENCED", "");
	}
}
