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
public class CompilerAction implements Serializable
{
	private static final long serialVersionUID = 879078266731356034L;

	private String name;

	private String argument;

	public CompilerAction()
	{}

	public String getName()
	{
		return name;
	}

	public void setName(String inName)
	{
		name = inName;
	}

	public String getArgument()
	{
		return argument;
	}

	public void setArgument(String inArgument)
	{
		argument = inArgument;
	}
}
