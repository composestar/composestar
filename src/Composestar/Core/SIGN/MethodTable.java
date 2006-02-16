package Composestar.Core.SIGN;

/**
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 * 
 * $Id: MethodTable.java,v 1.1 2006/02/13 11:16:57 pascal Exp $
 * 
**/

import java.util.*;

import Composestar.Core.CpsProgramRepository.*;

/**
 * Store symbols with a type to match in a hashmap.
 */
public class MethodTable
{
		/**
	 * Store all types in a hashmap. 
	 * Symbol name is the key and the type the value.
	 */
	private HashMap methodMap = null;

		/**
	 * Private constructor (singleton).
	 */
	public MethodTable()
	{
		methodMap = new HashMap();
	}

	/**
	 * Empty the symboltable.
	 */
	public void empty()
	{
		methodMap.clear();
	}
	
	public void add (Method m)
	{
		String key = m.getHashKey();
		if (!methodMap.containsKey(key)) 
		{ 
			methodMap.put(key, m);
		}
	}
	
	public Iterator getIterator()
	{
		return methodMap.values().iterator();
	}
	
	public Signature getSignature()
	{
		return null;
	}
}


