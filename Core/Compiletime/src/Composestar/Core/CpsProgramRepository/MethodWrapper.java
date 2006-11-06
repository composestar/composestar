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

import Composestar.Core.RepositoryImplementation.SerializableRepositoryEntity;
import Composestar.Core.LAMA.*;

/**
 * It used to Hold the relationship between a method and its implementation. It
 * is now more or less a wrapper for the 'real' Method information.
 */
public class MethodWrapper implements SerializableRepositoryEntity
{
	public int RelationType;

	public static final int NORMAL = 1;

	public static final int ADDED = 2;

	public static final int REMOVED = 4;

	public static final int UNKNOWN = 8;

	public static final int ALL = 255;

	public MethodInfo theMethodInfo;

	/**
	 * @roseuid 404C4B670107
	 * @param methodInfo
	 * @param relationType
	 */
	public MethodWrapper(int relationType, MethodInfo methodInfo)
	{
		RelationType = relationType;
		theMethodInfo = methodInfo;
	}

	public MethodWrapper()
	{

	}

	public MethodInfo getMethodInfo()
	{
		return theMethodInfo;
	}

	/**
	 * @return int
	 * @roseuid 4050504F0289
	 */
	public int getRelationType()
	{
		return RelationType;
	}

	public void setRelationType(int type)
	{
		RelationType = type;
	}

}
