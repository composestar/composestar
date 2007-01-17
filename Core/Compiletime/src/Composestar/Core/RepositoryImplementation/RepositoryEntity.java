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

public class RepositoryEntity implements SerializableRepositoryEntity, Cloneable
{
	private static final long serialVersionUID = -7445401465568382172L;

	/**
	 * Counter used to create the uniqueId
	 */
	private static int counter;

	public DataMap dynamicmap;

	public String repositoryKey;

	/**
	 * Used for a proper unique ID in the getUniqueId() method
	 */
	public int uniqueId;

	public String descriptionFileName;

	public int descriptionLineNumber;

	public RepositoryEntity()
	{
		dynamicmap = new DataMap();
		uniqueId = getCounter();

		DataStore ds = DataStore.instance();
		if (ds != null) // could be null in case of deserialization at runtime
		{
			repositoryKey = ds.addObject(this);
		}
	}

	private static int getCounter()
	{
		return counter++;
	}

	public int getDescriptionLineNumber()
	{
		return descriptionLineNumber;
	}

	public void setDescriptionLineNumber(int newLineNumber)
	{
		this.descriptionLineNumber = newLineNumber;
	}

	public String getDescriptionFileName()
	{
		return descriptionFileName;
	}

	public void setDescriptionFileName(String newFileName)
	{
		this.descriptionFileName = newFileName;
	}

	public void addDynObject(String key, Object obj)
	{
		this.dynamicmap.put(key, obj);
	}

	public Object getDynObject(String key)
	{
		return this.dynamicmap.get(key);
	}

	public Iterator getDynIterator()
	{
		return this.dynamicmap.values().iterator();
	}

	public void updateRepositoryReference()
	{
		if (repositoryKey != null && repositoryKey.compareTo(this.getUniqueID()) != 0)
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
	 * @return the repository key of this entiy
	 */
	public String getRepositoryKey()
	{
		return repositoryKey;
	}

	public void setRepositoryKey(String newKey)
	{
		System.err.println("Updating repo key from " + repositoryKey + " to " + newKey);
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
