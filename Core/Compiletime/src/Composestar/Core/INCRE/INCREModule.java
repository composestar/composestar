package Composestar.Core.INCRE;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import Composestar.Core.Exception.ModuleException;
import Composestar.Core.Master.CTCommonModule;
import Composestar.Core.Resources.CommonResources;
import Composestar.Core.Resources.ResourceException;
import Composestar.Utils.Logging.CPSLogger;
import Composestar.Utils.Logging.Log4j.CrucialLevel;

public class INCREModule implements Serializable
{
	private static final long serialVersionUID = -3962139604512378231L;

	private String moduleName;

	private Class<? extends CTCommonModule> moduleClass;

	private String input;

	private boolean enabled;

	private boolean incremental;

	private String summary = "";

	/**
	 * A map containing dependencies for the module.
	 */
	private Map<String, Dependency> deps = new LinkedHashMap<String, Dependency>();

	/**
	 * A map containing objects to be compared by MyComparator.
	 */
	private Map<String, List<Object>> comparableObjects = new HashMap<String, List<Object>>();

	public INCREModule(String inName)
	{
		moduleName = inName;
	}

	public void setName(String inName)
	{
		moduleName = inName;
	}

	public void setFullType(String ftype) throws ClassNotFoundException
	{
		Class<?> mclass = Class.forName(ftype);
		if (CTCommonModule.class.isAssignableFrom(mclass))
		{
			moduleClass = (Class<? extends CTCommonModule>) mclass;
		}
	}

	public void setModuleClass(Class<? extends CTCommonModule> ftype)
	{
		moduleClass = ftype;
	}

	public void setInput(String className)
	{
		input = className;
	}

	public void setEnabled(boolean b)
	{
		enabled = b;
	}

	public void setSummary(String s)
	{
		summary = s;
	}

	public void addDep(Dependency d)
	{
		String id = d.getName();
		deps.put(id, d);
	}

	public void clearDeps()
	{
		deps.clear();
	}

	public void addComparableObject(String key, Object obj)
	{
		List<Object> list;
		if (comparableObjects.containsKey(key))
		{
			list = comparableObjects.get(key);
		}
		else
		{
			list = new ArrayList<Object>();
		}

		list.add(obj);
		comparableObjects.put(key, list);
	}

	public void removeComparableObject(String key, Object obj)
	{
		List<Object> list;
		if (comparableObjects.containsKey(key))
		{
			list = comparableObjects.get(key);
		}
		else
		{
			list = new ArrayList<Object>();
		}

		list.remove(obj);
		comparableObjects.put(key, list);
	}

	public void addComparableObjects(Map<String, Map<?, Object>> map)
	{
		for (Entry<String, Map<?, Object>> entry : map.entrySet())
		{
			for (Object o1 : entry.getValue().values())
			{
				addComparableObject(entry.getKey(), o1);
			}
		}
	}

	public void removeComparableObjects(Map<String, Map<?, Object>> map)
	{
		for (Entry<String, Map<?, Object>> entry : map.entrySet())
		{
			for (Object o1 : entry.getValue().values())
			{
				removeComparableObject(entry.getKey(), o1);
			}
		}
	}

	public boolean hasComparableObjects(String key)
	{
		return comparableObjects.containsKey(key);
	}

	public List<Object> getComparableObjects(String key)
	{
		return comparableObjects.get(key);
	}

	public void clearComparableObjects()
	{
		comparableObjects.clear();
	}

	public Iterator<Composestar.Core.INCRE.Dependency> getDeps()
	{
		return deps.values().iterator();
	}

	public String getName()
	{
		return moduleName;
	}

	public String getFullType()
	{
		return moduleClass.getName();
	}

	public Class<? extends CTCommonModule> getModuleClass()
	{
		return moduleClass;
	}

	public String getSummary()
	{
		return summary;
	}

	public String getInput()
	{
		return input;
	}

	public boolean isIncremental()
	{
		return incremental;
	}

	public void setIncremental(boolean b)
	{
		incremental = b;
	}

	/**
	 * Creates an instance of a coreModule of type 'fulltype' and calls it run
	 * method
	 * 
	 * @param String phase of execution
	 * @param resources
	 */
	public void execute(CommonResources resources) throws ModuleException
	{
		if (!enabled)
		{
			return;
		}
		if (summary.length() != 0)
		{
			CPSLogger logger = CPSLogger.getCPSLogger(moduleName);
			logger.log(CrucialLevel.CRUCIAL, summary);
		}

		if (moduleClass == null)
		{
			throw new ModuleException("Module class has not been assigned", moduleName);
		}

		if (!CTCommonModule.class.isAssignableFrom(moduleClass))
		{
			throw new ModuleException("Module " + moduleClass + " does not implement interface CTCommonModule",
					moduleName);
		}

		try
		{
			CTCommonModule module = moduleClass.newInstance();
			INCRE.instance().addModuleByName(moduleName, module);

			resources.inject(module);

			INCRETimer timer = INCRE.instance().getReporter().openProcess(moduleName, moduleName, INCRETimer.TYPE_ALL);

			module.run(resources);
			timer.stop();

			resources.extract(module);
		}
		catch (InstantiationException e)
		{
			throw new ModuleException("Could not create an instance of '" + moduleClass + "': " + e.getMessage(),
					"INCRE running " + moduleName);
		}
		catch (IllegalAccessException e)
		{
			throw new ModuleException("Could not create an instance of '" + moduleClass + "': " + e.getMessage(),
					"INCRE running " + moduleName);
		}
		catch (ResourceException e)
		{
			throw new ModuleException("Could not create an instance of '" + moduleClass + "': " + e.getMessage(),
					"INCRE running " + moduleName);
		}
	}
}
