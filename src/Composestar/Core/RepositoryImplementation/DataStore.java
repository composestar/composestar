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
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;

import Composestar.Utils.Debug;

/**
 *   The Repository part of the Compose* project.
 *   It supports reading and writing objects.
 *   It allows for basic store and restore operations
 *   
 *   @author Pascal DŸrr
 *   @version $Id$
 */
public class DataStore implements Serializable, Cloneable
{
	private static DataStore instance = null;
	
	/**
	 * @modelguid {89C111E3-09B5-49FA-8CEA-FA0A9CE16692}
	 */
	public static final long serialVersionUID = -1235392544932797436L;

	/**
	 * @modelguid {8A9901E3-C698-49E4-8F98-71D347F0AE9E}
	 */
	private final static boolean DEBUG = false;

	/**
	 * @modelguid {861306A9-BFA5-4625-9C29-0C9F6C32D49D}
	 */
	private String filename = "ComposeStarDataStore.ser";

	public DataMap map;

	/**
	 * Creates a new datastore.
	 *
	 * @modelguid {36D267A8-90EA-4A0C-AF1A-E347061ED447}
	 * @roseuid 401FAA620201
	 */
	public DataStore() {
		if (DEBUG) {
			Debug.out(Debug.MODE_INFORMATION,"DataStore","Creating Datastore...");
		}
		map = new DataMap();
	}

	/**
	 * Sets the filename for serialization.
	 *
	 * @param filename String  The name of the file.
	 * @modelguid {E22AD171-4BFC-4D70-8253-3E848715CDB1}
	 * @roseuid 401FAA620202
	 */
	public void setFilename(String filename) {
		this.filename = filename;
	}

	/**
	 * Gets the filename for serialization.
	 *
	 * @return String  The name of the file.
	 *
	 * @modelguid {3BE17E02-BC58-42AA-82EA-947A85BFDD68}
	 * @roseuid 401FAA62020B
	 */
	public String getFilename() {
		return (this.filename);
	}


	/**
	 * Reads a serialized object from the given file.
	 *
	 * @param filename String The name of the file to read from.
	 * @return Object  The object that was read, or null if unsuccesfull.
	 *
	 * @modelguid {95DFF2E0-4F65-4F5C-B557-1C02B08CDA8E}
	 * @roseuid 401FAA62020C
	 */
	public Object readObject(String filename) {
		Object obj;
		try {
			if (DEBUG) Debug.out(Debug.MODE_INFORMATION,"DataStore","Reading object from file '" + filename + "'... ");
			FileInputStream fis;
			if (filename == null) {
				fis = new FileInputStream(this.filename);
			} else {
				fis = new FileInputStream(filename);
			}
			ObjectInputStream ois = new ObjectInputStream(fis);
			obj = ois.readObject();
			ois.close();
			if (DEBUG) Debug.out(Debug.MODE_INFORMATION,"DataStore","Done reading object '" + obj + "'.");
			return (obj);
		}
		catch (Exception e) {
			if (DEBUG) {
				Debug.out(Debug.MODE_WARNING,"DataStore","Failed reading object from file '" + filename + "'.");
				e.printStackTrace();
			}
			return (null);
		}
	}

	/**
	 * Write an object in a serialezed form to the given file.
	 *
	 * @param filename String The name of the file to write to.
	 * @param obj      Object The object to be written.
	 * @return boolean  The result, true if succesfull, false if not.
	 *
	 * @modelguid {4A1E184B-4716-4058-A91B-308BCBAEB7B2}
	 * @roseuid 401FAA620216
	 */
	public boolean writeObject(String filename, Object obj) {
		try {
			if (DEBUG) Debug.out(Debug.MODE_INFORMATION,"DataStore","Writing object '" + obj + "' to file '" + filename + "'...");
			FileOutputStream fos;
			if (filename == null) {
				fos = new FileOutputStream(this.filename);
			} else {
				fos = new FileOutputStream(filename);
			}
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(obj);
			oos.flush();
			oos.close();
			if (DEBUG) Debug.out(Debug.MODE_INFORMATION,"DataStore","Done writing object '" + obj + "'.");
			return (true);
		}
		catch (Exception e) {
			if (DEBUG) {
				Debug.out(Debug.MODE_WARNING,"DataStore","Failed writing object '" + obj + "' to file '" + filename + "'.");
				e.printStackTrace();
			}
			return (false);
		}
	}

