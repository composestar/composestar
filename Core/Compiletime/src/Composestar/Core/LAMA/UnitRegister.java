package Composestar.Core.LAMA;

import Composestar.Core.RepositoryImplementation.SerializableRepositoryEntity;

import java.util.HashSet;

/**
 * Contains a set of registered program elements. Separated from UnitDictionary
 * because the Runtime only needs this part; not the entire dictionary. Created
 * on Nov 3, 2004 by wilke
 */
public class UnitRegister implements SerializableRepositoryEntity
{
	private static final long serialVersionUID = 6093032955062774862L;

	// Contains the set of program elements, before they are processed by
	// UnitDictionary.processLanguageUnits
	private HashSet registeredUnits;

	private static UnitRegister instance;

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
		registeredUnits = new HashSet();
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
	public HashSet getRegisteredUnits()
	{
		return registeredUnits;
	}
}
