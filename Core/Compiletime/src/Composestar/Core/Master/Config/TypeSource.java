/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2006 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */
package Composestar.Core.Master.Config;

import java.io.Serializable;

@Deprecated
public class TypeSource implements Serializable
{
	private static final long serialVersionUID = 3478303188238617896L;

	private String name;

	private String fileName;

	public TypeSource()
	{}

	public String getName()
	{
		return name;
	}

	public void setName(String inName)
	{
		name = inName;
	}

	public String getFileName()
	{
		return fileName;
	}

	public void setFileName(String inFilename)
	{
		fileName = inFilename;
	}
}
