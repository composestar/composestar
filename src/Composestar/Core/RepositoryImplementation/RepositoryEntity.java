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

import Composestar.Utils.*;

import java.util.Iterator;
import java.lang.CloneNotSupportedException;

/**
 * @modelguid {DCB519CD-06CE-4264-98CC-EEB6B4793736}
 */
public class RepositoryEntity implements SerializableRepositoryEntity, Cloneable 
{
    
    /**
	 * 
	 */
	private static final long serialVersionUID = -7445401465568382172L;
	public DataMap dynamicmap;
    public String repositoryKey;	
    
	public String descriptionFileName;
	public int descriptionLineNumber;

	/**
	 * @return int
	 *
	 * @roseuid 401FAA630543
	 */

	public int getDescriptionLineNumber()
	{
		return descriptionLineNumber;
	}

	/**
	 * @param newLineNumber
	 *
	 * @roseuid 401FAA637532
	 */

	public void setDescriptionLineNumber(int newLineNumber)
	{
		this.descriptionLineNumber = newLineNumber;
	}

	/**
	 * @return java.lang.String
	 *
	 * @roseuid 401FAA630234
	 */

	public String getDescriptionFileName()
	{
		return descriptionFileName;
	}
	
	/**
	 * @param newFileName
	 *
	 * @roseuid 401FAA632345
	 */

	public void setDescriptionFileName(String newFileName)
	{
		this.descriptionFileName = newFileName;
	}

    /**
     * @modelguid {5C08748F-F17E-470D-A282-552FB667A7E1}
     * @roseuid 401FAA670295
     */
    public RepositoryEntity() {
		dynamicmap = new DataMap();
		createRepositoryReference();
    }
    
    /**
     * @param key
     * @param obj
     * @roseuid 405066C6007A
     */
    public void addDynObject(String key, Object obj) {
    this.dynamicmap.put(key, obj);     
    }
    
    /**
     * @param key
     * @return java.lang.Object
     * @roseuid 405066D40193
     */
    public Object getDynObject(String key) {    
  	//Debug.out(Debug.MODE_DEBUG,"RepositoryImplementation","Keys: "+this.dynamicmap.keySet());
    return(this.dynamicmap.get(key));     
    }
    
    /**
     * @return java.util.Iterator
     * @roseuid 405066E7030A
     */
    public Iterator getDynIterator() {
  	//Debug.out(Debug.MODE_DEBUG,"RepositoryImplementation","DynMap: "+this.dynamicmap);
    return(this.dynamicmap.values().iterator());     
    }
    
    public void createRepositoryReference()
    {
		DataStore ds = DataStore.instance();
		if( repositoryKey == null )
		{
			repositoryKey = ds.addObject(this);
		}
    }
    
    public void updateRepositoryReference()
    {
		if( repositoryKey != null && repositoryKey.compareTo(this.getUniqueID()) != 0 )
		{
			String oldKey = repositoryKey;
			DataStore ds = DataStore.instance();
			ds.removeObject(oldKey);
			repositoryKey = ds.addObject(this);
		}
    }
    
    public String getUniqueID()
    {
		return this.getClass() + "_" + this.hashCode();
    }

	public Object clone() throws CloneNotSupportedException
	{
		RepositoryEntity entity;
		
		entity = (RepositoryEntity) super.clone();
		
		// At this point, the newObject shares all data with the object
		// running clone. If you want newObject to have its own
		// copy of data, you must clone this data yourself.

		entity.dynamicmap = (DataMap)dynamicmap.clone(); 
		return entity;
	}

}
/**
 * void RepositoryEntity.accept(RepositoryVisitor){
 * visitor.visit(this);
 * }
 */
