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

import Composestar.Core.CpsProgramRepository.Concern;

/**
 * A repository entity with a qualified name. The qualified name is the defines
 * the value of the unique ID
 */
public class DeclaredRepositoryEntity extends ContextRepositoryEntity
{
	private static final long serialVersionUID = -3327856777545425252L;

	public String name;

	public DeclaredRepositoryEntity()
	{
		super();
	}

	/**
	 * @param name
	 * @param parent - set the parent when instantiating the object
	 */
	public DeclaredRepositoryEntity(String inName, RepositoryEntity parent)
	{
		super(parent);
		setName(inName);
	}

	/**
	 * @return java.lang.String
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Set the name of this entity. This will have as side effect that the
	 * qualified name is also change and therefor the unique ID and repository
	 * key.
	 * 
	 * @param nameValue
	 */
	public void setName(String nameValue)
	{
		name = nameValue;
		updateRepositoryReference();
	}

	/**
	 * Return a string with the names along the full path of parents
	 * 
	 * @return java.lang.String
	 */
	public String getQualifiedName()
	{		
		StringBuffer out = new StringBuffer();
		DeclaredRepositoryEntity o;

		o = this;
		while (o != null)
		{
			if (out.length() > 0)
			{
				out.insert(0, ".");
			}
			
			if (o instanceof Concern)
			{
				// concern overwrites getQualifiedName
				out.insert(0, o.getQualifiedName());
				// stop when we reach the concern
				break;
			}
			else
			{
				out.insert(0, o.getName());
				o = (DeclaredRepositoryEntity) o.getParent();
			}
		}
		return out.toString();
	}

	public String getUniqueID()
	{
		try
		{
			String s = this.getQualifiedName();
			if (s == null)
			{
				// very special case where a concern doesn't have a name yet...
				s = super.getUniqueID();
			}

			return s;
		}
		catch (NullPointerException npe)
		{
			return super.getUniqueID();
		}
	}

	public Object clone() throws CloneNotSupportedException
	{
		DeclaredRepositoryEntity newObject;

		newObject = (DeclaredRepositoryEntity) super.clone();

		// At this point, the newObject shares all data with the object
		// running clone. If you want newObject to have its own
		// copy of data, you must clone this data yourself.

		return newObject;
	}

}
