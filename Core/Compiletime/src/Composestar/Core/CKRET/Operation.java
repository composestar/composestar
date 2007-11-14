/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2004-2006 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */
package Composestar.Core.CKRET;

import java.io.Serializable;

/**
 * 
 */
public class Operation implements Serializable
{
	private static final long serialVersionUID = 1402987412844825501L;

	private String name;

	private String resource;

	public Operation(String inname, String inresource)
	{
		name = inname;
		resource = inresource;
	}

	public String getResource()
	{
		return resource;
	}

	public String getName()
	{
		return name;
	}

	public boolean isFork()
	{
		return getName().equals("fork") && getResource().equals("message");
	}

	public boolean isProceed()
	{
		return getName().equals("proceed") && getResource().equals("message");
	}

	public boolean isReturn()
	{
		return getName().equals("return") && getResource().equals("message");
	}
}
