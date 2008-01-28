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

//
// !! Compose* Runtime Warning !!
//
// This class is referenced in the Compose* Runtime for .NET 1.1
// Do not use Java features added after Java 2.0
//

/**
 * @author havingaw
 */
public class UnitResult
{
	private Set multiRes; // Used by INCRE

	private ProgramElement singleRes; // Used by INCRE

	public UnitResult()
	{ // Has to exist for .NET serialization
	}

	public UnitResult(ProgramElement single)
	{
		singleRes = single;
		multiRes = null;
	}

	public UnitResult(Set multi)
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
	public Set multiValue()
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
