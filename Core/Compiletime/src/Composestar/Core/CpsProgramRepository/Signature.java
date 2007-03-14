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
import Composestar.Core.LAMA.ParameterInfo;
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

	public static final int RECALCMAXSIG = -1;

	public static final int UNKNOWN = 0;

	public static final int UNSOLVED = 1;

	public static final int SOLVING = 2;

	public static final int SOLVED = 3;

	protected int status = Signature.UNKNOWN;

	/**
	 * @roseuid 404C4B67007B
	 */
	public Signature()
	{
		super();
		empty();
	}

	/**
	 * Empty the symboltable.
	 */
	public void empty()
	{
		methodByKey = DataMap.newDataMapInstance();
		methodByName = DataMap.newDataMapInstance();
	}

	/**
	 * @param method
	 * @roseuid 404C458900F3
	 * @param type
	 * @param methodInfo
	 */
	public boolean add(MethodInfo methodInfo, int type)
	{
		MethodWrapper mw = new MethodWrapper(type, methodInfo);
		String key = getHashKey(methodInfo);

		if (!methodByKey.containsKey(key))
		{
			methodByKey.put(key, mw);
			methodByName.put(methodInfo.getName(), mw);
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

	/** @deprecated: use iterator instead */
	public List getMethodWrappers()
	{
		List typeOnlyList = new LinkedList();
		Iterator itr = methodByKey.values().iterator();
		while (itr.hasNext())
		{
			MethodWrapper mw = (MethodWrapper) itr.next();
			typeOnlyList.add(mw);
		}
		return typeOnlyList;
	}

	public MethodWrapper getMethodWrapper(MethodInfo methodInfo)
	{
		String key = getHashKey(methodInfo);
		return (MethodWrapper) methodByKey.get(key);
	}

	public void removeMethodWrapper(MethodWrapper mw)
	{
		MethodInfo minfo = mw.getMethodInfo();
		String key = getHashKey(minfo);
		if (methodByKey.containsKey(key))
		{
			methodByKey.remove(key);
		}
		if (methodByName.containsKey(minfo.getName()))
		{
			methodByName.remove(minfo.getName());
		}
	}

	public boolean hasMethod(MethodInfo dnmi)
	{
		String key = getHashKey(dnmi);
		return (methodByKey.containsKey(key));
	}

	public boolean hasMethod(String methodName)
	{
		return (methodByName.containsKey(methodName));
	}

	public int getMethodStatus(String name)
	{
		MethodWrapper mw = (MethodWrapper) methodByName.get(name);
		if (mw == null)
		{
			return MethodWrapper.REMOVED;
		}

		return mw.getRelationType();
	}

	public MethodInfo getNamedMethod()
	{
		return null;
	}

	// FIXME: move to MethodInfo
	public String getHashKey(MethodInfo methodInfo)
	{
		StringBuffer sb = new StringBuffer();

		sb.append(methodInfo.getName()).append('%');
		sb.append(methodInfo.getReturnTypeString()).append('%');

		List pars = methodInfo.getParameters();
		for (int i = 0; i < pars.size(); i++)
		{
			ParameterInfo pi = (ParameterInfo) pars.get(i);
			sb.append(pi.parameterTypeString).append('%');
		}

		return sb.toString();
	}

	public void setStatus(int _status)
	{
		status = _status;
	}

	public int getStatus()
	{
		return status;
	}
}
