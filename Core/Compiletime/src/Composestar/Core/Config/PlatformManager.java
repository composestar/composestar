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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import Composestar.Utils.Logging.CPSLogger;

/**
 * Manager for the platform configurations. Use Project.getPlatform() to get the
 * handle for the platform configuration for the current project.
 * 
 * @author Michiel Hendriks
 */
public class PlatformManager
{
	protected static final CPSLogger logger = CPSLogger.getCPSLogger("PlatformConfig.Manager");

	protected static final PlatformManager instance = new PlatformManager();

	protected Map<String, Set<Platform>> platforms;

	protected PlatformManager()
	{
		platforms = new HashMap<String, Set<Platform>>();
	}

	/**
	 * Get the specified platform for the current OS.
	 * 
	 * @param id
	 * @return
	 */
	public static final Platform getPlatform(String id)
	{
		return getPlatform(id, null);
	}

	/**
	 * Get a platform for the specified OS.
	 * 
	 * @param id
	 * @param os the OS triple: name, version, arch
	 * @return
	 */
	public static final Platform getPlatform(String id, String[] os)
	{
		if (id == null || id.trim().length() == 0)
		{
			throw new IllegalArgumentException("Platform id can not be null or empty.");
		}
		Set<Platform> set = instance.platforms.get(id);
		Platform result = null;
		for (Platform platform : set)
		{
			OSFilter filter = platform.getOSFilter();
			if (filter == null)
			{
				if (result == null)
				{
					result = platform;
				}
				else
				{
					logger.warn("Two platforms without an OSFilter is not allowed. For platform id: " + id);
				}
				continue;
			}
			if (filter.matches(os))
			{
				return platform;
			}
		}
		return result;
	}

	public static final void addPlatform(Platform platform)
	{
		if (platform == null)
		{
			throw new IllegalArgumentException("Platform can not be null");
		}
		String id = platform.getId();
		if (id == null || id.length() == 0)
		{
			throw new IllegalArgumentException("Platform does not have a valid id");
		}
		Set<Platform> set = instance.platforms.get(id);
		if (set == null)
		{
			set = new HashSet<Platform>();
			instance.platforms.put(id, set);
		}
		set.add(platform);
	}
}
