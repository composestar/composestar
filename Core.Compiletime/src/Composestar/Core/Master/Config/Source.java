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

public class Source implements Serializable
{
	private static final long serialVersionUID = -5054531327936357776L;

	private boolean isEmbedded;

	private boolean isExecutable;

	private String filename;

	private String dummy;

	// private String compiledSource;
	private String target;

	private Project project;

	public Source()
	{}

	public String getFileName()
	{
		return filename;
	}

	public void setFileName(String inFilename)
	{
		filename = inFilename;
	}

	public boolean isEmbedded()
	{
		return isEmbedded;
	}

	public void setIsEmbedded(boolean isemb)
	{
		isEmbedded = isemb;
	}

	public boolean isExecutable()
	{
		return isExecutable;
	}

	public void setIsExecutable(boolean isexe)
	{
		isExecutable = isexe;
	}

	public void setDummy(String filename)
	{
		dummy = filename;
	}

	public String getDummy()
	{
		return dummy;
	}

	public String getTarget()
	{
		return target;
	}

	public void setTarget(String inTarget)
	{
		target = inTarget;
	}

	public void setProject(Project p)
	{
		project = p;
	}

	public Project getProject()
	{
		return project;
	}
}
