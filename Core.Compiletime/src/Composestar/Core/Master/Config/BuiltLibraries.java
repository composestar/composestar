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

public class BuiltLibraries implements Serializable
{
	private static final long serialVersionUID = -2556429479287190711L;

	private List<String> builtassemblies;

	public BuiltLibraries()
	{
		builtassemblies = new ArrayList<String>();
	}

	public void addLibrary(String file)
	{
		builtassemblies.add(file);
	}

	public List<String> getLibraries()
	{
		return builtassemblies;
	}
}
