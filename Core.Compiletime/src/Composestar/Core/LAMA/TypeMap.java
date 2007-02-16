/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */
package Composestar.Core.LAMA;

import java.util.Collection;

import Composestar.Core.RepositoryImplementation.DataMap;
import Composestar.Core.RepositoryImplementation.SerializableRepositoryEntity;

/**
 * TypeMap holds all the Types. It is used by the various classes to resolve
 * their type dependencies.
 */
public class TypeMap implements SerializableRepositoryEntity
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3742264878434693439L;

	private DataMap Types;

	private static TypeMap Instance;

	/**
	 * @roseuid 402CA342033C
	 */
	public TypeMap()
	{
		Types = DataMap.newDataMapInstance();
	}

	/**
	 * Add a new type to the map
	 * 
	 * @param name Unique identifier of the type to add.
	 * @param type
	 * @roseuid 402CA2FF0154
	 */
	public void addType(String name, Type type)
	{
		Types.put(name, type);
	}

	/**
	 * Retrieve a type from the map
	 * 
	 * @param name
	 * @return The requested Type or null if it wasn't found.
	 * @roseuid 402CA30E002A
	 */
	public Type getType(String name)
	{
		return (Type) Types.get(name);
	}

	/**
	 * Retrieve the TypeMap instance
	 * 
	 * @return Composestar.Core.LAMA.TypeMap
	 * @roseuid 402CA35A010F
	 */
	public static TypeMap instance()
	{
		if (Instance == null)
		{
			Instance = new TypeMap();
		}
		return Instance;
	}

	/**
	 * @return java.util.Collection
	 * @roseuid 405AD19F032D
	 */
	public Collection values()
	{
		return Types.values();
	}

	/**
	 * @return java.util.HashMap
	 * @roseuid 405AD1CB0396
	 */
	public java.util.HashMap map()
	{
		return Types.toHashMap();
	}
}
