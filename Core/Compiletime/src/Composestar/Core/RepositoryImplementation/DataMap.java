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

	public int size()
	{
		return m_keys.size();
	}

	public boolean isEmpty()
	{
		return m_keys.isEmpty();
	}

	public boolean containsKey(Object key)
	{
		return m_keys.contains(key);
	}

	public boolean containsValue(Object value)
	{
		return m_values.contains(value);     
	}

	public Object get(Object key) 
	{
		int index = m_keys.indexOf(key);
		return index < 0 ? null : m_values.elementAt(index);
	}

	public Object put(Object key, Object value) 
	{
		int index = m_keys.indexOf(key);
		if (index < 0)
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

	public Object remove(Object key)
	{
		int index = m_keys.indexOf(key);
		Object old = m_values.elementAt(index);
		m_values.removeElementAt(index);
		m_keys.removeElementAt(index);
		return old;
	}

	public void putAll(Map map)
	{
		throw new UnsupportedOperationException();
	}

	public void clear()
	{
		m_keys = new Vector();
		m_values = new Vector();
	}

	public Set keySet()
	{
		throw new UnsupportedOperationException();
	}

	public Set entrySet()
	{
		throw new UnsupportedOperationException();
	}

	public Collection values()
	{
		return m_values;
	}

	public boolean equals(Object o)
	{
		throw new UnsupportedOperationException();
	}

	public int hashCode()
	{
		return m_keys.hashCode();
	}

	public String toString()
	{
		return m_keys + " == " + m_values;
	}

	public Object clone() throws CloneNotSupportedException
	{
		throw new CloneNotSupportedException();
	}

	public void excludeUnreferenced(Class c)
	{
		Vector removeKeys = new Vector();

		int n = m_values.size();
		for (int i = 0; i < n; i++)
		{
			Object key = m_keys.elementAt(i);
			Object value = m_values.elementAt(i);
			if (value.getClass().equals(c) && value instanceof RepositoryEntity)
			{
				RepositoryEntity re = (RepositoryEntity)value;
				if (re.getDynObject("REFERENCED") == null)
				{
					removeKeys.addElement(key);
				}
			}
		}
		for (Enumeration e = removeKeys.elements(); e.hasMoreElements(); )
		{
			Object key = e.nextElement();
			this.remove(key);
		}
	}

	public HashMap toHashMap()
	{
		HashMap result = new HashMap();
		for (int i = 0; i < m_keys.size(); i++)
		{
			result.put(m_keys.elementAt(i),m_values.elementAt(i));
		}
		return result;
	}
}
