/*
 * Stores meta-information about LanguageUnits that can be used in the Prolog query engine.
 * 
 * Composite language units are a 'supertype' of 2 or more normal language units.
 * E.g. there exist language units for Classes and Interfaces, but many times the programmer
 * won't care and will refer to both as 'Types'.
 * 
 * The prolog engine 'fakes' inheritance by creating predicates that act like there exists a
 * composite unit type, e.g. the definition of isSuperType would look like:
 * 
 * isSuperType(X, Y) :- isSuperClass(X,Y).
 * isSuperType(X, Y) :- isSuperInterface(X,Y).
 * 
 * Because the Composite LanguageUnit is not a 'real' language unit type, it does not have relations itself.
 * Instead, it describes how existing relations are combined into ones for the composite type (as above).
 *   
 *  
 * Created on Nov 25, 2004 by wilke
 */
package Composestar.Core.LOLA.metamodel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CompositeLanguageUnitType extends LanguageUnitType
{
	protected List<LanguageUnitType> containedTypes;

	public CompositeLanguageUnitType(Class<?> impl, String type, boolean nameUnique, LanguageUnitType type1,
			LanguageUnitType type2)
	{
		this(impl, type, nameUnique, null);
		containedTypes = new ArrayList<LanguageUnitType>();
		containedTypes.add(type1);
		containedTypes.add(type2);
	}

	public CompositeLanguageUnitType(Class<?> impl, EUnitType type, boolean nameUnique, LanguageUnitType type1,
			LanguageUnitType type2)
	{
		this(impl, type.toString(), nameUnique, type1, type2);
	}

	public CompositeLanguageUnitType(Class<?> impl, String type, boolean nameUnique,
			List<LanguageUnitType> containedTypes)
	{
		super(impl, type, nameUnique);
		this.containedTypes = containedTypes;
	}

	/**
	 * @return Returns the containedTypes.
	 */
	public List<LanguageUnitType> getContainedTypes()
	{
		return Collections.unmodifiableList(containedTypes);
	}

	/**
	 * @param containedTypes The containedTypes to set.
	 */
	public void setContainedTypes(List<LanguageUnitType> containedTypes)
	{
		this.containedTypes = containedTypes;
	}
}
