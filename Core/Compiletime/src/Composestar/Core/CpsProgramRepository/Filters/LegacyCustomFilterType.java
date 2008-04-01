/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2007 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */

package Composestar.Core.CpsProgramRepository.Filters;

import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterType;

/**
 * Legacy Custom Filter Type
 * 
 * @author Michiel Hendriks
 */
public class LegacyCustomFilterType extends FilterType
{
	private static final long serialVersionUID = -3777972978684555L;

	public String name;

	public String className;

	public LegacyCustomFilterType()
	{
		super();
		setType(FilterTypeNames.CUSTOM);
	}

	public LegacyCustomFilterType(String inName)
	{
		this();
		name = inName;
	}

	public String getName()
	{
		return name;
	}

	public String getClassName()
	{
		if (className != null && className.length() > 0)
		{
			return className;
		}
		return name;
	}

	public void setClassName(String clsName)
	{
		className = clsName;
	}
}
