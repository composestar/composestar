package Composestar.Core.Master.Config;

import java.io.Serializable;
import java.util.HashMap;

public class ModuleSettings implements Serializable{

	private HashMap modules;
	
	public ModuleSettings() {
		modules = new HashMap();
	}
	
	public Module getModule(String key) {
		if(modules.containsKey(key))
			return (Module)modules.get(key);
		return null;
	}
	
	public void addModule(String key, Module m) {
		modules.put(key,m);
	}
}