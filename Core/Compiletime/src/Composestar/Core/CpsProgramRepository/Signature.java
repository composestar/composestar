/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */
package Composestar.Core.CpsProgramRepository;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import Composestar.Core.LAMA.MethodInfo;
import Composestar.Core.RepositoryImplementation.DataMap;
import Composestar.Core.RepositoryImplementation.SerializableRepositoryEntity;

/**
 * This class holds the methods forming the real signature of a concern. If a
 * concern has no such class its CompiledConcernReprs holds it's real signature.
 */
public class Signature implements SerializableRepositoryEntity
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1592569654092179522L;

	// Cannot use HashMap because these are not serialized.
	public DataMap methodByKey;

	public DataMap methodByName;

	/**
	 * @roseuid 404C4B67007B
	 */
	public Signature()
	{
		super();
		methodByKey = DataMap.newDataMapInstance();
		methodByName = DataMap.newDataMapInstance();
	}

	/**
	 * Adds a method wrapper to the signature.
	 * 
	 * @param wrapper
	 */
	public boolean addMethodWrapper(MethodWrapper wrapper)
	{
		String key = wrapper.getMethodInfo().getHashKey();

		if (!methodByKey.containsKey(key))
		{
			methodByKey.put(key, wrapper);

			String name = wrapper.getMethodInfo().getName();
			if (methodByName.containsKey(name))
			{
				Integer i = (Integer) methodByName.get(name);
				methodByName.put(name, new Integer(i.intValue() + 1));
			}
			else
			{
				Integer i = new Integer(1);
				methodByName.put(name, i);
			}

			return true;
		}

		return false;
	}

	/*
	 * public boolean methodExist (SignatureImplementation si) { return
	 * (implementations.containsKey(si)); }
	 */

	// TODO: Optimize: use LinkedHashSet
	public List getMethods(int type)
	{
		List typeOnlyList = new ArrayList();
		Iterator itr = methodByKey.values().iterator();
		while (itr.hasNext())
		{
			MethodWrapper mw = (MethodWrapper) itr.next();
			if ((mw.getRelationType() & type) != 0)
			{
				typeOnlyList.add(mw.getMethodInfo());
			}

		}
		return typeOnlyList;
	}

	public List getMethods()
	{
		List list = new LinkedList();

		Iterator itr = methodByKey.values().iterator();
		while (itr.hasNext())
		{
			MethodWrapper mw = (MethodWrapper) itr.next();
			list.add(mw.getMethodInfo());
		}

		return list;
	}

	public List getMethodWrappers(int type)
	{
		List typeOnlyList = new LinkedList();
		Iterator itr = methodByKey.values().iterator();
		while (itr.hasNext())
		{
			MethodWrapper mw = (MethodWrapper) itr.next();
			if (mw.getRelationType() == type)
			{
				typeOnlyList.add(mw);
			}

		}
		return typeOnlyList;
	}

	public Iterator getMethodWrapperIterator()
	{
		return methodByKey.values().iterator();
	}

	public MethodWrapper getMethodWrapper(MethodInfo methodInfo)
	{
		String key = methodInfo.getHashKey();
		return (MethodWrapper) methodByKey.get(key);
	}

	public void removeMethodWrapper(MethodWrapper mw)
	{
		MethodInfo minfo = mw.getMethodInfo();
		String key = minfo.getHashKey();
		if (methodByKey.containsKey(key))
		{
			methodByKey.remove(key);

			String name = mw.getMethodInfo().getName();
			Integer count = (Integer) methodByName.get(name);
			if (count.intValue() <= 1)
			{
				methodByName.remove(name);
			}
			else
			{
				methodByName.put(name, new Integer(count.intValue() - 1));
			}
		}
	}

	public boolean hasMethod(MethodInfo dnmi)
	{
		String key = dnmi.getHashKey();
		return (methodByKey.containsKey(key));
	}

	public boolean hasMethod(String methodName)
	{
		return methodByName.containsKey(methodName);
	}
}
