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
	protected Class<?> moduleClass;

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

	public String getId()
	{
		return id;
	}

	protected void setId(String moduleId)
	{
		if (moduleId == null || moduleId.trim().length() == 0)
		{
			throw new IllegalArgumentException("Module id can not be null or empty");
		}
		id = moduleId.trim();
	}

	public Class<?> getModuleClass()
	{
		return moduleClass;
	}

	public void setModuleClass(Class<?> cls)
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

	public void setModuleClass(String cls)
	{
		if (cls == null || cls.trim().length() == 0)
		{
			throw new IllegalArgumentException("Module class can not be null or empty");
		}
		try
		{
			setModuleClass(Class.forName(cls.trim()));
		}
		catch (ClassNotFoundException e)
		{
			throw new IllegalArgumentException(e);
		}
	}

	public String getName()
	{
		return name;
	}

	public void setName(String newName)
	{
		if (newName == null)
		{
			newName = "";
		}
		name = newName;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String desc)
	{
		if (desc == null)
		{
			desc = "";
		}
		description = desc;
	}

	public INCREModule getIncreModule()
	{
		return increModule;
	}

	public void setIncreModule(INCREModule inModule)
	{
		increModule = inModule;
	}

	public boolean addDepedency(String moduleId)
	{
		if (moduleId == null || moduleId.trim().length() == 0)
		{
			return false;
		}
		return dependson.add(moduleId);
	}

	public boolean removeDependency(String moduleId)
	{
		return dependson.remove(moduleId);
	}

	public Set<String> getDependencies()
	{
		return Collections.unmodifiableSet(dependson);
	}

	public void addModuleSetting(ModuleSetting<?> ms) throws ConfigurationException
	{
		if (settings.containsKey(ms.getId()))
		{
			throw new ConfigurationException("Duplicate module setting: " + ms.getId());
		}
		settings.put(ms.getId(), ms);
	}

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

	public Iterator<ModuleSetting<?>> getSettings()
	{
		return settings.values().iterator();
	}

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

	public <T extends Serializable> void setSettingValue(String key, T newValue) throws ConfigurationException
	{
		ModuleSetting<T> ms = getModuleSetting(key);
		ms.setValue(newValue);
	}

	public void setSettingValue(String key, int newValue) throws ConfigurationException
	{
		setSettingValue(key, Integer.valueOf(newValue));
	}

	public void setSettingValue(String key, boolean newValue) throws ConfigurationException
	{
		setSettingValue(key, Boolean.valueOf(newValue));
	}

	public void setSettingValue(String key, float newValue) throws ConfigurationException
	{
		setSettingValue(key, Float.valueOf(newValue));
	}

	public <T extends Serializable> T getSetting(String key) throws ConfigurationException
	{
		ModuleSetting<T> ms;
		ms = getModuleSetting(key);
		return ms.getValue();
	}

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

	public int getIntSetting(String key) throws ConfigurationException
	{
		Integer val = getSetting(key);
		return val;
	}

	public boolean getBooleanSetting(String key) throws ConfigurationException
	{
		Boolean val = getSetting(key);
		return val;
	}

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
				setSettingValue(key, entry.getValue());
			}
			catch (ConfigurationException e)
			{
				if (moduleLogger == null)
				{
					moduleLogger = CPSLogger.getCPSLogger(id);
				}
				moduleLogger.error("Error setting config option '" + entry.getKey() + "' to '" + entry.getValue()
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
