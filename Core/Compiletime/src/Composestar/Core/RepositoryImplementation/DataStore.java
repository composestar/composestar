/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */
package Composestar.Core.RepositoryImplementation;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import Composestar.Utils.Debug;

/**
 * The Repository part of the Compose* project. It supports reading and writing
 * objects. It allows for basic store and restore operations
 * 
 * @author Pascal Durr
 * @version $Id$
 */
public class DataStore implements Serializable, Cloneable
{
	private static DataStore instance = null;

	/**
	 * If set to true instance() will always return null in order to prevent
	 * automatically adding of objects to the DataStore during deserializing
	 */
	private static boolean isDeserializing = false;

	private static final long serialVersionUID = -1235392544932797436L;

	private static final boolean DEBUG = false;

	private String filename = "ComposeStarDataStore.ser";

	public DataMap map;

	/**
	 * Creates the datastore.
	 */
	public DataStore()
	{
		if (DEBUG)
		{
			Debug.out(Debug.MODE_INFORMATION, "DataStore", "Creating Datastore...");
		}
		map = new DataMap();
	}

	/**
	 * Returns what should be the sole instance of this class.
	 */
	public static DataStore instance()
	{
		if (isDeserializing)
		{
			return null;
		}
		if (instance == null)
		{
			instance = new DataStore();
		}
		return instance;
	}

	/**
	 * Needed for the Runtime: called by DotNETRepositoryDeserializer.
	 */
	public static void setInstance(DataStore ds)
	{
		instance = ds;
	}

	public static void setIsDeserializing(boolean inval)
	{
		isDeserializing = inval;
	}

	/**
	 * Returns the number of objects in the datastore.
	 */
	public int size()
	{
		return map.size();
	}

	/**
	 * Returns an iterator over all keys in this datastore.
	 */
	public Iterator keys()
	{
		return (map).m_keys.iterator();
	}

	/**
	 * Returns an iterator over all objects in this datastore.
	 */
	public Iterator getIterator()
	{
		return map.values().iterator();
	}

	/**
	 * Adds an object to the Data Store, given the id and the object.
	 * 
	 * @param id String The ID of the Object, needed for further reference.
	 * @param obj Object The Object to be stored.
	 * @return String The id of the stored object.
	 */
	public String addObject(String id, Object obj)
	{
		Object old = map.get(id);

		if (old != null)
		{
			// Something with an already present id is added :(
			if (old instanceof Composestar.Core.CpsProgramRepository.CpsConcern.CpsConcern)
			{
				if (obj instanceof Composestar.Core.CpsProgramRepository.PrimitiveConcern)
				{
					Composestar.Core.CpsProgramRepository.CpsConcern.CpsConcern cpsconcern = (Composestar.Core.CpsProgramRepository.CpsConcern.CpsConcern) old;
					cpsconcern.addDynObject("IMPLEMENTATION", obj);
					map.put(id, cpsconcern);
				}
			}
			else if (old instanceof Composestar.Core.CpsProgramRepository.PrimitiveConcern)
			{
				if (obj instanceof Composestar.Core.CpsProgramRepository.CpsConcern.CpsConcern)
				{
					Composestar.Core.CpsProgramRepository.CpsConcern.CpsConcern cpsconcern = (Composestar.Core.CpsProgramRepository.CpsConcern.CpsConcern) obj;
					cpsconcern.addDynObject("IMPLEMENTATION", old);
					map.put(id, cpsconcern);
				}
			}
			else
			{
				// OOPS this is very bad!!!
				if (DEBUG)
				{
					Debug.out(Debug.MODE_WARNING, "DataStore", "Overwriting existing object '" + old + "' with id '"
							+ id + "' with new object '" + obj + "'...");
				}
				map.put(id, obj);
			}
		}
		else
		{
			map.put(id, obj);
		}
		return id;
	}

	/**
	 * Adds an object to the Data Store, given the object.
	 * 
	 * @param obj Object The Object to be stored.
	 * @return String The id of the stored object.
	 */
	public String addObject(Object obj)
	{
		String id;
		if (obj instanceof RepositoryEntity)
		{
			id = ((RepositoryEntity) obj).getUniqueID();
		}
		else
		{
			// FIXME: obj.hashCode() is not guaranteed to produce unique values
			// for distinct instances of some class, so this can lead to subtle
			// bugs.
			id = obj.getClass() + "_" + obj.hashCode();
		}
		return addObject(id, obj);
	}

