/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2006 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */
package Composestar.Core.LAMA;

import java.util.Set;

/**
 * @author havingaw
 */
public class UnitResult
{
	private Set<? extends ProgramElement> multiRes;

	private ProgramElement singleRes;

	public UnitResult()
	{ // Has to exist for .NET serialization
	}

	public UnitResult(ProgramElement single)
	{
		singleRes = single;
		multiRes = null;
	}

	public UnitResult(Set<? extends ProgramElement> multi)
	{
		multiRes = multi;
		singleRes = null;
	}

	/*
	 * @return a single program element, or null if the relation is not unique
	 */

	public ProgramElement singleValue()
	{
		return singleRes;
	}

	/*
	 * @return a hashset containing program elements, or null if the relation is
	 * unique
	 */
	public Set<? extends ProgramElement> multiValue()
	{
		return multiRes;
	}

	public boolean isSingleValue()
	{
		return singleRes != null;
	}

	public boolean isMultiValue()
	{
		return multiRes != null;
	}
}
