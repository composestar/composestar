/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2006 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */

package Composestar.Core.Config;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import Composestar.Core.Exception.ConfigurationException;
import Composestar.Core.INCRE.INCREModule;
import Composestar.Core.Master.CTCommonModule;
import Composestar.Utils.Logging.CPSLogger;

/**
 * Encapsulates the data in the module's moduleinfo.xml <br />
 * Create an instance of this class using the <code>ModuleInfo.load()</code>
 * static method. To use this information in a module request the instance
 * through the Configuration class.
 * 
 * @author Michiel Hendriks
 */
public class ModuleInfo implements Serializable
{
	private static final long serialVersionUID = -818944551130427548L;

	/**
	 * Module identifier
	 */
	protected String id;

	/**
	 * The class of the module
	 */
	protected Class<? extends CTCommonModule> moduleClass;

	/**
	 * Human-readable name of the module
	 */
	protected String name = "";

	/**
	 * Description of the module (in HTML?)
	 */
	protected String description = "";

	/**
	 * Contains the module settings
	 */
	protected Map<String, ModuleSetting<?>> settings;

	/**
	 * List of modules this module depends on;
	 */
	protected Set<String> dependson;

	/**
	 * The incre module settings
	 */
	protected INCREModule increModule;

	protected transient CPSLogger moduleLogger;

	public ModuleInfo(String moduleId)
	{
		setId(moduleId);
		settings = new HashMap<String, ModuleSetting<?>>();
		dependson = new HashSet<String>();
	}

	public ModuleInfo(ModuleInfo copyFrom)
	{
		this(copyFrom.getId(), copyFrom);
	}

	public ModuleInfo(String moduleId, ModuleInfo copyFrom)
	{
		this(moduleId);
		if (!deepCopy(copyFrom))
		{
			throw new IllegalArgumentException("Unable to copy data");
		}
	}

	/**
	 * @return the module ID
	 */
	public String getId()
	{
		return id;
	}

	/**
	 * Set the module ID
	 * 
	 * @param moduleId
	 */
	protected void setId(String moduleId)
	{
		if (moduleId == null || moduleId.trim().length() == 0)
		{
			throw new IllegalArgumentException("Module id can not be null or empty");
		}
		id = moduleId.trim();
	}

	/**
	 * @return the module class
	 */
	public Class<? extends CTCommonModule> getModuleClass()
	{
		return moduleClass;
	}

	/**
	 * set the module class
	 * 
	 * @param cls
	 */
	public void setModuleClass(Class<? extends CTCommonModule> cls)
	{
		if (cls == null)
		{
			throw new IllegalArgumentException("Module class can not be null");
		}
		moduleClass = cls;
		if (increModule != null)
		{
			increModule.setModuleClass(cls);
		}
	}

	/**
	 * Set the module class according to the provided string. The class must
	 * implement {@link CTCommonModule}
	 * 
	 * @param cls
	 * @throws IllegalArgumentException when the string is null or empty
	 * @throws ClassNotFoundException
	 */
	public void setModuleClass(String cls) throws IllegalArgumentException, ClassNotFoundException
	{
		if (cls == null || cls.trim().length() == 0)
		{
			throw new IllegalArgumentException("Module class can not be null or empty");
		}
		try
		{
			Class<?> clazz = Class.forName(cls.trim());
			if (CTCommonModule.class.isAssignableFrom(clazz))
			{
				setModuleClass(clazz.asSubclass(CTCommonModule.class));
			}
			else
			{
				throw new IllegalArgumentException(String.format("%s does not implement CTCommonModule", cls));
			}
		}
		catch (ClassNotFoundException e)
		{
			throw new IllegalArgumentException(e);
		}
	}

	/**
	 * @return the human readable name of the class.
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Set the human readable name
	 * 
	 * @param newName
	 */
	public void setName(String newName)
	{
		if (newName == null)
		{
			newName = "";
		}
		name = newName;
	}

	/**
	 * Get a description of this module
	 * 
	 * @return
	 */
	public String getDescription()
	{
		return description;
	}

