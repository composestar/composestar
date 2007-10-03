/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente. Licensed under LGPL v2.1 or (at your
 * option) any later version. [http://www.fsf.org/copyleft/lgpl.html] $Id:
 * DotNETModule.java 1778 2006-10-01 13:57:50Z mivano $
 */

package Composestar.DotNET2.LAMA;

import Composestar.Core.RepositoryImplementation.SerializableRepositoryEntity;

/**
 * Corresponds to the Module class in the .NET framework. For more information
 * on the methods and their meaning please refer to the microsoft documentation:
 * http://msdn.microsoft.com/library/default.asp?url=/library/en-us/cpref/html/frlr
 * fsystemreflectionmoduleclasstopic.asp
 */
public class DotNETModule implements SerializableRepositoryEntity
{
	private static final long serialVersionUID = 9147930667863841248L;

	private String name;
	private String fullyQualifiedName;

	public DotNETModule()
	{
	}

	public String name()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String fullyQualifiedName()
	{
		return fullyQualifiedName;
	}

	public void setFullyQualifiedName(String name)
	{
		// TODO: Rename
		this.fullyQualifiedName = name;
	}
}
