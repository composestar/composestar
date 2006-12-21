package Composestar.Core.INCRE;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import Composestar.Core.Exception.ModuleException;
import Composestar.Core.Master.CTCommonModule;
import Composestar.Core.Master.CommonResources;
import Composestar.Utils.Debug;

public class Module
{
	private String name = "";

	private String fulltype;

	private String input;

	private boolean enabled;

	private boolean incremental;

	private String summary = "";

	/**
	 * A map containing dependencies for the module.
	 */
	private Map deps = new LinkedHashMap();

	/**
	 * A map containing objects to be compared by MyComparator.
	 */
	private Map comparableObjects = new HashMap();

	public Module(String inName)
	{
		name = inName;
	}

	public void setFullType(String ftype)
	{
		fulltype = ftype;
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

	public void addComparableObject(String key, Object obj)
	{
		ArrayList list;
		if (comparableObjects.containsKey(key))
		{
			list = (ArrayList) comparableObjects.get(key);
		}
		else
		{
			list = new ArrayList();
		}

		list.add(obj);
		comparableObjects.put(key, list);
	}

	public void removeComparableObject(String key, Object obj)
	{
		ArrayList list;
		if (comparableObjects.containsKey(key))
		{
			list = (ArrayList) comparableObjects.get(key);
		}
		else
		{
			list = new ArrayList();
		}

		list.remove(obj);
		comparableObjects.put(key, list);
	}

	public void addComparableObjects(HashMap map)
	{
		Iterator entries = map.entrySet().iterator();
		while (entries.hasNext())
		{
			Entry entry = (Entry) entries.next();
			Iterator objItr = ((HashMap) entry.getValue()).values().iterator();
			while (objItr.hasNext())
			{
				addComparableObject((String) entry.getKey(), objItr.next());
			}
		}
	}

	public void removeComparableObjects(HashMap map)
	{
		Iterator entries = map.entrySet().iterator();
		while (entries.hasNext())
		{
			Entry entry = (Entry) entries.next();
			Iterator objItr = ((HashMap) entry.getValue()).values().iterator();
			while (objItr.hasNext())
			{
				removeComparableObject((String) entry.getKey(), objItr.next());
			}
		}
	}

	public boolean hasComparableObjects(String key)
	{
		return comparableObjects.containsKey(key);
	}

	public ArrayList getComparableObjects(String key)
	{
		return (ArrayList) comparableObjects.get(key);
	}

	public Iterator getDeps()
	{
		return deps.values().iterator();
	}

	public String getName()
	{
		return name;
	}

	public String getInput()
	{
		return input;
	}

	public boolean isIncremental()
	{
		return this.incremental;
	}

	public void setIncremental(boolean b)
	{
		this.incremental = b;
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
		if (enabled)
		{
			// module is enabled for the phase so continue

			if (summary.length() != 0)
			{
				Debug.out(Debug.MODE_CRUCIAL, this.name, summary);
			}

			try
			{
				Class moduleClass = Class.forName(fulltype);
				CTCommonModule module = (CTCommonModule) moduleClass.newInstance();

				INCRETimer timer = INCRE.instance().getReporter().openProcess(name, name, INCRETimer.TYPE_ALL);
				module.run(resources);
				INCRE.instance().addModuleByName(this.name, module);
				timer.stop();
			}
			/*
			 * // these should not be catched; can cause more problems if you
			 * do. catch (StackOverflowError e) { throw new ModuleException("I
			 * need more stack!", "INCRE running " + this.name); } catch
			 * (OutOfMemoryError e) { throw new ModuleException("I am using too
			 * much memory!", "INCRE running " + this.name); }
			 */
			catch (ClassNotFoundException e)
			{
				throw new ModuleException("Cannot find class '" + fulltype + "'", "INCRE running " + name);
			}
			catch (InstantiationException e)
			{
				throw new ModuleException(
						"Could not create an instance of class '" + fulltype + "': " + e.getMessage(), "INCRE running "
								+ name);
			}
			catch (IllegalAccessException e)
			{
				throw new ModuleException(
						"Could not create an instance of class '" + fulltype + "': " + e.getMessage(), "INCRE running "
								+ name);
			}
			/*
			 * // the only other exception thrown is ModuleException, // which
			 * we can just let fall through catch (Exception e) {
			 * Debug.out(Debug.MODE_DEBUG, "Master", Debug.stackTrace(e)); throw
			 * new ModuleException(e.toString(),"INCRE running " + name); }
			 */
		}
	}
}
