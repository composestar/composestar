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
import java.util.Properties;

import Composestar.Core.RepositoryImplementation.DataMap;
import Composestar.Utils.Debug;

/**
 * This class holds the shared resources between the modules e.g the repository object.
 * TODO: Add hashmap for storing non-global stuff.
 */
public class CommonResources implements Serializable
{
	private static final long serialVersionUID = 2652039710117430543L;

//	/**
//	 * The main project configuration file. This resource is initialized by Master
//	 */
//	public Properties ProjectConfiguration;
	
	/**
	 * Information about the Custom Filters that are used in this project.
	 * FIXME: create a getter?
	 */
	public Properties CustomFilters;

	/**
	 * Map holding all private resources.
	 * FIXME: this field is private, so we can use a HashMap?
	 */
	private DataMap resources;

	/**
	 * Default constructor.
	 */
	public CommonResources()
	{
		resources = new DataMap();
	}

	/**
	 * Add a resource with a key.
	 * 
	 * @param key An identifier for this resource.
	 * @param object The object to store for this key.
	 */
	public void addResource(String key, Object object)
	{
	//	Debug.out(Debug.MODE_DEBUG,"RES","Added resource: " + key + " -> " + object.getClass());
		resources.put(key, object);
	}

	/**
	 * Fetch a resource with a key.
	 * 
	 * @param key key for the object you want to retrieve.
	 * @return An object pointer if an object with the specified key was found 
	 * or null if the key is invalid.
	 */
	public Object getResource(String key)
	{
	//	Debug.out(Debug.MODE_DEBUG,"RES","Requested resource: " + key);

		if (!resources.containsKey(key))
			return null;

		return resources.get(key);
	}
}
