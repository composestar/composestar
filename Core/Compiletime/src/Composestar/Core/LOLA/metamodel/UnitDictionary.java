/*
 * Fast index on Language Units, used by the prolog engines for looking up
 * Language Units.
 * 
 * UnitDictionary.java - Created on 23-aug-2004 by havingaw
 */

package Composestar.Core.LOLA.metamodel;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;

import Composestar.Utils.*;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.LAMA.ProgramElement;
import Composestar.Core.LAMA.UnitResult;

/**
 * @author havingaw
 *
 * Keeps a list of all registered Language Units that can be queried by the prolog engine.
 * Several indexes are created, such that looking up units should be fast in most 
 * common cases.
 */

public class UnitDictionary
{  
  private LanguageModel langModel;
  
  // Indexes on Units. Be very careful to keep these consistent with each other.
  // The information is stored in different ways to enable faster lookups.
  // E.g. users will often query by unit name, while the prolog engine will often
  // look up units based on type+id. So at least those methods should be fast.
  
  // units contains all language units. The key is the classname of the unit type,
  // the value is a hashset of all LanguageUnits of that type.
  private Hashtable unitsByType;
  
  // Used to select units by name - like units, the first level is an index classType=>2nd hashtable.
  // The 2nd level hashtable contains the name of a unit as key, and the object itself as value if
  // the name is guaranteed to be unique (Namespaces, Classes, Types, Interfaces), or a hashset
  // of objects if they don't have to be unique (fields, operations, methods, constructors).
  private Hashtable unitsByName; 

  public UnitDictionary()
  {
    this(null);
  }
  
  public UnitDictionary(LanguageModel langModel)
  {
    this.unitsByType = new Hashtable();
    this.unitsByName = new Hashtable();
    this.langModel = langModel;
  }
  
  /**
   * Directly adds a language unit to the dictionary. Note that this method will call
   * the getUnitName()-method of the LanguageUnit! The method also needs a LanguageModel
   * to be set using setLanguageModel(), so it can do some consistency checks (e.g. on
   * unique names really being unique).
   *  
   * @param unit the language unit to add
   */
  public void addLanguageUnit(ProgramElement unit) throws ModuleException
  {
    if (null == langModel)
    {
      Debug.out(Debug.MODE_WARNING, "LOLA", "UnitDictionary: internal error; no language model has been set!");
      throw new ModuleException("UnitDictionary needs a language model to function correctly!", "LOLA");
    }
    try
    {
      //String unitClass = unit.getClass().getName();
      String name = unit.getUnitName();
      String type = unit.getUnitType();
      LanguageUnitType unitInfo = langModel.getLanguageUnitType(type);
      
      
      // Step 1: Add the unit to the namebased index
      if (!unitsByName.containsKey(type)) // Might be the first unit of this type
      {
        unitsByName.put(type, new Hashtable()); // If yes, create the table for this type
      }

      Hashtable nameTypeTable = (Hashtable)unitsByName.get(type);
      if (unitInfo.isNameUnique()) // Unit has unique name; add it directly.
      {
  	  
        if (nameTypeTable.containsKey(name)) // In this case the unit will *NOT* have been added anywhere!
        {
        	Debug.out(Debug.MODE_WARNING,"LOLA","Duplicate key for unit with unique name: "+name);
        	return;
        	//throw new ModelClashException("Duplicate key for unit with unique name");
        }
          
        nameTypeTable.put(name, unit);
      }
      else // The unit might not have a unique name
      { // If no other unit with this name exists yet, create a hashset of names
        if (!nameTypeTable.containsKey(name))
        {
          nameTypeTable.put(name, new HashSet());
        }
        
        // Add the unit with this name to the hashset, and the hashset to the nametable.
        HashSet nameSet = (HashSet)nameTypeTable.get(name);
        nameSet.add(unit);
        nameTypeTable.put(name, nameSet);
      }

      // Step 2: Add the unit to the index by type
      // First unit of this type? Create a hashtable to store units of this type.
      if (!unitsByType.containsKey(type))
      {
        unitsByType.put(type, new HashSet());
      }
      
      // Add the unit to the 'unitsByType' dictionary
      HashSet typeSet = (HashSet)unitsByType.get(type);
      typeSet.add(unit);
    }
    catch (ModelException e)
    {
      System.err.println("Model error: " + e.getMessage());
      e.printStackTrace();
    }    
  }
  
  
  /**
   * @param id ID of the unit to look for
   * @return The language unit (any class that implements LanguageUnit) with the specified ID,
   *         null if no such unit exists. 
   */
  /*public LanguageUnit getUnit(String id)
  {
    Iterator i = unitsByType.keySet().iterator();
    while (i.hasNext())
    {
      Hashtable list = (Hashtable) unitsByType.get(i.next());
      if (list.containsKey(id))
        return (LanguageUnit)list.get(id);
    }
    return null;
  }*/

  
  /**
   * @param id ID of the unit to look for 
   * @param type Type of the unit to look for (=getClass().getName() of the specified language unit class)
   * @return The language unit of type 'type' with the specified ID,
   *         null if no such unit exists.
   */
  /*public LanguageUnit getUnit(String id, String type)
  {
    if (unitsByType.containsKey(type))
      return (LanguageUnit)((HashSet)unitsByType.get(type)). (id); // Result of 2nd get can still be null!
    return null;
  }*/
  
  /**
   * @param name Name of the unit to look for
   * @param type Type of the unit to look for (=getClass().getName() of the specified language unit class)
   * @return Language unit with the specified name, or null if no such unit exists *or* the
   *         unit is of a type where names might not be unique (!)
   */
  public UnitResult getByName(String name, String type)
  {
    if (!unitsByName.containsKey(type))
    {
      System.err.println("UnitDictionary::getUnitbyName() - Looking for a unit type that does not exist!");
      return null; // The unit type does not even exist!
    }
    
    Hashtable names = (Hashtable)unitsByName.get(type);
    if (names.containsKey(name))
    {
      Object result = names.get(name);
      if (result instanceof ProgramElement)
      {
        return new UnitResult((ProgramElement)result);
      }
      else if (result instanceof HashSet)
      {
        return new UnitResult((HashSet)result);
      }
      else
      {
        System.err.println("Internal error: wrong object type in UnitDictionary");
      }
    }
    return null;
  }
  
  /**
   * Returns all objects with the specified name, regardless of type.
   * This can be dangerous, because there may be units of different types having the same name.
   * E.g. there could be a field 'someVar' and also a method 'someVar'.
   * However, this may be exactly what you want.
   * @param name Name of the unit(s) that you are looking for
   * @return UnitResult containing the unit(s) that you asked for; empty multiValue-set if none are found
   */
  public UnitResult getByName(String name)
  {
    Iterator types = unitsByName.keySet().iterator();
    HashSet result = new HashSet();
    while (types.hasNext())
    {
      UnitResult typeRes = getByName(name, (String)types.next());
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
   * @param type
   */
  public UnitResult getByType(String type)
  {
    if (unitsByType.containsKey(type)){
      return new UnitResult((HashSet)unitsByType.get(type));
    }else{
      Debug.out(Debug.MODE_DEBUG, "LOLA", "UnitDictionary warning: request for non-existing unit type");
      return null;
    }
  }
  
  /*
   * Returns all registered units.
   */
  public UnitResult getAll()
  {
    HashSet result = new HashSet();

    Iterator types = unitsByType.values().iterator();
    while (types.hasNext())
    {
      result.addAll((HashSet)types.next());
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
