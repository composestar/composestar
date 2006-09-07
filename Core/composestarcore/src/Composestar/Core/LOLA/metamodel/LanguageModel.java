/*
 * Created on Oct 25, 2004
 *
 * A language model consists of a particular set of LanguageUnitTypes;
 * language models should extend this class to implement constraints and checks, as well as
 * the createMetaModel method that builds the structure of possible LanguageUnitTypes
 *
 *  @author wilke
 */

package Composestar.Core.LOLA.metamodel;

import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;

import Composestar.Core.Exception.ModuleException;


public abstract class LanguageModel
{
  protected Hashtable languageUnitTypes; // Hashtable of unitTypeName=>LanguageUnitType
  //protected Hashtable languageUnitTypesByName; // Hashtable of typeName=>LanguageUnitType 
  protected Hashtable relationPredicates; // Hashtable of relPredName=>RelationPredicate

  public LanguageModel()
  {
    languageUnitTypes = new Hashtable();
    //languageUnitTypesByName = new Hashtable();
    relationPredicates = new Hashtable();
  }
  
  /**
   * @param type a registered LanguageUnit implementation class (e.g. one that implements the LanguageUnit interface) 
   * @return The LangagueUnitType of which type is the implementation
   * @throws ModelClashException, when the specified type is not a registered LanguageUnitType.
   */
  public LanguageUnitType getLanguageUnitType(String type) throws ModelClashException
  {
    if (null == languageUnitTypes.get(type))
      throw new ModelClashException("The type " + type + " is not a registered LanguageUnitType");
    return (LanguageUnitType)languageUnitTypes.get(type);		
  }
  
  /*public LanguageUnitType getLanguageUnitTypeByName(String name) throws ModelClashException
  {
    if (null == languageUnitTypesByName.get(name))
      throw new ModelClashException("There exists no LanguageUnitType with name: " + name);
    return (LanguageUnitType)languageUnitTypesByName.get(name);
  }*/
  
  public void addLanguageUnitType(LanguageUnitType ut)
  {
	  languageUnitTypes.put(ut.getType(), ut);
	  //languageUnitTypesByName.put(ut.getPredicateName(), ut);
  }
  
  public void addRelationPredicate(RelationPredicate rp)
  {
    relationPredicates.put(rp.getPredicateName(), rp);
  }
  
  public RelationPredicate getRelationPredicate(String relName) throws ModelClashException
  {
    if (null == relationPredicates.get(relName))
      throw new ModelClashException("The relation " + relName + " is not a registered relation predicate");
    return (RelationPredicate)relationPredicates.get(relName);
  }
  
  /**
   * This method should be called after all language units have registered themselves.
   * Depending on the language, some actions might have to be taken to assure that
   * the model is complete.
   * For example, in the .NET model Namespaces will not register themselves, so this method
   * can add them.
   * This is kindof a 'hacked-up' solution, but most models will need some adjustment between
   * regestering the units and actually running the query engine.
   *  
   * @param dict, a dictionary of registered Language Units
   * @throws ModelClashException, when 'impossible' conditions are encountered
   */
  public abstract void completeModel(UnitDictionary dict) throws ModelClashException;
  
  /**
   * This method will create an index of the units in collection, leaving out those that
   * should not really be in the set of units.
   * 
   * @param units Set of units that should be indexed for faster processing
   * @param dict Unitdictionary object that will be used for faster lookups by the prolog engine
   */
  public abstract void createIndex(Collection units, UnitDictionary dict) throws ModuleException;
  
  public abstract void createMetaModel() throws InvalidModelException;
  
  /**
   * @return Returns the languageUnitTypes.
   */
  public Collection getLanguageUnitTypes()
  {
    return languageUnitTypes.values();
  }
  
  /**
   * @param languageUnitTypes The languageUnitTypes to set.
   */
  public void setLanguageUnitTypes(Hashtable languageUnitTypes)
  {
    this.languageUnitTypes = languageUnitTypes;
  }
  
  /**
   * @return Returns the relationPredicates.
   */
  public Hashtable getRelationPredicates()
  {
    return relationPredicates;
  }
  
  /**
   * @param relationPredicates The relationPredicates to set.
   */
  public void setRelationPredicates(Hashtable relationPredicates)
  {
    this.relationPredicates = relationPredicates;
  }
  
  /**
   *  Returns path of unitrelations between two languageunits 
   * @param from Type of languageunit 
   * @param to Type of languageunit
   */
  public abstract HashMap getPathOfUnitRelations(String from,String to);
  
  //public abstract static LanguageModel instance();
  
}