	/**
	 * Returns an array of all objects in this Compose* Data Store.
	 *
	 * @return Object[]    The objects availeble in this Data Store.
	 *
	 * @modelguid {F51A7B2C-ABEC-49D3-A103-F658C406F3DE}
	 * @roseuid 401FAA620220
	 */
	public Object[] getAllObjects() {
		if (DEBUG) Debug.out(Debug.MODE_INFORMATION,"DataStore","Returning all datastore objects...");
		return (map.values().toArray());
	}

	/**
	 * Returns an iterator that contains links to all the objects.
	 *
	 * @return Enumerator  The enumerator.
	 *
	 * @modelguid {7CB26ED6-5D2C-4265-AF69-6E83FEBE2D85}
	 * @roseuid 401FAA620221
	 */
	public Iterator getIterator() {
		if (DEBUG) Debug.out(Debug.MODE_INFORMATION,"DataStore","Returning datastore iterator...");
		return (map.values().iterator());
	}

	/**
	 * Adds an object to the Data Store, given the id and the object.
	 *
	 * @param id  String The ID of the Object, needed for further reference.
	 * @param obj Object The Object to be stored.
	 * @return String  The id of the stored object.
	 */
	public String addObject(String id, Object obj)
	{
//		if (obj instanceof ConcernReference)
//		{
//			ConcernReference cref = (ConcernReference)obj;
//			Debug.out(Debug.MODE_DEBUG,"DataStore","Adding object '" + id + "' to map with id '" + cref.getName()+"'.");
//		}

		Object old = map.get(id);
		if (old != null)
		{
			if (old instanceof Composestar.Core.CpsProgramRepository.CpsConcern.CpsConcern)
			{
				if (obj instanceof Composestar.Core.CpsProgramRepository.PrimitiveConcern)
				{
					Composestar.Core.CpsProgramRepository.CpsConcern.CpsConcern cpsconcern = (Composestar.Core.CpsProgramRepository.CpsConcern.CpsConcern)old;
					cpsconcern.addDynObject("IMPLEMENTATION",obj);
					map.put(id,cpsconcern);
				}
			}
			else if (old instanceof Composestar.Core.CpsProgramRepository.PrimitiveConcern)
			{
				if (obj instanceof Composestar.Core.CpsProgramRepository.CpsConcern.CpsConcern)
				{
					Composestar.Core.CpsProgramRepository.CpsConcern.CpsConcern cpsconcern = (Composestar.Core.CpsProgramRepository.CpsConcern.CpsConcern)obj;
					cpsconcern.addDynObject("IMPLEMENTATION",old);
					map.put(id,cpsconcern);
				}
			}
			else
			{
				// OOPS this is very bad!!!
				if (DEBUG) Debug.out(Debug.MODE_WARNING,"DataStore","Overwriting existing object '" + old + "' with id '" + id + "' with new object '" + obj + "'...");
				map.put(id, obj);
			}
		}
		else
		{
			map.put(id, obj);
		}
		return (id);
	}

	/**
	 * Adds an object to the Data Store, given the object.
	 *
	 * @param obj Object The Object to be stored.
	 * @return String  The id of the stored object.
	 *
	 * @modelguid {F304052E-9ECA-45EE-A174-DC958C5B09F1}
	 * @roseuid 401FAA62023F
	 */
	public String addObject(Object obj) {
		String id;
		if (obj instanceof RepositoryEntity)
		{
			id = ((RepositoryEntity) obj).getUniqueID();
		}
		else
		{
			id = obj.getClass() + "_" + obj.hashCode();
		}
		return addObject(id, obj);
	}

	/**
	 * Removes an object given the ID.
	 *
	 * @param id String The id of the object to be removed.
	 * @return Object  The object that was removed.
	 *
	 * @modelguid {28B326CA-2C74-4915-9EF3-F93463D1BB81}
	 * @roseuid 401FAA620248
	 */
	public Object removeObject(String id) {
		if (DEBUG) Debug.out(Debug.MODE_INFORMATION,"DataStore","Remove object from datastore with id '" + id + "'.");
		return (map.remove(id));
	}

	/**
	 * Removes an object given the ID.
	 *
	 * @param obj Object The object to be removed.
	 * @return Object  The object that was removed.
	 *
	 * @modelguid {3B873CFC-DA99-4C45-AB21-9C4485E9BEC3}
	 * @roseuid 401FAA620251
	 */
	public Object removeObject(Object obj) {
		return null;
	}

