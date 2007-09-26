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
import java.util.HashMap;
import java.util.Map;

/**
 * @deprecated
 */
@Deprecated
public class Modules implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4868992763327704934L;

	private Map<String, ModuleSettings> modules;

	public Modules()
	{
		modules = new HashMap<String, ModuleSettings>();
	}

	public ModuleSettings getModule(String key)
	{
		return modules.get(key);
	}

	public void addModule(String key, ModuleSettings m)
	{
		modules.put(key, m);
	}
}
