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
import java.util.ArrayList;
import java.util.List;

@Deprecated
public class Platform implements Serializable
{
	private static final long serialVersionUID = 6326989729059538537L;

	private List<String> requiredFiles;

	public Platform()
	{
		requiredFiles = new ArrayList<String>();
	}

	public void addRequiredFile(String file)
	{
		requiredFiles.add(file);
	}

	public List<String> getRequiredFiles()
	{
		return requiredFiles;
	}
}
