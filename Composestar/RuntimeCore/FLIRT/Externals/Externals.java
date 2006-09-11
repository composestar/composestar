package Composestar.RuntimeCore.FLIRT.Externals;

import java.util.HashMap;

import Composestar.RuntimeCore.FLIRT.Filters.Concern;
import Composestar.RuntimeCore.FLIRT.Filters.FilterModule;

public class Externals {
	protected static HashMap map = new HashMap();
	
	public static Object getExternal(String name, Concern concern){
		HashMap externals = getExternals(concern);
		synchronized(externals){
			return externals.get(name);	
		}
	}
	
	public static HashMap getExternals(Concern concern){
		return getNewExternals(concern); 
	}
	
	public static HashMap getExternals(FilterModule module){
		return getNewExternals(module);
	}
	
	public static Object getExternal(String name, FilterModule module){
		HashMap externals = getExternals(module);
		synchronized(externals){
			return externals.get(name);	
		}
	}
	
	protected synchronized static HashMap getNewExternals(Object identifier){
		HashMap externals = (HashMap) map.get(identifier);
		if(externals == null){
			//TODO get or create external
			externals = new HashMap();
			map.put(identifier,externals);
		}
		return externals;
	}
}
