/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */

package Composestar.Core.Resources;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import Composestar.Core.Annotations.ComposestarModule;
import Composestar.Core.Annotations.In;
import Composestar.Core.Annotations.ModuleSetting;
import Composestar.Core.Annotations.Out;
import Composestar.Core.Annotations.ResourceManager;
import Composestar.Core.Config.BuildConfig;
import Composestar.Core.CpsRepository2.Repository;
import Composestar.Utils.Logging.CPSLogger;

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

	protected static final CPSLogger logger = CPSLogger.getCPSLogger("Resources");

	/**
	 * Map holding all the resources
	 */
	protected Map<String, Object> resources;

	/**
	 * Unserializable resources
	 */
	protected transient Map<String, Object> resourcesEx;

	/**
	 * The build configuration for the current project
	 */
	protected BuildConfig config;

	/**
	 * The CPS Language repository
	 */
	protected Repository repository;

	/**
	 * Reference to the path resolver. The path resolver provides means to
	 * resolve the paths for external compiler resources.
	 */
	protected transient PathResolver pathResolver;

	protected Map<Class<? extends ModuleResourceManager>, ModuleResourceManager> resourceManagers;

	/**
	 * Default constructor.
	 */
	public CommonResources()
	{
		resources = new HashMap<String, Object>();
		resourcesEx = new HashMap<String, Object>();
		resourceManagers = new HashMap<Class<? extends ModuleResourceManager>, ModuleResourceManager>();
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

	public void setRepository(Repository ds)
	{
		repository = ds;
	}

	/**
	 * Retrieve the repository instance that contains all Compose* language
	 * elements.
	 * 
	 * @return
	 */
	public Repository repository()
	{
		return repository;
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
	 * @deprecated Use {@link #put(String,Object)} instead
	 */
	@Deprecated
	public void add(String key, Object object)
	{
		put(key, object);
	}

	/**
	 * Add a resource with a key.
	 * 
	 * @param key An identifier for this resource.
	 * @param object The object to store for this key.
	 */
	public void put(String key, Object object)
	{
		if (object instanceof Serializable)
		{
			resources.put(key, object);
		}
		else
		{
			resourcesEx.put(key, object);
		}
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
	public <T extends Object> T get(String key)
	{
		Object res = resources.get(key);
		if (res == null)
		{
			res = resourcesEx.get(key);
		}
		if (res == null)
		{
			return null;
		}
		return (T) res;
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

	/**
	 * Get a specific resource manager
	 * 
	 * @param <T>
	 * @param type
	 * @return
	 */
	public <T extends ModuleResourceManager> T getResourceManager(Class<T> type)
	{
		try
		{
			ModuleResourceManager res = resourceManagers.get(type);
			if (res == null)
			{
				return null;
			}
			return type.cast(res);
		}
		catch (ClassCastException e)
		{
			return null;
		}
	}

	/**
	 * Get a specific resource manager
	 * 
	 * @param <T>
	 * @param type
	 * @return
	 */
	public <T extends ModuleResourceManager> T getResourceManager(Class<T> type, boolean bCreate)
	{
		T result = getResourceManager(type);
		if (result == null && bCreate)
		{
			try
			{
				logger.debug(String.format("Creating new instance of %s", type.getName()));
				result = type.newInstance();
				addResourceManager(result);
			}
			catch (InstantiationException e)
			{
				logger.error(e);
			}
			catch (IllegalAccessException e)
			{
				logger.error(e);
			}
		}
		return result;
	}

	/**
	 * Register a resource manager
	 * 
	 * @param value
	 */
	public void addResourceManager(ModuleResourceManager value)
	{
		resourceManagers.put(value.getClass(), value);
	}

	public void removeResourceManager(ModuleResourceManager key)
	{
		removeResourceManager(key.getClass());
	}

	public void removeResourceManager(Class<? extends ModuleResourceManager> key)
	{
		resourceManagers.remove(key);
	}

	public <T> T create(String key, Class<T> c)
	{
		T resource = create(c);
		put(key, resource);
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

	/**
	 * Inject resources from the common resources
	 * 
	 * @param object
	 */
	public void inject(Object object)
	{
		Class<?> c = object.getClass();
		while (c != null)
		{
			for (Field field : c.getDeclaredFields())
			{
				ResourceManager rm = field.getAnnotation(ResourceManager.class);
				if (rm != null)
				{
					if (ModuleResourceManager.class.isAssignableFrom(field.getType()))
					{
						Class<? extends ModuleResourceManager> mrmType = (Class<? extends ModuleResourceManager>) field
								.getType();
						try
						{
							field.setAccessible(true);
							field.set(object, getResourceManager(mrmType, rm.create()));
						}
						catch (IllegalAccessException e)
						{
							throw new ResourceException("Could not access field '" + field.getName() + "'");
						}
					}
					else
					{
						logger
								.warn(String
										.format(
												"Incorrect annotation on field %s.%s: %s is not a ModuleResourceManager implementation",
												object.getClass().getName(), field.getName(), field.getType().getName()));
					}
				}

				ModuleSetting ms = field.getAnnotation(ModuleSetting.class);
				if (ms != null)
				{
					assignSetting(object, field, ms);
				}

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
			c = c.getSuperclass();
		}
	}

	/**
	 * Assigns the values of settings in the configuration file to the fields
	 * with ModuleSetting annotations.
	 * 
	 * @param subject
	 * @param field
	 * @param setting
	 */
	protected void assignSetting(Object subject, Field field, ModuleSetting setting)
	{
		// Construct the setting id
		String settingId = setting.ID();
		if (settingId.length() == 0)
		{
			settingId = field.getName();
		}
		if (settingId.indexOf('.') == -1)
		{
			// No explicit module defined, use the module ID of the subject
			ComposestarModule cm = subject.getClass().getAnnotation(ComposestarModule.class);
			if (cm != null)
			{
				settingId = cm.ID() + "." + settingId;
			}
			else
			{
				// TODO report an error/warning? and return?
			}
		}
		if (settingId.indexOf('.') == 0)
		{
			// requesting root item
			settingId = settingId.substring(1);
		}
		String value = config.getSetting(settingId);
		if (value == null)
		{
			// has not been set, leave it as the default
			return;
		}
		// Convert the string value
		Object objectValue = null;
		Class<?> valueType = field.getType();
		if (valueType == String.class || (setting.setter().length() > 0 && setting.setterTakesString()))
		{
			objectValue = value;
			valueType = String.class;
		}
		else if (valueType == Integer.class || valueType == int.class)
		{
			if (value.length() == 0)
			{
				return;
			}
			try
			{
				objectValue = Integer.valueOf(value);
			}
			catch (NumberFormatException e)
			{
				logger.error(String.format("The value of setting %s is not a valid integer: %s", settingId, value));
				return;
			}
		}
		else if (valueType == Float.class || valueType == float.class)
		{
			if (value.length() == 0)
			{
				return;
			}
			try
			{
				objectValue = Float.valueOf(value);
			}
			catch (NumberFormatException e)
			{
				logger.error(String.format("The value of setting %s is not a valid float: %s", settingId, value));
				return;
			}
		}
		else if (valueType == Double.class || valueType == double.class)
		{
			if (value.length() == 0)
			{
				return;
			}
			try
			{
				objectValue = Double.valueOf(value);
			}
			catch (NumberFormatException e)
			{
				logger.error(String.format("The value of setting %s is not a valid double: %s", settingId, value));
				return;
			}
		}
		else if (valueType == Long.class || valueType == long.class)
		{
			if (value.length() == 0)
			{
				return;
			}
			try
			{
				objectValue = Long.valueOf(value);
			}
			catch (NumberFormatException e)
			{
				logger.error(String.format("The value of setting %s is not a valid long: %s", settingId, value));
				return;
			}
		}
		else if (valueType == Boolean.class || valueType == boolean.class)
		{
			if (value.length() == 0)
			{
				return;
			}
			objectValue = Boolean.valueOf(value);
		}
		else if (Enum.class.isAssignableFrom(field.getType()))
		{
			if (value.length() == 0)
			{
				return;
			}
			try
			{
				Class<Enum> enumcls = (Class<Enum>) field.getType();
				objectValue = (Enum) Enum.valueOf(enumcls, value);
			}
			catch (IllegalArgumentException e)
			{
				logger.error(String.format("The value '%s' of setting %s is not a valid enum value of type %s", value,
						settingId, field.getType().getName()));
				return;
			}
		}
		else
		{
			logger.error(String.format("Settings field %s in class %s does not have a settable type %s.", field
					.getName(), subject.getClass().getName(), field.getType()));
			return;
		}
		// Assign the value
		if (setting.setter().length() > 0)
		{
			// Delegate the setting to a setter
			try
			{
				Method setter = subject.getClass().getMethod(setting.setter(), valueType);
				setter.invoke(subject, objectValue);
			}
			catch (SecurityException e)
			{
				logger.error(String.format("Setter %s for settings field %s in class %s not public", setting.setter(),
						field.getName(), subject.getClass().getName()));
			}
			catch (NoSuchMethodException e)
			{
				logger.error(String.format("Setter %s for settings field %s in class %s not found", setting.setter(),
						field.getName(), subject.getClass().getName()));
			}
			catch (IllegalArgumentException e)
			{
				logger.error(String.format("Error calling setter %s for settings field %s in class %s", setting
						.setter(), field.getName(), subject.getClass().getName()), e);
			}
			catch (IllegalAccessException e)
			{
				logger.error(String.format("Setter %s for settings field %s in class %s not public", setting.setter(),
						field.getName(), subject.getClass().getName()));
			}
			catch (InvocationTargetException e)
			{
				logger.error(String.format("Error calling setter %s for settings field %s in class %s", setting
						.setter(), field.getName(), subject.getClass().getName()), e);
			}
		}
		else
		{
			// otherwise directly write the variable
			try
			{
				field.setAccessible(true);
				field.set(subject, objectValue);
			}
			catch (IllegalArgumentException e)
			{
				logger.error(String.format("Error setting settings field %s in class %s", field.getName(), subject
						.getClass().getName()), e);
			}
			catch (IllegalAccessException e)
			{
				logger.error(String.format("Error setting settings field %s in class %s", field.getName(), subject
						.getClass().getName()), e);
			}
		}
	}

	public void extract(Object object)
	{
		Class<?> c = object.getClass();
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
					put(out.value(), value);
				}
				catch (IllegalAccessException e)
				{
					throw new ResourceException("Could not access field '" + field.getName() + "'");
				}
			}
		}
	}

	private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException
	{
		stream.defaultReadObject();
		resourcesEx = new HashMap<String, Object>();
	}
}
