package Composestar.Core.RepositoryImplementation;

import java.util.Map;
import java.util.Vector;
import java.util.Set;
import java.util.Collection;
import java.lang.CloneNotSupportedException;

/**
 * The DataMap is in implementation of the java.util.Map interface that uses two 
 * public Vectors as storage, one for keys, and one for values. RepositoryEntities 
 * that need a map should used the DataMap since it can be serialized by CONE-XML.
 * 
 * @author Tom Staijen
 * @version 0.9.0
 */
public class DataMap implements Map, SerializableRepositoryEntity, Cloneable {
    /**
	 * 
	 */
	private static final long serialVersionUID = -6486304623601718657L;
	public Vector keys;
    public Vector values;
    
    /**
     * @roseuid 40EBC9CC0082
     */
    public DataMap() {
     	keys = new Vector();
     	values = new Vector();     
    }

	public DataMap(DataMap map)
	{
		keys = new Vector(map.keys.size());
		values = new Vector(map.values.size()); 
		for(int i = 0; i < keys.size(); i++)
		{
			keys.addElement(map.keys.elementAt(i));
			values.addElement(map.values.elementAt(i));
		}
	}
    
    /**
     * @roseuid 40EBC9CC00D0
     */
    public void clear() {
     	keys = new Vector();
     	values = new Vector();     
    }
    
    /**
     * @param arg0
     * @return boolean
     * @roseuid 40EBC9CC00FF
     */
    public boolean containsKey(Object arg0) {
     	return keys.contains(arg0);     
    }
    
    /**
     * @param arg0
     * @return boolean
     * @roseuid 40EBC9CC016C
     */
    public boolean containsValue(Object arg0) {
     	return values.contains(arg0);     
    }
    
    /**
     * @return java.util.Set
     * @roseuid 40EBC9CC01E9
     */
    public Set entrySet() {
     return null;
    }
    
    /**
     * @param arg0
     * @return boolean
     * @roseuid 40EBC9CC0218
     */
    public boolean equals(Object arg0) {
     	return false;
    }
    
    /**
     * @param arg0
     * @return java.lang.Object
     * @roseuid 40EBC9CC0285
     */
	public Object get(Object arg0) 
	{
		int index = keys.indexOf(arg0);
		if(index < 0)
		{
			return null; 
		}
		else
		{
			return values.elementAt(index);
		}   
	}
    
    /**
     * @return int
     * @roseuid 40EBC9CC0302
     */
    public int hashCode() {
     	return keys.hashCode();     
    }
    
    /**
     * @return boolean
     * @roseuid 40EBC9CC0322
     */
    public boolean isEmpty() {
     	return (keys.isEmpty());
    }
    
    /**
     * @return java.util.Set
     * @roseuid 40EBC9CC0351
     */
    public Set keySet() {
     return null;
    }
    
    /**
     * @param arg0
     * @param arg1
     * @return java.lang.Object
     * @roseuid 40EBC9CC037F
     */
	public Object put(Object arg0, Object arg1) 
	{
		int index = keys.indexOf(arg0);
		if(index <0)
		{
			keys.addElement(arg0);
			values.addElement(arg1);
			return null;
		}
		else
		{
			Object old = values.elementAt(index);
			values.setElementAt(arg1, index);
			return old;
		}
	}
    
    /**
     * @param arg0
     * @roseuid 40EBC9CD0053
     */
    public void putAll(Map arg0) {
     
    }
    
    /**
     * @param arg0
     * @return java.lang.Object
     * @roseuid 40EBC9CD00D0
     */
    public Object remove(Object arg0) {
     	int index = keys.indexOf(arg0);
     	Object old = values.elementAt(index);
     	values.removeElementAt(index);
     	keys.removeElementAt(index);
     	return old;     
    }
    
    /**
     * @return int
     * @roseuid 40EBC9CD014D
     */
    public int size() {
     	return keys.size();     
    }
    
    /**
     * @return java.util.Collection
     * @roseuid 40EBC9CD017C
     */
    public Collection values() {
		return values;
    }
    
    public void excludeUnreferenced(Class c)
    {
    	int n = this.values.size();
    	Vector removeKeys = new Vector();
    	for( int i = 0; i < n; i++ )
    	{
    		Object key = this.keys.elementAt(i);
    		Object value = this.values.elementAt(i);
    		if( value.getClass().equals(c) && value instanceof RepositoryEntity )
    		{
    			if( ((RepositoryEntity) value).getDynObject("REFERENCED") == null )
    				removeKeys.addElement(key);	
    		}
    	}
    	for( java.util.Enumeration e = removeKeys.elements(); e.hasMoreElements(); )
    	{
    		Object key = e.nextElement();
    		this.remove(key);
    	}
    }

    public String toString()
    {
        return(keys+" == "+values);
    }

	public Object clone() throws CloneNotSupportedException
	{
		DataMap map;
		map = (DataMap) super.clone();
		
		// At this point, the newObject shares all data with the object
		// running clone. If you want newObject to have its own
		// copy of data, you must clone this data yourself.

		map.keys = new Vector();
		map.values = new Vector();
		return map;
	}

	/* For converting
	 */
	public java.util.HashMap getHashMap()
	{
		java.util.HashMap result = new java.util.HashMap();
		for(int i = 0; i < keys.size(); i++)
		{
			result.put(keys.elementAt(i),values.elementAt(i));
		}
		return result;
	}
}
