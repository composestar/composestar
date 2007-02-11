/*
 * UnitRelation.java - Created on 22-okt-2004 by havingaw
 */

package Composestar.Core.LAMA;

import java.util.Set;

/**
 * @author havingaw TODO: Insert description of class here
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
		this.singleRes = single;
		this.multiRes = null;
	}

	public UnitResult(Set multi)
	{
		this.multiRes = multi;
		this.singleRes = null;
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
