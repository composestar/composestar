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

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import Composestar.Core.Annotations.In;
import Composestar.Core.Annotations.Out;
import Composestar.Core.Config.BuildConfig;

/**
 * This class holds the shared resources between the modules e.g the repository
 * object. Use the common resources to store information only required at
 * runtime. Do not use the DataStore or dynamic maps of repository entities for
 * this because they will be saved in the repository file. The CommonResources
 * will also be saved to the compilation history file, but only the entries that
 * implement Serializable. Information that is important for incremental
 * compilation or visualization should be made serializable.
 */
public class CommonResources implements Serializable
{
	private static final long serialVersionUID = -4099474761502163870L;

	/**
	 * Map holding all the resources
	 */
	private Map<String, Object> resources;

	protected BuildConfig config;

	protected PathResolver pathResolver;

	/**
	 * Default constructor.
	 */
	public CommonResources()
	{
		resources = new HashMap<String, Object>();
	}

	public void setConfiguration(BuildConfig inConfig)
	{
		config = inConfig;
	}

	/**
	 * Return the current build configuration
	 * 
	 * @return
	 */
	public BuildConfig configuration()
	{
		return config;
	}

	/**
	 * Return the path resolver.
	 * 
	 * @param inResolver
	 */
	public void setPathResolver(PathResolver inResolver)
	{
		pathResolver = inResolver;
	}

	public PathResolver getPathResolver()
	{
		return pathResolver;
	}

	/**
	 * Add a resource with a key.
	 * 
	 * @param key An identifier for this resource.
	 * @param object The object to store for this key.
	 */
	public void add(String key, Object object)
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
	public Object get(String key)
	{
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
		Object resource = get(key);

		if (resource == null)
		{
			throw new ResourceException("No resource for key '" + key + "'");
		}

		if (!(resource instanceof Boolean))
		{
			throw new ResourceException("Resource with key '" + key + "' is not a Boolean");
		}

		return (Boolean) resource;
	}

	public <T> T create(String key, Class<T> c)
	{
		T resource = create(c);
		add(key, resource);
		return resource;
	}

	public <T> T create(Class<T> c)
	{
		try
		{
			T resource = c.newInstance();
			inject(resource);
			return resource;
		}
		catch (InstantiationException e)
		{
			throw new ResourceException("Could not create resource of class '" + c.getName() + "'" + e.getMessage());
		}
		catch (IllegalAccessException e)
		{
			throw new ResourceException("Could not create resource of class '" + c.getName() + "'" + e.getMessage());
		}
	}

	public void inject(Object object)
	{
		Class c = object.getClass();
		for (Field field : c.getFields())
		{
			In in = field.getAnnotation(In.class);
			Out out = field.getAnnotation(Out.class);

			if (in != null)
			{
				if (out != null)
				{
					throw new ResourceException("Field '" + field.getName() + "' "
							+ "cannot have both an In and an Out annotation");
				}

				Object value = get(in.value());

				if (value == null && in.required())
				{
					throw new ResourceException("No value for required resource '" + in.value() + "'");
				}

				if (value == null)
				{
					continue;
				}

				if (!field.getClass().isAssignableFrom(value.getClass()))
				{
					throw new ResourceException("Resource '" + in.value() + "' " + "is not assignable to field '"
							+ field.getName() + "'");
				}

				try
				{
					field.setAccessible(true);
					field.set(object, value);
				}
				catch (IllegalAccessException e)
				{
					throw new ResourceException("Could not access field '" + field.getName() + "'");
				}
			}
		}
	}

	public void extract(Object object)
	{
		Class c = object.getClass();
		for (Field field : c.getFields())
		{
			In in = field.getAnnotation(In.class);
			Out out = field.getAnnotation(Out.class);

			if (out != null)
			{
				if (in != null)
				{
					throw new ResourceException("Field '" + field.getName() + "' "
							+ "cannot have both an In and an Out annotation");
				}

				try
				{
					field.setAccessible(true);
					Object value = field.get(object);
					add(out.value(), value);
				}
				catch (IllegalAccessException e)
				{
					throw new ResourceException("Could not access field '" + field.getName() + "'");
				}
			}
		}
	}

	private void writeObject(java.io.ObjectOutputStream stream) throws IOException
	{
		Map<String, Object> temp = new HashMap<String, Object>(resources);
		Iterator<Entry<String, Object>> it = temp.entrySet().iterator();
		while (it.hasNext())
		{
			Entry<String, Object> entry = it.next();
			if (!(entry.getValue() instanceof Serializable))
			{
				it.remove();
			}
		}
		stream.writeObject(temp);
	}

	@SuppressWarnings("unchecked")
	private void readObject(java.io.ObjectInputStream stream) throws IOException, ClassNotFoundException
	{
		resources = (Map<String, Object>) stream.readObject();
	}
}
