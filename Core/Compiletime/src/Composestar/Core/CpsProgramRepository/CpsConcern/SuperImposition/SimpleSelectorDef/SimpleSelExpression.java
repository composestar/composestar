/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */
package Composestar.Core.CpsProgramRepository.CpsConcern.SuperImposition.SimpleSelectorDef;

import java.util.Vector;

import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.CpsProgramRepository.CpsConcern.References.ConcernReference;
import Composestar.Core.RepositoryImplementation.ContextRepositoryEntity;

/**
 * @modelguid {3DA7D7CD-878F-4446-8F14-10A874370B7B}
 * @todo 00:58:59| WARNING: Component SimpleSelExpression - 1 method(s) found in
 *       code which is(are) not in model - moved to end of file :SelExpression()
 */
public abstract class SimpleSelExpression extends ContextRepositoryEntity
{

	/**
	 * @modelguid {A9D91007-91C9-401F-9F72-ECBE8283FC46}
	 */
	private ConcernReference ref;

	/**
	 * @roseuid 404DDA7801B8
	 */
	public SimpleSelExpression()
	{
		super();
	}

	/**
	 * @return java.lang.String
	 * @modelguid {98F1A5EF-A4AE-4BFD-BF0D-BBB67BB112AC}
	 * @roseuid 401FAA6801EC
	 */
	public String getClassName()
	{
		return ref.getName();
	}

	/**
	 * @param classNameValue
	 * @modelguid {44219BCA-221A-4167-B84F-CCE99D8E1E9C}
	 * @roseuid 401FAA6801F5
	 */
	public void setClassName(String classNameValue)
	{
		ref.setName(classNameValue);
	}

	/**
	 * @return returns a list of ConcernReferences
	 * @roseuid 404FA9100138
	 */
	public abstract Vector interpret();

	/**
	 * @param className
	 * @param pack
	 * @return Composestar.Core.CpsProgramRepository.Concern
	 * @roseuid 405774680378
	 */
	public Concern resolveReference(String className, String pack)
	{
		return null;
	}

	/**
	 * @param classNValue
	 * @roseuid 40ADE8C1029C
	 */
	public void setClass(ConcernReference classNValue)
	{
		this.ref = classNValue;
	}

	public ConcernReference getRef()
	{
		return ref;
	}

	public void setRef(ConcernReference ref)
	{
		this.ref = ref;
	}

}
