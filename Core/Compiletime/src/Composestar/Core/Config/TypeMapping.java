/*
 * This file is part of the Compose* project.
 * http://composestar.sourceforge.net
 * Copyright (C) 2007 University of Twente.
 *
 * Compose* is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as 
 * published by the Free Software Foundation; either version 2.1 of 
 * the License, or (at your option) any later version.
 *
 * Compose* is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public 
 * License along with this program. If not, see 
 * <http://www.gnu.org/licenses/>.
 *
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.txt
 *
 * $Id$
 */

package Composestar.Core.Config;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

/**
 * Keeps track of the mappings between types and Sources.
 * 
 * @author Michiel Hendriks
 */
public class TypeMapping implements Serializable
{
	private static final long serialVersionUID = -5930101525680788205L;

	/**
	 * The mapping of types (as strings) to the Source instances. More than one
	 * type can refer to a Source instance.
	 */
	protected Map<String, Source> mapping;

	public TypeMapping()
	{
		mapping = new HashMap<String, Source>();
	}

	/**
	 * Add a mapping from type to source entry
	 * 
	 * @param type
	 * @param source
	 */
	public void addType(String type, Source source)
	{
		if (type == null || type.trim().length() == 0)
		{
			throw new IllegalArgumentException("Type can not be null or empty");
		}
		if (source == null)
		{
			throw new IllegalArgumentException("Source can not be null");
		}
		mapping.put(type.trim(), source);
	}

	/**
	 * Remove a type association
	 * 
	 * @param type
	 * @return
	 */
	public Source removeType(String type)
	{
		return mapping.remove(type);
	}

	/**
	 * Remove all entries associated with the given source
	 * 
	 * @param source
	 * @return
	 */
	public List<String> removeSource(Source source)
	{
		List<String> result = getTypes(source);
		for (String type : result)
		{
			mapping.remove(type);
		}
		return result;
	}

	/**
	 * Get a list of all types
	 * 
	 * @return
	 */
	public Set<String> getTypes()
	{
		return Collections.unmodifiableSet(mapping.keySet());
	}

	/**
	 * Get all types associated with a given Source
	 * 
	 * @param source
	 * @return
	 */
	public List<String> getTypes(Source source)
	{
		List<String> result = new ArrayList<String>();
		if (!mapping.containsValue(source))
		{
			return result;
		}
		for (Entry<String, Source> entry : mapping.entrySet())
		{
			if (entry.getValue().equals(source))
			{
				result.add(entry.getKey());
			}
		}
		return result;
	}

	/**
	 * Get the source for the given type. Can return null when no such type
	 * exists.
	 * 
	 * @param type
	 * @return
	 */
	public Source getSource(String type)
	{
		return mapping.get(type);
	}

	/**
	 * Get a readonly copy of the type mapping
	 * 
	 * @return
	 */
	public Map<String, Source> getMapping()
	{
		return Collections.unmodifiableMap(mapping);
	}
}
