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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import Composestar.Utils.Logging.ILogger;
import Composestar.Utils.Logging.SafeLogger;

//
// !! Compose* Runtime Warning !!
//
// This class is referenced in the Compose* Runtime for .NET 1.1
// Do not use Java features added after Java 2.0
//

/**
 * The Repository part of the Compose* project. It supports reading and writing
 * objects. It allows for basic store and restore operations
 * 
 * @author Pascal Dürr
 * @version $Id$
 */
public class DataStore implements Serializable, Cloneable
{
	/**
	 * If set to true instance() will always return null in order to prevent
	 * automatically adding of objects to the DataStore during deserializing
	 */
	private static boolean isDeserializing = false;

	private static final long serialVersionUID = -1235392544932797436L;

	private static DataStore instance = null;

	private static final boolean DEBUG = false;

	private transient ILogger logger;

	public DataMap map;

	/**
	 * Creates the datastore.
	 */
	public DataStore()
	{
		if (DEBUG)
		{
			logger = SafeLogger.getILogger("DataStore");
			logger.info("Creating Datastore...");
		}
		map = DataMap.newDataMapInstance();
	}

	/**
	 * Returns what should be the sole instance of this class.
	 * 
	 * @deprecated Use Composestar.Core.Resources.CommonResources.repository()
	 * @see Composestar.Core.Resources.CommonResources#repository()
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
		return map.keySet().iterator();
	}

	/**
	 * Returns a collection of all objects in this datastore.
	 */
	public Collection getObjects()
	{
		return map.values();
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
					logger.error("Overwriting existing object '" + old + "' with id '" + id + "' with new object '"
							+ obj + "'...");
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
			id = obj.getClass() + "@" + System.identityHashCode(obj);
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
			logger.info("Removing object from datastore with id '" + id + "'.");
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
		map.excludeUnreferenced(c);
	}
}
