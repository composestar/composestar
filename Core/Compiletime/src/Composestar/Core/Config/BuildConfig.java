/*
 * This file is part of the Compose* project.
 * http://composestar.sourceforge.net
 * Copyright (C) 2007 University of Twente.
 *
 * Compose* is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as 
 * published by the Free Software Foundation; either version 2.1 of 
 * the License, or (at your option) any later version.
 *
 * Compose* is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public 
 * License along with this program. If not, see 
 * <http://www.gnu.org/licenses/>.
 *
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.txt
 *
 * $Id$
 */

package Composestar.Core.Config;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import Composestar.Utils.Logging.CPSLogger;

/**
 * The configuration for the current project that will be compiled.
 * 
 * @author Michiel Hendriks
 */
public class BuildConfig implements Serializable
{
	protected static final CPSLogger logger = CPSLogger.getCPSLogger("Configuration.Build");

	private static final long serialVersionUID = 5759753577247818565L;

	/**
	 * Contains the project specific settings for the compiler.
	 */
	protected Map<String, String> settings;

	protected Project project;

	protected Filters filters;

	public BuildConfig()
	{
		settings = new HashMap<String, String>();
		filters = new Filters();
	}

	public void clearSettings()
	{
		settings.clear();
	}

	public void addSetting(String key, String value)
	{
		if (key == null || key.trim().length() == 0)
		{
			throw new IllegalArgumentException("Key can not be null or empty");
		}
		settings.put(key, value);
	}

	public String getSetting(String key)
	{
		return settings.get(key);
	}

	public Map<String, String> getSettings()
	{
		return Collections.unmodifiableMap(settings);
	}

	/**
	 * Creates a new project and return it's handle. The BuildConfig only
	 * supports a single project, therefore calling this method will discard the
	 * previous project.
	 * 
	 * @return
	 */
	public Project getNewProject()
	{
		if (project != null)
		{
			logger.warn("Discarding previously created project");
		}
		project = new Project();
		return project;
	}

	public Project getProject()
	{
		return project;
	}

	public Filters getFilters()
	{
		return filters;
	}
}
