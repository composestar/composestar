package Composestar.Core.RepositoryImplementation;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * The DataMap is in implementation of the java.util.Map interface that uses two 
 * public Vectors as storage, one for keys, and one for values. RepositoryEntities 
 * that need a map should used the DataMap since it can be serialized by CONE-XML.
 * 
 * @author Tom Staijen
 * @version 0.9.0
 */
public class DataMap implements Map, SerializableRepositoryEntity, Cloneable
{
	private static final long serialVersionUID = -6486304623601718657L;

//	public Vector m_keys;
//	public Vector m_values;
	public HashMap map;

	public DataMap()
	{
//		m_keys = new Vector();
//		m_values = new Vector();
		map = new HashMap();
	}

	public DataMap(DataMap dataMap)
	{
//		m_keys = new Vector(map.m_keys.size());
//		m_values = new Vector(map.m_values.size());
		map = new HashMap(dataMap.map.size());
		
		Iterator keyIterator = dataMap.keySet().iterator();
		while(keyIterator.hasNext())
		{
			String key = (String) keyIterator.next();
			map.put(key, dataMap.map.get(key));
		}
	}

	public void clear()
	{
		map = new HashMap();
	}

	public boolean containsKey(Object key)
	{
		return map.containsKey(key);
	}

	public boolean containsValue(Object value)
	{
		return map.containsValue(value);     
	}

	public Set entrySet()
	{
		throw new UnsupportedOperationException();
	}

	 public boolean equals(Object arg0)
	 {
		throw new UnsupportedOperationException();
	 }

	 public Object get(Object key) 
	 {
		 return map.get(key);
	 }

	 public int hashCode()
	 {
		 return map.hashCode();     
	 }

	 public boolean isEmpty()
	 {
		 return map.isEmpty();
	 }

	 public Set keySet()
	 {
		 return map.keySet();
	 }

	 public Object put(Object key, Object value) 
	 {
		 return map.put(key, value);
	 }

	 public void putAll(Map map)
	 {
		 this.map.putAll(map);
	 }

	 public Object remove(Object key)
	 {
		 return map.remove(key);     
	 }

	 public int size()
	 {
		 return map.size();
	 }

	 public Collection values()
	 {
		 return map.values();
	 }

	 public void excludeUnreferenced(Class c)
	 {
//		 int n = m_values.size();
//		 Vector removeKeys = new Vector();
//		 for (int i = 0; i < n; i++)
//		 {
//			 Object key = m_keys.elementAt(i);
//			 Object value = m_values.elementAt(i);
//			 if (value.getClass().equals(c) && value instanceof RepositoryEntity)
//			 {
//				 RepositoryEntity re = (RepositoryEntity)value;
//				 if (re.getDynObject("REFERENCED") == null)
//					 removeKeys.addElement(key);	
//			 }
//		 }
//		 for (Enumeration e = removeKeys.elements(); e.hasMoreElements(); )
//		 {
//			 Object key = e.nextElement();
//			 this.remove(key);
//		 }
	 }

//	 public String toString()
//	 {
//		 return (m_keys + " == " + m_values);
//	 }

	 public Object clone() throws CloneNotSupportedException
	 {
	/*	 
		 DataMap map = (DataMap)super.clone();

		 // At this point, the newObject shares all data with the object
		 // running clone. If you want newObject to have its own
		 // copy of data, you must clone this data yourself.

		 map.m_keys = new Vector();
		 map.m_values = new Vector();
		 return map;
	*/
		 // the code above is wrong, so throw an exception instead
		 throw new UnsupportedOperationException();
	 }

	 /**
	  * For converting
	  */
	 public HashMap getHashMap()
	 {
		 return map;
	 }
}
