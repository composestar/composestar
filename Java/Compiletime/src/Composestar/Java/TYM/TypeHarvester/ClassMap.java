package Composestar.Java.TYM.TypeHarvester;

import java.util.HashMap;

public class ClassMap {
	
	private HashMap harvestedClasses = new HashMap();
	private static ClassMap Instance;
	
	public ClassMap() {
		
	}
	
	public void addClass(Class c) {
		String name = c.getName();
		if(!harvestedClasses.containsKey(name)) {
			harvestedClasses.put(name,c);
		}
		else
			return;
			//duplicate
	}
	
	public Class getClass(String name) {
		Class c = (Class)harvestedClasses.get(name);
		return c;
	}
	
	public static ClassMap instance() {
        if( Instance == null ) {
            Instance = new ClassMap();
        }
        return Instance;     
    }
	
	public HashMap map() {
	   return harvestedClasses;
	}
}
