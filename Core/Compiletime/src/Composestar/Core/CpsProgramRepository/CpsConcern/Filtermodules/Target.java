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
	 * The name of the target, should be equal to ref.getName
	 */
	public String name;

	/**
	 * Reference to a filter module element. Can be null.
	 */
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
		if (ref != null)
		{
			return ref.getName();
		}
		return name;
	}

	/**
	 * @param targetValue
	 */
	public void setName(String targetValue)
	{
		// NOTE: not allowed when ref!=null
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
		name = refValue.getName();
	}

	public String asSourceCode()
	{
		if (ref != null)
		{
			return ref.getName();
		}
		return name;
	}

	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((ref == null) ? 0 : ref.hashCode());
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
		if (getName() == null)
		{
			if (other.getName() != null)
			{
				return false;
			}
		}
		else if (!getName().equals(other.getName()))
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
