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
import java.util.Properties;

public class PathSettings implements Serializable
{
	private static final long serialVersionUID = 4406357801604000841L;

	private Properties paths;

	public PathSettings()
	{
		paths = new Properties();
	}

	public void addPath(String key, String value)
	{
		paths.setProperty(key, value);
	}

	public String getPath(String key)
	{
		return paths.getProperty(key);
	}

	public String getPath(String key, String def)
	{
		return paths.getProperty(key, def);
	}
}
