/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2007 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */

package Composestar.Core.CpsProgramRepository.Legacy;

import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterType;

/**
 * Legacy Custom Filter Type
 * @author Michiel Hendriks
 */
public class LegacyCustomFilterType extends FilterType
{
	public String name;
	
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
}
