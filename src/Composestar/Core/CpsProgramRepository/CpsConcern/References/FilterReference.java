/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */
package Composestar.Core.CpsProgramRepository.CpsConcern.References;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Filter;

/**
 * reference to a in- or outputfilter
 */
public class FilterReference extends FilterModuleElementReference
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1395841459798706305L;

	private Filter ref;

	/**
	 * @roseuid 401FAA65018D
	 */
	public FilterReference()
	{
		super();
	}

	/**
	 * @return Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Filter
	 * @roseuid 401FAA65018E
	 */
	public Filter getRef()
	{
		return ref;
	}

	/**
	 * @param refValue
	 * @roseuid 40503C8203D3
	 */
	public void setRef(Filter refValue)
	{
		this.ref = refValue;
	}

	/**
	 * Custom deserialization of this object
	 * 
	 * @param in
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
	{
	// nothing yet
	}

	/**
	 * Custom serialization of this object
	 * 
	 * @param out
	 */
	private void writeObject(ObjectOutputStream out) throws IOException
	{
	// nothing yet
	}
}
