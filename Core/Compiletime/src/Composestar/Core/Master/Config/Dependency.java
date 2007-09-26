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
public class Dependency implements Serializable
{
	private static final long serialVersionUID = -710749427319025140L;

	protected String fileName;

	public Dependency()
	{}

	public String getFileName()
	{
		return fileName;
	}

	public void setFileName(String file)
	{
		fileName = file;
	}

	public boolean equals(Object o)
	{
		if (!(o instanceof Dependency))
		{
			return false;
		}

		Dependency other = (Dependency) o;
		return fileName.equals(other.fileName);
	}

	public int hashCode()
	{
		return fileName.hashCode();
	}
}
