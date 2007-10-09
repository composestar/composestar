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

import java.util.Iterator;

//
// !! Compose* Runtime Warning !!
//
// This class is referenced in the Compose* Runtime for .NET 1.1
// Do not use Java features added after Java 2.0
//

public class RepositoryEntity implements SerializableRepositoryEntity, Cloneable
{
	private static final long serialVersionUID = -7445401465568382172L;

	/**
	 * Counter used to create the uniqueId
	 */
	private static int counter;

	/**
	 * Data store for accociate information with this entity that is also
	 * required during runtime. All entities added to this map need to be
	 * serializable.
	 */
	public DataMap dynamicmap;

	/**
	 * The key under which this entity is added to the repository
	 */
	public String repositoryKey;

	/**
	 * Used for a proper unique ID in the getUniqueId() method
	 */
	public int uniqueId;

	/**
	 * Filename this entity was declared in. Only used for entities specified in
	 * .cps files
	 */
	public String descriptionFileName;

	/**
	 * Line number where this entity was declared
	 */
	public int descriptionLineNumber;

	/**
	 * Line number where this entity was declared
	 */
	public int descriptionLinePosition;

	public RepositoryEntity()
	{
		dynamicmap = DataMap.newDataMapInstance();
		uniqueId = getCounter();

		DataStore ds = DataStore.instance();
		if (ds != null) // could be null in case of deserialization at runtime
		{
			repositoryKey = ds.addObject(this);
		}
	}

	private static synchronized int getCounter()
	{
		return counter++;
	}

	public int getDescriptionLineNumber()
	{
		return descriptionLineNumber;
	}

	public void setDescriptionLineNumber(int newLineNumber)
	{
		descriptionLineNumber = newLineNumber;
	}

	public int getDescriptionLinePosition()
	{
		return descriptionLinePosition;
	}

	public void setDescriptionLinePosition(int newLineNumber)
	{
		descriptionLinePosition = newLineNumber;
	}

	public String getDescriptionFileName()
	{
		return descriptionFileName;
	}

	public void setDescriptionFileName(String newFileName)
	{
		descriptionFileName = newFileName;
	}

	public void addDynObject(String key, Object obj)
	{
		dynamicmap.put(key, obj);
	}

	public Object getDynObject(String key)
	{
		return dynamicmap.get(key);
	}

	public Iterator getDynIterator()
	{
		return dynamicmap.values().iterator();
	}

	/**
	 * Update repository to point to the proper location of this instance. The
	 * key could have been changed.
	 */
	public void updateRepositoryReference()
	{
		if (repositoryKey != null && repositoryKey.compareTo(getUniqueID()) != 0)
		{
			DataStore ds = DataStore.instance();
			if (ds != null) // is null when deserializing at runtime
			{
				String oldKey = repositoryKey;
				ds.removeObject(oldKey);
				repositoryKey = ds.addObject(this);
			}
		}
	}

	/**
	 * @return the repository key of this entity
	 */
	public String getRepositoryKey()
	{
		return repositoryKey;
	}

	public void setRepositoryKey(String newKey)
	{
		// System.err.println("Updating repo key from " + repositoryKey + " to "
		// + newKey);
		repositoryKey = newKey;
	}

	/**
	 * Returns the generated unique identifier. This might not be equal to the
	 * stored repository key.
	 * 
	 * @return
	 */
	public String getUniqueID()
	{
		return this.getClass() + "#" + uniqueId;
	}

	public Object clone() throws CloneNotSupportedException
	{
		RepositoryEntity clone = (RepositoryEntity) super.clone();

		// At this point, the newObject shares all data with the object
		// running clone. If you want newObject to have its own
		// copy of data, you must clone this data yourself.

		clone.dynamicmap = (DataMap) dynamicmap.clone();
		return clone;
	}
}
