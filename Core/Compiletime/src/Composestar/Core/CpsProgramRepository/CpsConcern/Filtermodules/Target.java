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

import Composestar.Core.CpsProgramRepository.CpsConcern.References.FilterModuleElementReference;
import Composestar.Core.RepositoryImplementation.ContextRepositoryEntity;

/**
 * should be either a declared (in the scope) object or a pseudo variable
 * (inner) or a wildcard; [if it was only a reference, it would be a
 * ConcernReference, perhaps it could inherit from ConcernReference??? ]
 */
public class Target extends ContextRepositoryEntity
{
	private static final long serialVersionUID = 4238743031003419298L;

	public static final String INNER = "inner";
	
	public static final String SELF = "self";

	/**
	 * fixme: same information twice, name kept for compatibility, should only
	 * be ref
	 */
	public String name;

	public FilterModuleElementReference ref;

	/**
	 * identifier?
	 */
	public Target()
	{
		super();
	}

	/**
	 * @return java.lang.String
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * @param targetValue
	 */
	public void setName(String targetValue)
	{
		this.name = targetValue;
	}

	/**
	 * @return Composestar.Core.CpsProgramRepository.CpsConcern.References.FilterModuleElem
	 *         entReference
	 */
	public FilterModuleElementReference getRef()
	{
		return ref;
	}

	/**
	 * @param refValue
	 */
	public void setRef(FilterModuleElementReference refValue)
	{
		this.ref = refValue;
	}

	public String asSourceCode()
	{
		return ref.getName();
	}

	public int hashCode()
	{
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((name == null) ? 0 : name.hashCode());
		result = PRIME * result + ((ref == null) ? 0 : ref.hashCode());
		return result;
	}

	public boolean equals(Object obj)
	{
		if ((this == obj) || (obj == null))
		{
			return true;
		}
		if (getClass() != obj.getClass())
		{
			return false;
		}
		final Target other = (Target) obj;
		if (name == null)
		{
			if (other.name != null)
			{
				return false;
			}
		}
		else if (!name.equals(other.name))
		{
			return false;
		}
		// if (ref == null)
		// {
		// if (other.ref != null)
		// {
		// return false;
		// }
		// }
		// else if (!ref.equals(other.ref))
		// {
		// return false;
		// }
		return true;
	}
}
