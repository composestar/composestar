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

/**
 * alleen klasse
 * 
 * @modelguid {0394CAFD-15F1-4FD3-BD29-403B714ED162}
 */
public class SelClass extends SimpleSelExpression
{

	/**
	 * @modelguid {5ACC13A5-BB8B-4943-B5A2-DF4CB06E99FC}
	 * @roseuid 401FAA670303
	 */
	public SelClass()
	{
		super();
	}

	/**
	 * this list contains the concern designated by the classname (=concernname)
	 * (only)
	 * 
	 * @return a list of ConcernReferences (with only one entry)
	 * @roseuid 404FA993024F
	 */
	public Vector interpret()
	{
		Vector v = new Vector();
		v.add(this.getRef());
		return v;

	}
}
