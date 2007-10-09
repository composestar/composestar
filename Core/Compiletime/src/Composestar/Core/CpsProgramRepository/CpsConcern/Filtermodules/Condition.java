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
import Composestar.Core.CpsProgramRepository.CpsConcern.References.Reference;
import Composestar.Core.RepositoryImplementation.DeclaredRepositoryEntity;

/**
 * 
 */
public class Condition extends DeclaredRepositoryEntity
{
	private static final long serialVersionUID = 4642502845696125975L;

	// public String name; // declared in parent class
	public BooleanOclExpression ocl;

	public Reference shortref;

	public FilterModuleElementReference longref;

	public Condition()
	{
		super();
	}

	public String getName()
	{
		return name;
	}

	public void setName(String inname)
	{
		name = inname;
	}

	/**
	 * @return Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.BooleanOclExp
	 *         ression
	 * @deprecated what is this?
	 */
	public BooleanOclExpression getOCL()
	{
		return ocl;
	}

	/**
	 * @param oCL
	 * @deprecated what is this?
	 */
	public void setOCL(BooleanOclExpression inocl)
	{
		ocl = inocl;
	}

	public Reference getShortref()
	{
		return shortref;
	}

	public void setShortref(Reference inshortref)
	{
		shortref = inshortref;
	}

	public void setLongref(FilterModuleElementReference inlongref)
	{
		longref = inlongref;
	}

	public FilterModuleElementReference getLongref()
	{
		return longref;
	}

	public String asSourceCode()
	{
		StringBuffer sb = new StringBuffer();
		sb.append(getName());
		sb.append(": ");
		if (shortref != null)
		{
			sb.append(shortref.getName());
		}
		else if (longref != null)
		{
			sb.append("#LONGREF#"); // TODO:
		}
		sb.append(".");
		sb.append(getDynObject("selector"));
		sb.append("()");
		return sb.toString();
	}
}
