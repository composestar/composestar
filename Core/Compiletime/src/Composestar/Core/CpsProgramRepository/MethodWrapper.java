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

import Composestar.Core.LAMA.MethodInfo;
import Composestar.Core.RepositoryImplementation.SerializableRepositoryEntity;

/**
 * It used to Hold the relationship between a method and its implementation. It
 * is now more or less a wrapper for the 'real' Method information.
 */
public class MethodWrapper implements SerializableRepositoryEntity
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3283145732364930465L;

	public int relationType;

	public static final int NORMAL = 1;

	public static final int ADDED = 2;

	public static final int REMOVED = 4;

	public static final int UNKNOWN = 8;

	public static final int ALL = 255;

	public MethodInfo methodInfo;

	/**
	 * @param inRelationType
	 * @param inMethodInfo
	 */
	public MethodWrapper(int inRelationType, MethodInfo inMethodInfo)
	{
		relationType = inRelationType;
		methodInfo = inMethodInfo;
	}

	public MethodWrapper()
	{

	}

	public MethodInfo getMethodInfo()
	{
		return methodInfo;
	}

	/**
	 * @return int
	 * @roseuid 4050504F0289
	 */
	public int getRelationType()
	{
		return relationType;
	}

	public void setRelationType(int type)
	{
		relationType = type;
	}

}
