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

	public DataMap dynamicmap;

	public String repositoryKey;

	public String descriptionFileName;

	public int descriptionLineNumber;

	public RepositoryEntity()
	{
		dynamicmap = new DataMap();

		DataStore ds = DataStore.instance();
		repositoryKey = ds.addObject(this);
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
		// Debug.out(Debug.MODE_DEBUG,"RepositoryImplementation","Keys:
		// "+this.dynamicmap.keySet());
		return this.dynamicmap.get(key);
	}

	public Iterator getDynIterator()
	{
		// Debug.out(Debug.MODE_DEBUG,"RepositoryImplementation","DynMap:
		// "+this.dynamicmap);
		return this.dynamicmap.values().iterator();
	}

	public void updateRepositoryReference()
	{
		if (repositoryKey != null && repositoryKey.compareTo(this.getUniqueID()) != 0)
		{
			String oldKey = repositoryKey;
			DataStore ds = DataStore.instance();
			ds.removeObject(oldKey);
			repositoryKey = ds.addObject(this);
		}
	}

	public String getUniqueID()
	{
		// FIXME: obj.hashCode() is not guaranteed to produce unique values
		// for distinct instances of some class, so this can lead to subtle
		// bugs.
		// FIXME: shouldn't this simply return repositoryKey?
		return this.getClass() + "_" + this.hashCode();
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
