package Composestar.Core.RepositoryImplementation;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

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

	public HashMap<Object, Object> map;

	public DataMapImpl()
	{
		map = new HashMap<Object, Object>();
	}

	public DataMapImpl(DataMap dataMap)
	{
		map = new HashMap<Object, Object>(dataMap.size());
		Iterator<Object> keyIterator = dataMap.keySet().iterator();
		while (keyIterator.hasNext())
		{
			Object key = keyIterator.next();
			map.put(key, dataMap.get(key));
		}
	}

	public void clear()
	{
		map = new HashMap<Object, Object>();
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

	public Collection<Object> values()
	{
		return map.values();
	}

	public void excludeUnreferenced(Class c)
	{
		// CPSLogger logger = CPSLogger.getCPSLogger("datamap");
		Iterator<Entry<Object, Object>> it = map.entrySet().iterator();
		while (it.hasNext())
		{
			Entry<Object, Object> entry = it.next();
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

		dmap.map = new HashMap<Object, Object>(map.size());

		Iterator<Object> keyIterator = keySet().iterator();
		while (keyIterator.hasNext())
		{
			Object key = keyIterator.next();
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

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		if (!DataMap.rtSerialization)
		{
			in.defaultReadObject();
			return;
		}
		else
		{
			map = (HashMap<Object, Object>) in.readObject();
		}
	}

	private void writeObject(ObjectOutputStream out) throws IOException
	{
		if (!DataMap.rtSerialization)
		{
			out.defaultWriteObject();
			return;
		}
		else
		{
			HashMap<Object, Object> rtmap = new HashMap<Object, Object>(map);
			Iterator<Entry<Object, Object>> it = rtmap.entrySet().iterator();
			while (it.hasNext())
			{
				Entry<Object, Object> entry = it.next();
				Object obj = entry.getValue();
				if (obj instanceof String)
				{
					continue;
				}
				if (obj instanceof Integer)
				{
					continue;
				}
				if (obj instanceof Vector)
				{
					continue;
				}
				if (obj instanceof SerializableRepositoryEntity)
				{
					continue;
				}
				// System.err.println("Not serializing object of type: " +
				// obj.getClass().getName());
				// System.err.println(" " + entry.getKey() + " = " +
				// obj.toString());
				it.remove();
			}
			out.writeObject(rtmap);
		}
	}
}
