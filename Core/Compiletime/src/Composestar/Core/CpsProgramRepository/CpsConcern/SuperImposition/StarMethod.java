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

import java.util.Iterator;
import java.util.Vector;

import Composestar.Core.CpsProgramRepository.CpsConcern.References.FilterModuleElementReference;
import Composestar.Core.CpsProgramRepository.CpsConcern.References.MethodReference;
import Composestar.Utils.CPSIterator;

/**
 * @deprecated
 */
public class StarMethod extends FilterModuleElementReference
{
	private static final long serialVersionUID = -8845115159529931618L;

	/**
	 * contains a collection of methodreferences
	 */
	public Vector methodSet;

	/**
	 * contains a collection of methodreferences
	 * 
	 * @roseuid 401FAA690018
	 */
	public StarMethod()
	{
		super();
		methodSet = new Vector();
	}

	/**
	 * methodNameSet
	 * 
	 * @param m
	 * @return boolean
	 * @roseuid 401FAA690020
	 */
	public boolean addMethod(MethodReference m)
	{
		methodSet.addElement(m);
		return true;
	}

	/**
	 * @param index
	 * @return Composestar.Core.CpsProgramRepository.CpsConcern.References.MethodReference
	 * @roseuid 401FAA69002B
	 */
	public MethodReference removeMethod(int index)
	{
		Object o = methodSet.elementAt(index);
		methodSet.removeElementAt(index);
		return (MethodReference) o;
	}

	/**
	 * @param index
	 * @return Composestar.Core.CpsProgramRepository.CpsConcern.References.MethodReference
	 * @roseuid 401FAA69003F
	 */
	public MethodReference getMethod(int index)
	{
		return (MethodReference) methodSet.elementAt(index);
	}

	/**
	 * @return java.util.Iterator
	 * @roseuid 401FAA690049
	 */
	public Iterator getMethodIterator()
	{
		return new CPSIterator(methodSet);
	}
}
