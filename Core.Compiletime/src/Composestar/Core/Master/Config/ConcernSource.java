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

public class ConcernSource implements Serializable
{
	private static final long serialVersionUID = 1046241361643868704L;

	protected String fileName;

	public ConcernSource()
	{}

	public String getFileName()
	{
		return fileName;
	}

	public void setFileName(String file)
	{
		fileName = file;
	}
}
