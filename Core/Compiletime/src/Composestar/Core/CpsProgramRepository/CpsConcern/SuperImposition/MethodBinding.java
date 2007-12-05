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

import Composestar.Core.CpsProgramRepository.CpsConcern.References.MethodReference;
import Composestar.Utils.CPSIterator;

/**
 * @deprecated
 */
public class MethodBinding extends Binding
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3261110884394154857L;

	public Vector methodSet;

	/**
	 * @modelguid {6A3A82DF-C97D-47E7-B900-EBEA600C3907}
	 * @roseuid 401FAA6601E8
	 */
	public MethodBinding()
	{
		super();
		methodSet = new Vector();
	}

	/**
	 * methodNameSet
	 * 
	 * @param m
	 * @return boolean
	 * @modelguid {A25E805B-DBF3-484E-8E97-FA91101EFDF2}
	 * @roseuid 401FAA6601E9
	 */
	public boolean addMethod(MethodReference m)
	{
		methodSet.addElement(m);
		return true;
	}

	/**
	 * @param m
	 * @return boolean
	 * @roseuid 401FAA660207
	 */
	public boolean addMethod(StarMethod m)
	{
		methodSet.addElement(m);
		return true;
	}

	/**
	 * @param index
	 * @return java.lang.Object
	 * @modelguid {25C924D5-9E62-4F27-8729-5580DD9FB5A4}
	 * @roseuid 401FAA66022F
	 */
	public Object removeMethod(int index)
	{ // fixme: could be star or single object
		Object o = methodSet.elementAt(index);
		methodSet.removeElementAt(index);
		return o;
	}

	/**
	 * @param index
	 * @return java.lang.Object
	 * @modelguid {B45EF46F-5144-40FB-B7AD-6EBD0E0714A2}
	 * @roseuid 401FAA660231
	 */
	public Object getMethod(int index)
	{ // fixme: could be star or single object
		return methodSet.elementAt(index);
	}

	/**
	 * @return java.util.Iterator
	 * @modelguid {3A6A5880-EFF6-4F5F-9850-1A5EA54EEC75}
	 * @roseuid 401FAA66023A
	 */
	public Iterator getMethodIterator()
	{
		return new CPSIterator(methodSet);
		// Vector tmpVector = new Vector();
		// tmpVector.add(methodSet);
		// tmpVector.add(starMethodSet);
		// return (tmpVector.iterator());
	}
}