	/**
	 * Set the module description
	 * 
	 * @param desc
	 */
	public void setDescription(String desc)
	{
		if (desc == null)
		{
			desc = "";
		}
		description = desc;
	}

	/**
	 * Get the INCRE configuration
	 * 
	 * @return
	 */
	public INCREModule getIncreModule()
	{
		return increModule;
	}

	/**
	 * Set the INCRE configuration
	 * 
	 * @param inModule
	 */
	public void setIncreModule(INCREModule inModule)
	{
		increModule = inModule;
	}

	/**
	 * Add a dependency for this module
	 * 
	 * @param moduleId
	 * @return
	 */
	public boolean addDepedency(String moduleId)
	{
		if (moduleId == null || moduleId.trim().length() == 0)
		{
			return false;
		}
		return dependson.add(moduleId);
	}

	/**
	 * Remove a dependency
	 * 
	 * @param moduleId
	 * @return
	 */
	public boolean removeDependency(String moduleId)
	{
		return dependson.remove(moduleId);
	}

	/**
	 * Get all dependencies of this module
	 * 
	 * @return
	 */
	public Set<String> getDependencies()
	{
		return Collections.unmodifiableSet(dependson);
	}

	/**
	 * Add a module setting
	 * 
	 * @param ms
	 * @throws ConfigurationException
	 */
	public void addModuleSetting(ModuleSetting<?> ms) throws ConfigurationException
	{
		if (settings.containsKey(ms.getId()))
		{
			throw new ConfigurationException("Duplicate module setting: " + ms.getId());
		}
		settings.put(ms.getId(), ms);
	}

	/**
	 * Get the module setting with the given key.
	 * 
	 * @param <T>
	 * @param key
	 * @return
	 * @throws ConfigurationException
	 */
	@SuppressWarnings("unchecked")
	public <T extends Serializable> ModuleSetting<T> getModuleSetting(String key) throws ConfigurationException
	{
		if (settings.containsKey(key))
		{
			ModuleSetting<T> ms = (ModuleSetting<T>) settings.get(key);
			return ms;
		}
		throw new ConfigurationException("Requested unknown module setting '" + key + "' for module '" + id + "'");
	}

	/**
	 * @return all settings for this module
	 */
	public Iterator<ModuleSetting<?>> getSettings()
	{
		return settings.values().iterator();
	}

	/**
	 * Remove a setting
	 * 
	 * @param key
	 */
	public void removeSetting(String key)
	{
		settings.remove(key);
	}

	/**
	 * Reset the settings to their default values
	 */
	public void resetSettings()
	{
		for (ModuleSetting<?> ms : settings.values())
		{
			ms.reset();
		}
	}

	/**
	 * Set the value for a given setting
	 * 
	 * @param <T>
	 * @param key
	 * @param newValue
	 * @throws ConfigurationException
	 */
	public <T extends Serializable> void setSettingValue(String key, T newValue) throws ConfigurationException
	{
		ModuleSetting<T> ms = getModuleSetting(key);
		ms.setValue(newValue);
	}

	/**
	 * @see #setSettingValue(String, Serializable)
	 * @param key
	 * @param newValue
	 * @throws ConfigurationException
	 */
	public void setSettingValue(String key, int newValue) throws ConfigurationException
	{
		setSettingValue(key, Integer.valueOf(newValue));
	}

	/**
	 * @see #setSettingValue(String, Serializable)
	 * @param key
	 * @param newValue
	 * @throws ConfigurationException
	 */
	public void setSettingValue(String key, boolean newValue) throws ConfigurationException
	{
		setSettingValue(key, Boolean.valueOf(newValue));
	}

	/**
	 * @see #setSettingValue(String, Serializable)
	 * @param key
	 * @param newValue
	 * @throws ConfigurationException
	 */
	public void setSettingValue(String key, float newValue) throws ConfigurationException
	{
		setSettingValue(key, Float.valueOf(newValue));
	}

	/**
	 * Get the value of a setting
	 * 
	 * @param <T>
	 * @param key
	 * @return
	 * @throws ConfigurationException
	 */
	public <T extends Serializable> T getSetting(String key) throws ConfigurationException
	{
		ModuleSetting<T> ms;
		ms = getModuleSetting(key);
		return ms.getValue();
	}

