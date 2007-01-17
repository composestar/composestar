/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */

package Composestar.Core.Master;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * This class holds the shared resources between the modules e.g the repository
 * object. Use the common resources to store information only required at
 * runtime. Do not use the DataStore or dynamic maps of repository entities for
 * this because they will be saved in the repository file.
 */
public class CommonResources implements Serializable
{
	private static final long serialVersionUID = 2652039710117430543L;

	/**
	 * Information about the Custom Filters that are used in this project.
	 * FIXME: this is never written, remove?
	 */
	public transient Properties CustomFilters;

	/**
	 * Map holding all the resources
	 */
	private transient Map<String, Object> resources;

	/**
	 * Default constructor.
	 */
	public CommonResources()
	{
		CustomFilters = new Properties();
		resources = new HashMap<String, Object>();
	}

	/**
	 * Add a resource with a key.
	 * 
	 * @param key An identifier for this resource.
	 * @param object The object to store for this key.
	 */
	public void addResource(String key, Object object)
	{
		resources.put(key, object);
	}

	public void addBoolean(String key, boolean value)
	{
		resources.put(key, value);
	}

	/**
	 * Fetch a resource with a key.
	 * 
	 * @param key key for the object you want to retrieve.
	 * @return An object pointer if an object with the specified key was found
	 *         or null if the key is invalid.
	 */
	public Object getResource(String key)
	{
		if (!resources.containsKey(key))
		{
			return null;
		}

		return resources.get(key);
	}

	/**
	 * Returns the resource with the specified key as a boolean.
	 * 
	 * @throws RuntimeException if there is no resource with the specified name,
	 *             or if it is not a Boolean.
	 */
	public boolean getBoolean(String key)
	{
		Object resource = getResource(key);

		if (resource == null)
		{
			throw new RuntimeException("No resource for key '" + key + "'");
		}

		if (!(resource instanceof Boolean))
		{
			throw new RuntimeException("Resource with key '" + key + "' is not a Boolean");
		}

		return (Boolean) resource;
	}
}
