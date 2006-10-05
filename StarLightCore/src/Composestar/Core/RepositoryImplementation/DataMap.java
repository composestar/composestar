package Composestar.Core.RepositoryImplementation;

import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

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

	public Vector m_keys;
	public Vector m_values;

	public DataMap()
	{
		m_keys = new Vector();
		m_values = new Vector();     
	}

	public DataMap(DataMap map)
	{
		m_keys = new Vector(map.m_keys.size());
		m_values = new Vector(map.m_values.size());
		
		for (int i = 0; i < m_keys.size(); i++)
		{
			m_keys.addElement(map.m_keys.elementAt(i));
			m_values.addElement(map.m_values.elementAt(i));
		}
	}

	public void clear()
	{
		m_keys = new Vector();
		m_values = new Vector();
	}

	public boolean containsKey(Object key)
	{
		return m_keys.contains(key);
	}

	public boolean containsValue(Object value)
	{
		return m_values.contains(value);     
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
		 int index = m_keys.indexOf(key);
		 return (index < 0 ? null : m_values.elementAt(index));
	 }

	 public int hashCode()
	 {
		 return m_keys.hashCode();     
	 }

	 public boolean isEmpty()
	 {
		 return m_keys.isEmpty();
	 }

	 public Set keySet()
	 {
		 throw new UnsupportedOperationException();
	 }

	 public Object put(Object key, Object value) 
	 {
		 int index = m_keys.indexOf(key);
		 if (index <0)
		 {
			 m_keys.addElement(key);
			 m_values.addElement(value);
			 return null;
		 }
		 else
		 {
			 Object old = m_values.elementAt(index);
			 m_values.setElementAt(value, index);
			 return old;
		 }
	 }

	 public void putAll(Map map)
	 {
		 throw new UnsupportedOperationException();
	 }

	 public Object remove(Object key)
	 {
		 int index = m_keys.indexOf(key);
		 Object old = m_values.elementAt(index);
		 m_values.removeElementAt(index);
		 m_keys.removeElementAt(index);
		 return old;     
	 }

	 public int size()
	 {
		 return m_keys.size();     
	 }

	 public Collection values()
	 {
		 return m_values;
	 }

	 public void excludeUnreferenced(Class c)
	 {
		 int n = m_values.size();
		 Vector removeKeys = new Vector();
		 for (int i = 0; i < n; i++)
		 {
			 Object key = m_keys.elementAt(i);
			 Object value = m_values.elementAt(i);
			 if (value.getClass().equals(c) && value instanceof RepositoryEntity)
			 {
				 RepositoryEntity re = (RepositoryEntity)value;
				 if (re.getDynObject("REFERENCED") == null)
					 removeKeys.addElement(key);	
			 }
		 }
		 for (Enumeration e = removeKeys.elements(); e.hasMoreElements(); )
		 {
			 Object key = e.nextElement();
			 this.remove(key);
		 }
	 }

	 public String toString()
	 {
		 return (m_keys + " == " + m_values);
	 }

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
		 HashMap result = new HashMap();
		 for (int i = 0; i < m_keys.size(); i++)
		 {
			 result.put(m_keys.elementAt(i),m_values.elementAt(i));
		 }
		 return result;
	 }
}
