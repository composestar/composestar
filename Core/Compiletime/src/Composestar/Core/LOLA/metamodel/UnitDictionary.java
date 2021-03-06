/*
 * Fast index on Language Units, used by the prolog engines for looking up
 * Language Units.
 * 
 * UnitDictionary.java - Created on 23-aug-2004 by havingaw
 */

package Composestar.Core.LOLA.metamodel;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import Composestar.Core.Exception.ModuleException;
import Composestar.Core.LAMA.ProgramElement;
import Composestar.Core.LAMA.UnitResult;
import Composestar.Core.Master.ModuleNames;
import Composestar.Utils.Logging.CPSLogger;

/**
 * Keeps a list of all registered Language Units that can be queried by the
 * prolog engine. Several indexes are created, such that looking up units should
 * be fast in most common cases.
 * 
 * @author havingaw
 */
public class UnitDictionary
{
	protected static final CPSLogger logger = CPSLogger.getCPSLogger(ModuleNames.LOLA);

	public static final String REPOSITORY_KEY = "LOLA.UnitDictionary";

	private LanguageModel langModel;

	// Indexes on Units. Be very careful to keep these consistent with each
	// other.
	// The information is stored in different ways to enable faster lookups.
	// E.g. users will often query by unit name, while the prolog engine will
	// often look up units based on type+id. So at least those methods should be
	// fast.

	/**
	 * Contains all language units. The key is the classname of the unit type,
	 * the value is a set of all LanguageUnits of that type.
	 */
	private Map<String, Set<ProgramElement>> unitsByType;

	/**
	 * Used to select units by name - like unitsByType, the first level is an
	 * index classType -> 2nd hashtable. The 2nd level hashtable contains the
	 * name of a unit as key, and the object itself as value if the name is
	 * guaranteed to be unique (Namespaces, Classes, Types, Interfaces), or a
	 * hashset of objects if they don't have to be unique (fields, operations,
	 * methods, constructors).
	 */
	private Map<String, Map<String, Object>> unitsByName;

	private Map<String, ProgramElement> duplicateUniqueUnits;

	public UnitDictionary()
	{
		this(null);
	}

	public UnitDictionary(LanguageModel langModel)
	{
		unitsByType = new HashMap<String, Set<ProgramElement>>();
		unitsByName = new HashMap<String, Map<String, Object>>();
		duplicateUniqueUnits = new HashMap<String, ProgramElement>();
		this.langModel = langModel;
	}

	/**
	 * Directly adds a language unit to the dictionary. Note that this method
	 * will call the getUnitName()-method of the LanguageUnit! The method also
	 * needs a LanguageModel to be set using setLanguageModel(), so it can do
	 * some consistency checks (e.g. on unique names really being unique).
	 * 
	 * @param unit the language unit to add
	 */
	public void addLanguageUnit(ProgramElement unit) throws ModuleException
	{
		if (langModel == null)
		{
			logger.warn("UnitDictionary: internal error; no language model has been set!");
			throw new ModuleException("UnitDictionary needs a language model to function correctly!", "LOLA");
		}
		try
		{
			String name = unit.getUnitName();
			String type = unit.getUnitType();
			// Debug.out(Debug.MODE_DEBUG, LOLA.MODULE_NAME, "Adding " + type +
			// ": '" + name + "'");

			LanguageUnitType unitInfo = langModel.getLanguageUnitType(type);

			// Step 1: Add the unit to the namebased index
			if (!unitsByName.containsKey(type)) // Might be the first unit of
			// this type
			{
				/*
				 * If yes, create the table for this type
				 */
				unitsByName.put(type, new HashMap<String, Object>());
			}

			Map<String, Object> nameTypeTable = unitsByName.get(type);
			if (unitInfo.isNameUnique()) // Unit has unique name; add it
			// directly.
			{
				if (nameTypeTable.containsKey(name)) // In this case the unit
				// will *NOT* have been
				// added anywhere!
				{
					// Add warning suppression for duplicate class names within
					// different assemblies
					if (unit.getUnitType().equalsIgnoreCase("class"))
					{
						duplicateUniqueUnits.put(name, unit);
						nameTypeTable.remove(name);
					}

					logger.warn("Duplicate key for unit with unique name: " + name + " (type '" + unit.getUnitType()
							+ "')");
					return;
					// throw new ModelClashException("Duplicate key for unit
					// with unique name");
				}

				if (duplicateUniqueUnits.containsKey(name))
				{
					return;
				}

				nameTypeTable.put(name, unit);
			}
			else
			// The unit might not have a unique name
			{ // If no other unit with this name exists yet, create a hashset
				// of names
				if (!nameTypeTable.containsKey(name))
				{
					nameTypeTable.put(name, new HashSet<ProgramElement>());
				}

				// Add the unit with this name to the hashset, and the hashset
				// to the nametable.
				Set<ProgramElement> nameSet = (Set<ProgramElement>) nameTypeTable.get(name);
				nameSet.add(unit);
				nameTypeTable.put(name, nameSet);
			}

			// Step 2: Add the unit to the index by type
			// First unit of this type? Create a hashtable to store units of
			// this type.
			if (!unitsByType.containsKey(type))
			{
				unitsByType.put(type, new HashSet<ProgramElement>());
			}

			// Add the unit to the 'unitsByType' dictionary
			Set<ProgramElement> typeSet = unitsByType.get(type);
			typeSet.add(unit);
		}
		catch (ModelException e)
		{
			throw new ModuleException("Model error: " + e.getMessage(), ModuleNames.LOLA);
		}
	}