	/**
	 * Removes an object given the ID.
	 * 
	 * @param id String The id of the object to be removed.
	 * @return Object The object that was removed.
	 */
	public Object removeObject(String id)
	{
		if (DEBUG)
		{
			Debug.out(Debug.MODE_INFORMATION, "DataStore", "Removing object from datastore with id '" + id + "'.");
		}
		return map.remove(id);
	}

	/**
	 * Checks if the given id is contained in the datastore.
	 * 
	 * @param id String The id to check for.
	 * @return boolean The result true if it is in the datastore and false if
	 *         not.
	 */
	public boolean containsObject(String id)
	{
		return map.containsKey(id);
	}

	/**
	 * Returns the object given the ID.
	 * 
	 * @param id String The id of the object wanted.
	 * @return Object The corresponding object, or null if not present.
	 */
	public Object getObjectByID(String id)
	{
		return map.get(id);
	}

	public Iterator getAllInstancesOf(Class c)
	{
		return getListOfAllInstances(c).iterator();
	}

	public List getListOfAllInstances(Class c)
	{
		Iterator it = map.values().iterator();
		List list = new ArrayList();
		while (it.hasNext())
		{
			Object obj = it.next();
			if (c.isInstance(obj))
			{
				list.add(obj);
			}
		}
		return list;
	}

	public void excludeUnreferenced(Class c)
	{
		(map).excludeUnreferenced(c);
		/*
		 * List removeKeys = new ArrayList(); Iterator it =
		 * map.entrySet().iterator(); while (it.hasNext()) { Map.Entry entry =
		 * (Map.Entry)it.next(); Object key = entry.getKey(); Object value =
		 * entry.getValue(); if (value.getClass().equals(c) && value instanceof
		 * RepositoryEntity) { RepositoryEntity re = (RepositoryEntity)value; if
		 * (re.getDynObject("REFERENCED") == null) removeKeys.add(key); } }
		 * Iterator rkIt = removeKeys.iterator(); while (rkIt.hasNext()) {
		 * Object key = rkIt.next(); map.remove(key); }
		 */
	}

	/**
	 * Reads a serialized object from the given file.
	 * 
	 * @param filename String The name of the file to read from.
	 * @return Object The object that was read, or null if unsuccesfull.
	 */
	public Object readObject(String filename)
	{
		try
		{
			if (DEBUG)
			{
				Debug.out(Debug.MODE_INFORMATION, "DataStore", "Reading object from file '" + filename + "'... ");
			}
			FileInputStream fis;
			if (filename == null)
			{
				fis = new FileInputStream(this.filename);
			}
			else
			{
				fis = new FileInputStream(filename);
			}
			ObjectInputStream ois = new ObjectInputStream(fis);
			Object obj = ois.readObject();
			ois.close();
			if (DEBUG)
			{
				Debug.out(Debug.MODE_INFORMATION, "DataStore", "Done reading object '" + obj + "'.");
			}
			return obj;
		}
		catch (Exception e)
		{
			if (DEBUG)
			{
				Debug.out(Debug.MODE_WARNING, "DataStore", "Failed reading object from file '" + filename + "'.");
				e.printStackTrace();
			}
			return null;
		}
	}

	/**
	 * Write an object in a serialezed form to the given file.
	 * 
	 * @param filename String The name of the file to write to.
	 * @param obj Object The object to be written.
	 * @return boolean The result, true if succesfull, false if not.
	 */
	public boolean writeObject(String filename, Object obj)
	{
		try
		{
			if (DEBUG)
			{
				Debug.out(Debug.MODE_INFORMATION, "DataStore", "Writing object '" + obj + "' to file '" + filename
						+ "'...");
			}
			FileOutputStream fos;
			if (filename == null)
			{
				fos = new FileOutputStream(this.filename);
			}
			else
			{
				fos = new FileOutputStream(filename);
			}
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(obj);
			oos.flush();
			oos.close();
			if (DEBUG)
			{
				Debug.out(Debug.MODE_INFORMATION, "DataStore", "Done writing object '" + obj + "'.");
			}
			return true;
		}
		catch (Exception e)
		{
			if (DEBUG)
			{
				Debug.out(Debug.MODE_WARNING, "DataStore", "Failed writing object '" + obj + "' to file '" + filename
						+ "'.");
				e.printStackTrace();
			}
			return false;
		}
	}
}
