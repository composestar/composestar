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
	private static final long serialVersionUID = 3283145732364930465L;

	// relation type constants
	public static final int NORMAL = 1;

	public static final int ADDED = 2;

	public static final int REMOVED = 4;

	// status constants
	public static final int UNKNOWN = 8;

	public static final int EXISTING = 16;

	public static final int NOT_EXISTING = 32;

	private int relationType;

	private int status;

	public MethodInfo methodInfo;

	/**
	 * @param inRelationType
	 * @param inMethodInfo
	 */
	public MethodWrapper(MethodInfo inMethodInfo, int inStatus)
	{
		relationType = NORMAL;
		methodInfo = inMethodInfo;
		status = inStatus;
	}

	public MethodWrapper()
	{}

	public MethodInfo getMethodInfo()
	{
		return methodInfo;
	}

	/**
	 * @return int
	 */
	public int getRelationType()
	{
		return relationType;
	}

	public void setRelationType(int type)
	{
		relationType = type;
	}

	/**
	 * @return the status
	 */
	public int getStatus()
	{
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(int inStatus)
	{
		status = inStatus;
	}

	public String toString()
	{
		String rel = "?" + relationType;
		if (relationType == NORMAL)
		{
			rel = "o";
		}
		else if (relationType == ADDED)
		{
			rel = "+";
		}
		else if (relationType == REMOVED)
		{
			rel = "-";
		}

		return "[" + rel + "]" + methodInfo.toString();
	}
}
