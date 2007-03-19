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

/**
 * used as the rightHandOperator of the last FilterElement in a filter.
 */
public class VoidFilterElementCompOper extends FilterElementCompOper
{
	private static final long serialVersionUID = -7341779162828357908L;

	public VoidFilterElementCompOper()
	{
		super();
	}

	/**
	 * raise exception (should not be invoked on this element)
	 * 
	 * @return Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterElement
	 */
	public FilterElementAST getRightArgument()
	{
		return null;
	}

	/**
	 * raise exception (should not be invoked on this element)
	 * 
	 * @param element
	 */
	public void setRightArgument(FilterElementAST element)
	{}

	public String asSourceCode()
	{
		return "";
	}
}
