package Composestar.Core.RepositoryImplementation;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
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
public class DataMapImpl extends DataMap
{
	private static final long serialVersionUID = -6486304623601718657L;

	// public Vector m_keys;
	// public Vector m_values;
	public HashMap map;

	public DataMapImpl()
	{
		// m_keys = new Vector();
		// m_values = new Vector();
		map = new HashMap();
	}

	public DataMapImpl(DataMap dataMap)
	{
		map = new HashMap(dataMap.size());

		Iterator keyIterator = dataMap.keySet().iterator();
		while (keyIterator.hasNext())
		{
			String key = (String) keyIterator.next();
			map.put(key, dataMap.get(key));
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
		// CPSLogger logger = CPSLogger.getCPSLogger("datamap");
		Iterator it = map.entrySet().iterator();
		while (it.hasNext())
		{
			Entry entry = (Entry) it.next();
			if (entry.getValue().getClass().equals(c) && entry.getValue() instanceof RepositoryEntity)
			{
				RepositoryEntity re = (RepositoryEntity) entry.getValue();
				if (re.getDynObject("REFERENCED") == null)
				{
					// if (logger.isDebugEnabled())
					// {
					// logger.debug("Removing " + entry.getValue().getClass() +
					// " instance with key " + entry.getKey());
					// }
					it.remove();
				}
			}
		}
	}

	// public String toString()
	// {
	// return (m_keys + " == " + m_values);
	// }

	public Object clone() throws CloneNotSupportedException
	{
		/*
		 * DataMap map = (DataMap)super.clone(); // At this point, the newObject
		 * shares all data with the object // running clone. If you want
		 * newObject to have its own // copy of data, you must clone this data
		 * yourself. map.m_keys = new Vector(); map.m_values = new Vector();
		 * return map;
		 */
		DataMapImpl dmap = (DataMapImpl) super.clone();

		dmap.map = new HashMap(map.size());

		Iterator keyIterator = keySet().iterator();
		while (keyIterator.hasNext())
		{
			String key = (String) keyIterator.next();
			dmap.put(key, map.get(key));
		}

		return dmap;
	}

	/**
	 * For converting
	 */
	public HashMap toHashMap()
	{
		return map;
	}
}
