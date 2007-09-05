package Composestar.Core.RepositoryImplementation;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

//
// !! Compose* Runtime Warning !!
//
// This class is referenced in the Compose* Runtime for .NET 1.1
// Do not use Java features added after Java 2.0
//

/**
 * The DataMap is in implementation of the java.util.Map interface that uses two
 * public Vectors as storage, one for keys, and one for values.
 * RepositoryEntities that need a map should used the DataMap since it can be
 * serialized by CONE-XML. Only serializable objects may be added to this map
 * because the entries will mostlikely be serialized. Only data that is needed
 * at runtime should be added to a DataMap. To store compiletime only data used
 * the CommonResources.
 * 
 * @author Tom Staijen
 * @version 0.9.0
 */
public class LegacyDataMap extends DataMap
{
	private static final long serialVersionUID = -6486304623601718657L;

	public Vector m_keys;

	public Vector m_values;

	public LegacyDataMap()
	{
		m_keys = new Vector();
		m_values = new Vector();
	}

	public LegacyDataMap(DataMap dataMap)
	{
		m_keys = new Vector(dataMap.size());
		m_values = new Vector(dataMap.size());

		Iterator keyIterator = dataMap.keySet().iterator();
		while (keyIterator.hasNext())
		{
			Object key = keyIterator.next();
			put(key, dataMap.get(key));
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
		// At runtime elements are added that are not serializable (and don't
		// have to be)
		/*
		 * if ((key != null) && !(value instanceof Serializable)) { throw new
		 * UnsupportedOperationException( "Value must implement Serializable or
		 * use CommonResources otherwise"); } if ((key != null) && !(key
		 * instanceof Serializable)) { throw new UnsupportedOperationException(
		 * "Value must implement Serializable or use CommonResources
		 * otherwise"); }
		 */

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
		if (index == -1) return null;
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
		return new HashSet(m_keys);
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
		// shallow copy
		DataMap dmap = new LegacyDataMap(this);

		return dmap;
	}

	public void excludeUnreferenced(Class c)
	{
		Vector removeKeys = new Vector();

		int n = m_values.size();
		for (int i = 0; i < n; i++)
		{
			Object key = m_keys.elementAt(i);
			Object value = m_values.elementAt(i);
			if (value.getClass().equals(c) && (value instanceof RepositoryEntity))
			{
				RepositoryEntity re = (RepositoryEntity) value;
				if (re.getDynObject("REFERENCED") == null)
				{
					removeKeys.addElement(key);
				}
			}
		}
		for (Enumeration e = removeKeys.elements(); e.hasMoreElements();)
		{
			Object key = e.nextElement();
			remove(key);
		}
	}

	public HashMap toHashMap()
	{
		HashMap result = new HashMap();
		for (int i = 0; i < m_keys.size(); i++)
		{
			result.put(m_keys.elementAt(i), m_values.elementAt(i));
		}
		return result;
	}

	/**
	 * Custom deserialization of this object
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		m_keys = (Vector) in.readObject();
		m_values = (Vector) in.readObject();
	}

	/**
	 * Custom serialization of this object. Only store serializable objects.
	 */
	private void writeObject(ObjectOutputStream out) throws IOException
	{
		Vector serKeys = new Vector();
		Vector serValues = new Vector();
		for (int i = 0; i < m_values.size(); i++)
		{
			Object o = m_values.get(i);
			if (o instanceof Serializable)
			{
				serKeys.add(m_keys.get(i));
				serValues.add(o);
			}
		}
		out.writeObject(serKeys);
		out.writeObject(serValues);
	}
}
