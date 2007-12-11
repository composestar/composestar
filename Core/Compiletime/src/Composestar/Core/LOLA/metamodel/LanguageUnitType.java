/*
 * Created on Oct 25, 2004
 *
 * Stores meta-information about LanguageUnits that can be used in the Prolog query engine.
 * 
 * @author wilke
 */
package Composestar.Core.LOLA.metamodel;

import java.util.HashMap;
import java.util.Map;

public class LanguageUnitType
{
	private Class<?> impl; // Class that implements this type of LanguageUnit

	// private String predicateName; // Base name of this kind of language unit
	private String type; // Unit type

	private boolean nameUnique; // Whether the name of this kind of unit should

	// be unique

	private Map<String, RelationType> relationTypes;// Specs of

	// relations that
	// this type of unit

	// can have to other units

	public LanguageUnitType(/* String predicateName, */Class<?> impl, String type, boolean nameUnique)
	{
		// this.predicateName = predicateName;
		this.impl = impl;
		this.type = type;
		this.nameUnique = nameUnique;
		relationTypes = new HashMap<String, RelationType>();
	}

	/**
	 * @return Returns the class that implements this type of language unit
	 */
	public Class<?> getImplementingClass()
	{
		return impl;
	}

	/*
	 * public void setImplementingClass(Class impl) { this.impl = impl; }
	 */

	/**
	 * @return Returns the base predicate name of this type of unit.
	 */
	/*
	 * public String getPredicateName() { return this.predicateName; }
	 */

	/**
	 * @param predicateName The predicateName to set.
	 */
	/*
	 * public void setPredicateName(String predicateName) { this.predicateName =
	 * predicateName; }
	 */

	/**
	 * @return Returns whether the name of this type of unit should be unique.
	 */
	public boolean isNameUnique()
	{
		return nameUnique;
	}

	/**
	 * @param nameUnique The nameUnique to set.
	 */
	public void setNameUnique(boolean nameUnique)
	{
		this.nameUnique = nameUnique;
	}

	/**
	 * @param relationType A relation type specification
	 * @param argument This unit type is the Nth argument of this relation
	 *            (start counting at 0)
	 */
	public void addRelationType(RelationType relationType)
	{
		// Using a hashtable for fast lookups (based on the relation name)
		relationTypes.put(relationType.getName(), relationType);

	}

	/**
	 * @param relationName Name of the relation that point to other language
	 *            unit(s)
	 * @return Returns the relationType specifying this relation.
	 * @throws ModelClashException, when the relation does not exist for this
	 *             language unit.
	 * @param relName
	 */
	public RelationType getRelationType(String relName) throws ModelClashException
	{
		if (null == relationTypes.get(relName))
		{
			throw new ModelClashException("Unit does not have the relation " + relName);
		}
		return relationTypes.get(relName);
	}

	/**
	 * @return Returns the type.
	 */
	public String getType()
	{
		return type;
	}

	/**
	 * @param type The type to set.
	 */
	public void setType(String type)
	{
		this.type = type;
	}
}
