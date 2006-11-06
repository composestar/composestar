/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */
package Composestar.Core.CpsProgramRepository.CpsConcern.SuperImposition;

import Composestar.Core.CpsProgramRepository.CpsConcern.References.*;
import Composestar.Utils.*;

import java.util.*;

/**
 * @modelguid {96208145-32E1-4DF1-9960-F87525EAD5D1}
 */
public class FilterModuleBinding extends Binding
{
	public Vector filterModuleSet;

	/**
	 * @modelguid {887B9895-CA97-4FDA-82D2-17A606C9BB2F}
	 * @roseuid 401FAA64024B
	 */
	public FilterModuleBinding()
	{
		super();
		filterModuleSet = new Vector();
	}

	/**
	 * filtermoduleset
	 * 
	 * @param f
	 * @return boolean
	 * @modelguid {6891E07E-5266-4990-947A-45E4D436327A}
	 * @roseuid 401FAA640254
	 */
	public boolean addFilterModule(FilterModuleReference f)
	{
		filterModuleSet.addElement(f);
		return true;
	}

	/**
	 * @param index
	 * @return Composestar.Core.CpsProgramRepository.CpsConcern.References.FilterModuleRefe
	 *         rence
	 * @modelguid {2B8715FA-678D-4387-A5EE-E3936248B5AE}
	 * @roseuid 401FAA640272
	 */
	public FilterModuleReference removeFilterModule(int index)
	{
		Object o = filterModuleSet.elementAt(index);
		filterModuleSet.removeElementAt(index);
		return (FilterModuleReference) o;
	}

	/**
	 * @param index
	 * @return Composestar.Core.CpsProgramRepository.CpsConcern.References.FilterModuleRefe
	 *         rence
	 * @modelguid {497F3A9B-2F06-4727-81E8-490369486EDC}
	 * @roseuid 401FAA64027D
	 */
	public FilterModuleReference getFilterModule(int index)
	{
		return (FilterModuleReference) filterModuleSet.elementAt(index);
	}

	/**
	 * @return java.util.Iterator
	 * @modelguid {170E849B-7C6D-4E83-8A0F-7DED4B70B10F}
	 * @roseuid 401FAA640291
	 */
	public Iterator getFilterModuleIterator()
	{
		return new CPSIterator(filterModuleSet);
	}
}
