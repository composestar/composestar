/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2006 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */
package Composestar.Core.LAMA;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

//
// !! Compose* Runtime Warning !!
//
// This class is referenced in the Compose* Runtime for .NET 1.1
// Do not use Java features added after Java 2.0
//

/**
 * Contains a set of registered program elements. Separated from UnitDictionary
 * because the Runtime only needs this part; not the entire dictionary. Created
 * on Nov 3, 2004 by wilke
 */
public class UnitRegister implements Serializable
{
	private static final long serialVersionUID = 9098646852116014L;

	public static final String RESOURCE_KEY = "UnitRegister";

	// Contains the set of program elements, before they are processed by
	// UnitDictionary.processLanguageUnits
	private Set<ProgramElement> registeredUnits;

	@Deprecated
	private static UnitRegister instance;

	@Deprecated
	public static UnitRegister instance()
	{
		if (instance == null)
		{
			instance = new UnitRegister();
		}
		return instance;
	}

	/**
	 * Creates a new UnitRegister.
	 */
	public UnitRegister()
	{
		registeredUnits = new HashSet<ProgramElement>();
	}

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
	}

	/**
	 * @return Returns the registeredUnits.
	 */
	public Set<ProgramElement> getRegisteredUnits()
	{
		return registeredUnits;
	}
}
