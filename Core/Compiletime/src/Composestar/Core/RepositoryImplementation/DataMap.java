package Composestar.Core.RepositoryImplementation;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * The DataMap is in implementation of the java.util.Map interface that uses two
 * public Vectors as storage, one for keys, and one for values.
 * RepositoryEntities that need a map should used the DataMap since it can be
 * serialized by CONE-XML.
 * 
 * @author Tom Staijen
 * @version 0.9.0
 */
public abstract class DataMap implements Map, SerializableRepositoryEntity, Cloneable
{
	protected static Class dataMapClass;
	
	public static boolean setDataMapClass(Class newClass)
	{
		if (DataMap.class.isAssignableFrom(newClass))
		{
			dataMapClass = newClass;
			return true;
		}
		return false;
	}
	
	public static Class getDataMapClass()
	{
		return dataMapClass;
	}

	public static DataMap newDataMapInstance()
	{
		DataMap newMap;
		try
		{
			newMap = (DataMap) dataMapClass.newInstance();
		}
		catch (Exception e)
		{
			throw new RuntimeException("Unable to create DataMap of class " + dataMapClass);
		}
		return newMap;
	}

	public abstract void clear();

	public abstract boolean containsKey(Object key);

	public abstract boolean containsValue(Object value);

	public Set entrySet()
	{
		throw new UnsupportedOperationException();
	}

	public boolean equals(Object arg0)
	{
		throw new UnsupportedOperationException();
	}

	public abstract Object get(Object key);

	public abstract boolean isEmpty();

	public abstract Set keySet();

	public abstract Object put(Object key, Object value);

	public abstract void putAll(Map map);

	public abstract Object remove(Object key);

	public abstract int size();

	public abstract Collection values();

	public abstract void excludeUnreferenced(Class c);

	public abstract HashMap toHashMap();
	
	public Object clone() throws CloneNotSupportedException
	{
		return super.clone();
	}
}
