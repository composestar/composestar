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
public class CustomFilter implements Serializable
{
	private static final long serialVersionUID = 5750706940042791837L;

	private String filter = "";

	private String library = "";

	public CustomFilter()
	{}

	public String getFilter()
	{
		return filter;
	}

	public void setFilter(String inFilter)
	{
		filter = inFilter;
	}

	public String getLibrary()
	{
		return library;
	}

	public void setLibrary(String inLibrary)
	{
		library = inLibrary;
	}
}
