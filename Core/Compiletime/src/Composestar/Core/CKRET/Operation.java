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
 * @author Administrator To change the template for this generated type comment
 *         go to Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and
 *         Comments
 */
public class Operation implements Serializable
{

	/**
	 * 
	 */
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
		return this.resource;
	}

	public String getName()
	{
		return this.name;
	}

	public boolean isFork()
	{
		return this.getName().equals("fork") && this.getResource().equals("message");
	}

	public boolean isProceed()
	{
		return this.getName().equals("proceed") && this.getResource().equals("message");
	}

	public boolean isReturn()
	{
		return this.getName().equals("return") && this.getResource().equals("message");
	}
}
