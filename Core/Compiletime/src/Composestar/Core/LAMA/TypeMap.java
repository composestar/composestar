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
import java.util.Map;

import Composestar.Core.RepositoryImplementation.DataMap;
import Composestar.Core.RepositoryImplementation.SerializableRepositoryEntity;

//
// !! Compose* Runtime Warning !!
//
// This class is referenced in the Compose* Runtime for .NET 1.1
// Do not use Java features added after Java 2.0
//

/**
 * TypeMap holds all the Types. It is used by the various classes to resolve
 * their type dependencies.
 */
public class TypeMap implements SerializableRepositoryEntity
{
	private static final long serialVersionUID = 3742264878434693439L;

	private Map types;

	private static TypeMap Instance;

	/**
	 * 
	 */
	public TypeMap()
	{
		types = DataMap.newDataMapInstance();
	}

	/**
	 * Add a new type to the map
	 * 
	 * @param name Unique identifier of the type to add.
	 * @param type
	 */
	public void addType(String name, Type type)
	{
		types.put(name, type);
	}

	/**
	 * Retrieve a type from the map
	 * 
	 * @param name
	 * @return The requested Type or null if it wasn't found.
	 */
	public Type getType(String name)
	{
		return (Type) types.get(name);
	}

	/**
	 * Retrieve the TypeMap instance
	 * 
	 * @return Composestar.Core.LAMA.TypeMap
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
	 */
	public Collection values()
	{
		return types.values();
	}

	/**
	 * @return java.util.HashMap
	 */
	public Map map()
	{
		return types;
	}
}
