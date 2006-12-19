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

import java.util.Vector;

import Composestar.Core.CpsProgramRepository.Concern;

public class DeclaredRepositoryEntity extends ContextRepositoryEntity
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3327856777545425252L;

	public String name;

	/**
	 * @roseuid 404C4B6502BD
	 */
	public DeclaredRepositoryEntity()
	{
		super();
	}

	/**
	 * @param name
	 * @param parent - set the parent when instantiating the object
	 * @roseuid 401FAA6202FB
	 */
	public DeclaredRepositoryEntity(String name, RepositoryEntity parent)
	{
		super(parent);
		this.name = name;
		updateRepositoryReference();
	}

	/**
	 * @return java.lang.String
	 * @roseuid 401FAA620305
	 */
	public String getName()
	{
		return this.name;
	}

	/**
	 * @param nameValue
	 * @roseuid 401FAA620306
	 */
	public void setName(String nameValue)
	{
		this.name = nameValue;
		this.updateRepositoryReference();
	}

	/**
	 * return a string with the names along the full path of parents
	 * 
	 * @return java.lang.String
	 * @roseuid 401FAA620324
	 */
	public String getQualifiedName()
	{
		String out = null;
		DeclaredRepositoryEntity o;
		Vector temp = new Vector(); // here we store the names of parents
		int i;
		boolean ready = false;

		o = this;
		/*
		 * if(o instanceof CpsConcern) { return o.getQualifiedName(); }
		 */
		while (!ready && o != null)
		{
			if (o instanceof Concern)
			{ // stop when we reach the concern
				temp.addElement(o.getName());
				ready = true;
			}
			else
			{
				temp.addElement(o.getName());
				o = (DeclaredRepositoryEntity) o.getParent();
			}
		}
		// now traverse in reverse direction, so we start with a concern
		for (i = temp.size() - 1; i >= 0; i--)
		{
			if (i == temp.size() - 1)
			{
				out = (String) temp.elementAt(i); // do not add a dot for the
				// first element
			}
			else
			{
				out += '.' + (String) temp.elementAt(i);
			}
		}
		return out;
	}

	public String getUniqueID()
	{
		try
		{
			String s = this.getQualifiedName();
			if (s == null)
			{
				// special case where a concern doesn't have a name yet...
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
