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

import Composestar.Core.SECRET3.SECRETResources;
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
	 * Contains the project specific settings for the compiler. These settings
	 * should be considered to apply to all projects. Modules should use the
	 * ModuleInfo structures to get their settings.
	 * 
	 * @see ModuleInfo
	 */
	protected Map<String, String> settings;

	/**
	 * {@link Project}
	 */
	protected Project project;

	/**
	 * {@link Filters}
	 */
	protected Filters filters;

	/**
	 * {@link SECRETResources}
	 */
	protected SECRETResources secretResources;

	public BuildConfig()
	{
		settings = new HashMap<String, String>();
		filters = new Filters();
	}

	/**
	 * Clear the settings table
	 */
	public void clearSettings()
	{
		settings.clear();
	}

	/**
	 * Add or set a setting. The key may not be empty or null.
	 * 
	 * @param key
	 * @param value
	 */
	public void addSetting(String key, String value)
	{
		if (key == null || key.trim().length() == 0)
		{
			throw new IllegalArgumentException("Key can not be null or empty");
		}
		settings.put(key, value);
	}

	/**
	 * Return the value of the given setting. The result will be null when no
	 * value is set.
	 * 
	 * @param key
	 * @return
	 */
	public String getSetting(String key)
	{
		return settings.get(key);
	}

	/**
	 * Get a readonly copy of the settings map
	 * 
	 * @return
	 */
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

	/**
	 * Return the current project. Modules should also use this method to get a
	 * hold on the current project then their run method is executed. They
	 * should not cache the return value.
	 * 
	 * @return
	 */
	public Project getProject()
	{
		return project;
	}

	/**
	 * Return the filters object.
	 * 
	 * @return
	 */
	public Filters getFilters()
	{
		return filters;
	}

	/**
	 * Optional SECRET resources set in the configuration file. When no secret
	 * resource items were defined this will be null.
	 * 
	 * @return
	 */
	public SECRETResources getSecretResources()
	{
		return secretResources;
	}

	/**
	 * Set the SECRET resources.
	 * 
	 * @param inresc
	 */
	public void setSecretResources(SECRETResources inresc)
	{
		secretResources = inresc;
	}
}
