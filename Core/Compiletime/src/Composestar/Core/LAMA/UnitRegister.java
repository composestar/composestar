/*
 * This file is part of the Compose* project.
 * http://composestar.sourceforge.net
 * Copyright (C) 2006-2008 University of Twente.
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
package Composestar.Core.LAMA;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Registry where all harvested units will be stored. It will/should be placed
 * in the common resources. The content of this register is copied to the
 * datastore repository, therefor all fields are set to be transient (it can
 * cause problems when serializing the compile history).
 */
public class UnitRegister
{
	public static final String RESOURCE_KEY = "LAMA.UnitRegister";

	/**
	 * Contains the set of program elements, before they are processed by
	 * UnitDictionary.processLanguageUnits
	 */
	private Set<ProgramElement> registeredUnits = new HashSet<ProgramElement>();

	private Set<Type> pendingReg = new HashSet<Type>();

	private Map<String, Type> typeMap = new HashMap<String, Type>();

	/**
	 * Creates a new UnitRegister.
	 */
	public UnitRegister()
	{}

	/**
	 * Registers a program element. The unit will *not* actually be added to the
	 * dictionary until you call processLanguageUnits(). This is done because
	 * the unit name must be available in order to add it. As units are
	 * registered at creation, this information may not yet be available. In
	 * such cases, call this method. If all information is already available,
	 * just add it directly, using addLanguageUnit().
	 * 
	 * @param unit the language unit to add to the dictionary
	 */
	public void registerLanguageUnit(ProgramElement unit)
	{
		// Only register the unit. We can *not* call its methods (e.g.
		// getName()) because the unit might not
		// be fully initialized at this point.
		// Therefore, call 'processLanguageUnits()' to really create the table
		// of units.
		registeredUnits.add(unit);
		if (unit instanceof Type)
		{
			Type type = (Type) unit;
			if (type.getFullName() != null)
			{
				typeMap.put(type.getFullName(), type);
			}
			else
			{
				pendingReg.add(type);
			}
		}
	}

	/**
	 * @return Returns the registeredUnits.
	 */
	public Set<ProgramElement> getRegisteredUnits()
	{
		return registeredUnits;
	}

	/**
	 * Construct the typemap and resolve the types
	 * 
	 * @param resolver
	 */
	public void resolveTypes(TypeResolver resolver)
	{
		// first add full names
		for (Type unit : pendingReg)
		{
			typeMap.put(unit.getFullName(), unit);
		}
		pendingReg.clear();
		for (ProgramElement unit : registeredUnits)
		{
			resolver.resolve(unit, this);
		}
	}

	public Type getType(String typename)
	{
		return typeMap.get(typename);
	}

	public void removeType(String typename)
	{
		typeMap.remove(typename);
	}

	public boolean hasType(String typename)
	{
		return typeMap.containsKey(typename);
	}

	public Map<String, Type> getTypeMap()
	{
		return Collections.unmodifiableMap(typeMap);
	}
}