	// /**
	// * @param id ID of the unit to look for
	// * @return The language unit (any class that implements LanguageUnit) with
	// * the specified ID, null if no such unit exists.
	// */
	// public LanguageUnit getUnit(String id)
	// {
	// Iterator i = unitsByType.keySet().iterator();
	// while (i.hasNext())
	// {
	// Hashtable list = (Hashtable) unitsByType.get(i.next());
	// if (list.containsKey(id))
	// return (LanguageUnit)list.get(id);
	// }
	// return null;
	// }

	// /**
	// * @param id ID of the unit to look for
	// * @param type Type of the unit to look for (=getClass().getName() of the
	// * specified language unit class)
	// * @return The language unit of type 'type' with the specified ID, null if
	// * no such unit exists.
	// */
	// public LanguageUnit getUnit(String id, String type)
	// {
	// Map units = (Map)unitsByType.get(type);
	// if (units != null)
	// {
	// return (LanguageUnit)units.get(id);
	// }
	// return null;
	// }

	/**
	 * @param name Name of the unit to look for
	 * @param type Type of the unit to look for (=getClass().getName() of the
	 *            specified language unit class)
	 * @return Language unit with the specified name, or null if no such unit
	 *         exists *or* the unit is of a type where names might not be unique
	 *         (!)
	 */
	public UnitResult getByName(String name, String type)
	{
		if (!unitsByName.containsKey(type))
		{
			logger.warn("UnitDictionary::getByName - type '" + type + "' not found!");

			return null; // The unit type does not even exist!
		}

		Map<String, Object> names = unitsByName.get(type);
		if (names.containsKey(name))
		{
			Object result = names.get(name);
			if (result instanceof ProgramElement)
			{
				return new UnitResult((ProgramElement) result);
			}
			else if (result instanceof Set)
			{
				return new UnitResult((Set<ProgramElement>) result);
			}
			else
			{
				throw new RuntimeException("Wrong object type in UnitDictionary: " + result.getClass());
			}
		}
		return null;
	}

	/**
	 * Returns all objects with the specified name, regardless of type. This can
	 * be dangerous, because there may be units of different types having the
	 * same name. E.g. there could be a field 'someVar' and also a method
	 * 'someVar'. However, this may be exactly what you want.
	 * 
	 * @param name Name of the unit(s) that you are looking for
	 * @return UnitResult containing the unit(s) that you asked for; empty
	 *         multiValue-set if none are found
	 */
	public UnitResult getByName(String name)
	{
		Iterator<String> types = unitsByName.keySet().iterator();
		Set<ProgramElement> result = new HashSet<ProgramElement>();
		while (types.hasNext())
		{
			UnitResult typeRes = getByName(name, types.next());
			if (null != typeRes)
			{
				if (typeRes.isSingleValue())
				{
					result.add(typeRes.singleValue());
				}
				else
				{
					result.addAll(typeRes.multiValue());
				}
			}
		}
		return new UnitResult(result);
	}

	/**
	 * Returns all units of the specified type.
	 * 
	 * @param type
	 */
	public UnitResult getByType(String type)
	{
		if (unitsByType.containsKey(type))
		{
			return new UnitResult(unitsByType.get(type));
		}
		else
		{
			logger.debug("UnitDictionary warning: request for non-existing unit type '" + type + "'");
			return null;
		}
	}

	/*
	 * Returns all registered units.
	 */
	public UnitResult getAll()
	{
		Set<ProgramElement> result = new HashSet<ProgramElement>();

		for (Set<ProgramElement> o : unitsByType.values())
		{
			result.addAll(o);
		}

		return new UnitResult(result);
	}

	/**
	 * @param langModel The langModel to set.
	 */
	public void setLanguageModel(LanguageModel langModel)
	{
		this.langModel = langModel;
	}
}
