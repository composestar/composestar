package Composestar.Core.Master.Config;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Modules implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4868992763327704934L;

	private Map modules;

	public Modules()
	{
		modules = new HashMap();
	}

	public ModuleSettings getModule(String key)
	{
		return (ModuleSettings) modules.get(key);
	}

	public void addModule(String key, ModuleSettings m)
	{
		modules.put(key, m);
	}
}
