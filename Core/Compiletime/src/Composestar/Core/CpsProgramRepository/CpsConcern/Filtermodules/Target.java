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
import Composestar.Core.CpsProgramRepository.CpsConcern.References.*;

/**
 * should be either a declared (in the scope) object or a pseudo variable
 * (inner) or a wildcard; [if it was only a reference, it would be a
 * ConcernReference, perhaps it could inherit from ConcernReference??? ]
 */
public class Target extends ContextRepositoryEntity
{

	/**
	 * fixme: same information twice, name kept for compatibility, should only
	 * be ref
	 */
	public String name;

	public FilterModuleElementReference ref;

	/**
	 * identifier?
	 * 
	 * @roseuid 401FAA6902AB
	 */
	public Target()
	{
		super();
	}

	/**
	 * @return java.lang.String
	 * @roseuid 401FAA6902AC
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * @param targetValue
	 * @roseuid 401FAA6902B5
	 */
	public void setName(String targetValue)
	{
		this.name = targetValue;
	}

	/**
	 * @return Composestar.Core.CpsProgramRepository.CpsConcern.References.FilterModuleElem
	 *         entReference
	 * @roseuid 40ADDC9D0206
	 */
	public FilterModuleElementReference getRef()
	{
		return ref;
	}

	/**
	 * @param refValue
	 * @roseuid 40ADDCA10090
	 */
	public void setRef(FilterModuleElementReference refValue)
	{
		this.ref = refValue;
	}
}