	/**
	 * Get the value of a setting, if it doesn't exist return the given default
	 * value
	 * 
	 * @param <T>
	 * @param key
	 * @param defval the default value to return
	 * @return
	 */
	public <T extends Serializable> T getSetting(String key, T defval)
	{
		try
		{
			ModuleSetting<T> ms;
			ms = getModuleSetting(key);
			return ms.getValue();
		}
		catch (ConfigurationException e)
		{
			return defval;
		}
	}

	/**
	 * @deprecated use getSetting(key,def)
	 */
	@Deprecated
	public String getStringSetting(String key, String def)
	{
		return getSetting(key, def);
	}

	/**
	 * @deprecated use getSetting(key,def)
	 */
	@Deprecated
	public int getIntSetting(String key, int def)
	{
		return getSetting(key, def);
	}

	/**
	 * @deprecated use getSetting(key,def)
	 */
	@Deprecated
	public boolean getBooleanSetting(String key, boolean def)
	{
		return getSetting(key, def);
	}

	/**
	 * @deprecated use getSetting(key,def)
	 */
	@Deprecated
	public float getFloatSetting(String key, float def)
	{
		return getSetting(key, def);
	}

	/**
	 * @throws ConfigurationException
	 * @deprecated use getSetting(key)
	 */
	@Deprecated
	public String getStringSetting(String key) throws ConfigurationException
	{
		return getSetting(key);
	}

	/**
	 * @see #getSetting(String)
	 * @param key
	 * @return
	 * @throws ConfigurationException
	 */
	public int getIntSetting(String key) throws ConfigurationException
	{
		Integer val = getSetting(key);
		return val;
	}

	/**
	 * @see #getSetting(String)
	 * @param key
	 * @return
	 * @throws ConfigurationException
	 */
	public boolean getBooleanSetting(String key) throws ConfigurationException
	{
		Boolean val = getSetting(key);
		return val;
	}

	/**
	 * @see #getSetting(String)
	 * @param key
	 * @return
	 * @throws ConfigurationException
	 */
	public float getFloatSetting(String key) throws ConfigurationException
	{
		Float val = getSetting(key);
		return val;
	}

	/**
	 * Initialize the configuration
	 */
	public void initConfig(BuildConfig bcfg)
	{
		resetSettings();
		if (bcfg == null)
		{
			return;
		}
		Map<String, String> config = bcfg.getSettings();
		for (Entry<String, String> entry : config.entrySet())
		{
			if (!entry.getKey().startsWith(id + "."))
			{
				continue;
			}
			String key = entry.getKey().substring(id.length() + 1);
			try
			{
				ModuleSetting<?> set = getModuleSetting(key);
				set.setValueFromString(entry.getValue());
			}
			catch (ConfigurationException e)
			{
				if (moduleLogger == null)
				{
					moduleLogger = CPSLogger.getCPSLogger(id);
				}
				moduleLogger.warn("Error setting config option '" + entry.getKey() + "' to '" + entry.getValue()
						+ "': " + e.getMessage());
			}
		}
	}

	/**
	 * Perform a deep copy
	 * 
	 * @param copyFrom
	 */
	protected boolean deepCopy(ModuleInfo copyFrom)
	{
		ModuleInfo copy = null;
		try
		{
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(bos);
			out.writeObject(copyFrom);
			out.flush();
			out.close();

			ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray()));
			copy = (ModuleInfo) in.readObject();

			// copy back data
			// id = copy.id; // don't copy Id
			description = copy.description;
			increModule = copy.increModule;
			moduleClass = copy.moduleClass;
			name = copy.name;
			settings = copy.settings;
			dependson = copy.dependson;

			if (increModule != null)
			{
				increModule.setName(id);
				increModule.setModuleClass(moduleClass);
			}
		}
		catch (IOException e)
		{
			throw new IllegalArgumentException(e);
		}
		catch (ClassNotFoundException cnfe)
		{
			throw new IllegalArgumentException(cnfe);
		}
		return true;
	}
}
