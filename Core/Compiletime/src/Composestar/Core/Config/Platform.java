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

import java.io.File;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import Composestar.Core.Resources.PathResolver;
import Composestar.Utils.Logging.CPSLogger;

/**
 * A platform configuration. This defines all information for a platform that
 * various modules need to proper operate. This include information about
 * required libraries and supported languages.
 * 
 * @author Michiel Hendriks
 */
public class Platform implements Serializable
{
	private static final long serialVersionUID = 7218107389529628503L;

	protected static final CPSLogger logger = CPSLogger.getCPSLogger("Configuration.Platform");

	/**
	 * The platform identifier
	 */
	protected String id;

	/**
	 * Optional OS filter for this platform.
	 */
	protected OSFilter osfilter;

	/**
	 * Registered languages
	 */
	protected Map<String, Language> languages;

	/**
	 * Additional project resources that also need to be copied to the output.
	 */
	protected Set<FileCollection> resources;

	/**
	 * Cache with resolved resource files.
	 */
	protected transient Set<File> resourceFiles;

	public Platform(String newId)
	{
		setId(newId);
		languages = new HashMap<String, Language>();
		resources = new HashSet<FileCollection>();
	}

	/**
	 * @return the platform ID
	 */
	public String getId()
	{
		return id;
	}

	/**
	 * Set the platform ID
	 * 
	 * @param newId
	 */
	protected void setId(String newId)
	{
		if (newId == null || newId.trim().length() == 0)
		{
			throw new IllegalArgumentException("Id can not be null or empty");
		}
		id = newId.trim();
	}

	/**
	 * @return the OS filter, can be null
	 */
	public OSFilter getOSFilter()
	{
		return osfilter;
	}

	/**
	 * Set an OS filter for this platform
	 * 
	 * @param filter
	 */
	public void setOSFilter(OSFilter filter)
	{
		osfilter = filter;
	}

	/**
	 * Add a language to the current platform
	 * 
	 * @param lang
	 */
	public void addLanguage(Language lang)
	{
		if (lang == null)
		{
			throw new IllegalArgumentException("Language can not be null");
		}
		String langName = lang.getName();
		if (langName == null || langName.trim().length() == 0)
		{
			throw new IllegalArgumentException("Language does not have a valid name");
		}
		if (languages.containsKey(langName))
		{
			logger.warn("Platform " + id + " already has a language with the name " + langName);
			return;
		}
		languages.put(langName, lang);
	}

	/**
	 * Get the language with the given name
	 * 
	 * @param name
	 * @return
	 */
	public Language getLanguage(String name)
	{
		return languages.get(name);
	}

	/**
	 * @return the available languages
	 */
	public Collection<Language> getLanguages()
	{
		return Collections.unmodifiableCollection(languages.values());
	}

	/**
	 * Add a platform resource
	 * 
	 * @param res
	 */
	public void addResource(FileCollection res)
	{
		if (res == null)
		{
			return;
		}
		resources.add(res);
		resourceFiles = null;
	}

	/**
	 * Remove a resource
	 * 
	 * @param res
	 */
	public void removeResource(FileCollection res)
	{
		if (resources.remove(res))
		{
			resourceFiles = null;
		}
	}

	/**
	 * Add a set of file resources
	 * 
	 * @param res
	 */
	public void addResources(Set<FileCollection> res)
	{
		if (res == null)
		{
			return;
		}
		if (resources.addAll(res))
		{
			resourceFiles = null;
		}
	}

	/**
	 * Return a list of resources files.
	 * 
	 * @return
	 */
	public Set<File> getResourceFiles(PathResolver resolver)
	{
		if (resourceFiles == null)
		{
			resourceFiles = new HashSet<File>();
			for (FileCollection col : resources)
			{
				for (File file : col.getFiles())
				{
					File resfile = resolver.getResource(file);
					if (resfile == null)
					{
						logger.warn("Resource file does not exist: " + file.toString());
					}
					else
					{
						resourceFiles.add(file);
					}
				}
			}
		}
		return Collections.unmodifiableSet(resourceFiles);
	}

	/**
	 * Return an readonly reference to the platform resources.
	 * 
	 * @return
	 */
	public Set<FileCollection> getResources()
	{
		return Collections.unmodifiableSet(resources);
	}
}
