package Composestar.RuntimeCore.FLIRT.Internals;

import java.util.HashMap;

import Composestar.RuntimeCore.FLIRT.Filters.FilterModule;

public class Internals {
	protected static HashMap map = new HashMap();
	
	public static Object getInternal(String name, FilterModule filterModule){
		HashMap internals = getInternals(filterModule);
		synchronized(filterModule){
			return internals.get(name);	
		}
	}
	
	public synchronized static HashMap getInternals(FilterModule filterModule){
		HashMap internals = (HashMap) map.get(filterModule);
		if(internals == null){
			//TODO get or create internal
			internals = new HashMap();
			map.put(filterModule,internals);
		}
		return internals;
	}
}