	/**
	 * Checks if the given id is contained in the datastore.
	 *
	 * @param id String The id to check for.
	 * @return boolean The result true if it is in the datastore and false if not.
	 *
	 * @modelguid {7B2E1320-8C24-416C-86F3-B2A47D19E23F}
	 * @roseuid 401FAA62025B
	 */
	public boolean containsObject(String id) {
		return (map.containsKey(id));
	}

	/**
	 * Checks if the given object is contained in the datastore.
	 *
	 * @param obj Object The object to check for.
	 * @return boolean The result true if it is in the datastore and false if not.
	 *
	 * @modelguid {80CD0289-8A4C-4ECE-8709-A2F8B3ECB1A5}
	 * @roseuid 401FAA62025D
	 */
	public boolean containsObject(Object obj) {
		return (map.containsValue(obj));
	}

	/**
	 * Returns the object given the ID.
	 *
	 * @param id Strng The id of the object wanted.
	 * @return Object  The corresponding object, or null if not present.
	 *
	 * @modelguid {1D233105-533F-4CAF-9478-E6BCC485D73E}
	 * @roseuid 401FAA620266
	 */
	public Object getObjectByID(String id) {
		return (map.get(id));
	}

	/**
	 * Returns the object from the requested field, this can be a name for example.
	 *
	 * @param field String The name of the field.
	 * @param obj   Object The object from which the field should be retrieved.
	 * @return Object The corresponding object, or null if not present.
	 *
	 * @modelguid {3A2BE3AF-E8E6-4A6A-AC12-26FBFB58EB6C}
	 * @roseuid 401FAA620270
	 */
	public static Object getField(String field, Object obj) {
		Object object = null;
		try {
			Class c = obj.getClass();
			Field[] fields = c.getDeclaredFields();
			for (int i = 0; i < fields.length; i++) {
				if (fields[i].getName().equals(field)) {
					object = fields[i].get(field);
				}
			}
		}
		catch (Exception e) {
			if (DEBUG) {
				Debug.out(Debug.MODE_WARNING,"DataStore","Could not retrieve field '"+field+"' from object '"+obj+"'.");
				e.printStackTrace();
			}
			object = null;
		}
		return (object);
	}

	/**
	 * Returns the object from the requested fieldpath, this can be e.g. a name.
	 *
	 * @param path String[] The path of the field.
	 * @param obj  Object The object from which the field should be retrieved.
	 * @return Object The corresponding object, or null if not present.
	 *
	 * @modelguid {DFA5E5DE-8BAA-4D50-8820-D2B236E4172A}
	 * @roseuid 401FAA62027B
	 * @see ComposeStar.DataStore.getField(String field, Object obj)
	 */
	public static Object getFieldFromPath(String[] path, Object obj) {
		return null;
	}

	/**
	 * @param theclass
	 * @return java.util.Iterator
	 *
	 * @roseuid 404DC0490173
	 */
	public Iterator getAllInstancesOf(Class theclass) {
		Iterator iter = this.map.values().iterator();
		//Debug.out(Debug.MODE_DEBUG,"DataStore","HAS NEXT: "+enumer.hasMoreElements());
		ArrayList list = new ArrayList();
		while (iter.hasNext()) {
			Object obj = iter.next();
			if (theclass.isInstance(obj)) {
				list.add(obj);
			}
		}
		return list.iterator();
	}

	/**
	 * @param theclass
	 * @return java.util.ArrayList
	 */
	public ArrayList getListOfAllInstances(Class theclass){
		Iterator iter = this.map.values().iterator();
		ArrayList list = new ArrayList();
		while (iter.hasNext()) {
			Object obj = iter.next();
			if (theclass.isInstance(obj)) {
				list.add(obj);
			}
		}
		return list;
	}

	/**
	 * @return Composestar.Core.RepositoryImplementation.DataStore
	 *
	 * @roseuid 40572A47026A
	 */
	public static DataStore instance() {
		if (instance == null) {
			instance = new DataStore();
		}
		return (instance);
	}

	public static void setInstance(DataStore store)
	{
		instance = store;
	}

	/**
	 * @roseuid 40EBCD3C02B9
	 */
	public void clean() {
		map.clear();
	}
}
