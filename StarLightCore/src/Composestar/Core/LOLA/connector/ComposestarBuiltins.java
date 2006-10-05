/*
 * Builtin predicates. These are a kind of 'plugins' to the Prolog engine.
 * They are used by the Language model predicates to get information from Java.
 * 
 * ComposestarBuiltins.java - Created on 6-aug-2004 by havingaw
 */

package Composestar.Core.LOLA.connector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import tarau.jinni.Const;
import tarau.jinni.FunBuiltin;
import tarau.jinni.HashDict;
import tarau.jinni.IO;
import tarau.jinni.JavaObject;
import tarau.jinni.Prog;
import tarau.jinni.Source;
import tarau.jinni.Term;
import Composestar.Core.LAMA.ProgramElement;
import Composestar.Core.LAMA.UnitResult;
import Composestar.Core.LOLA.metamodel.CompositeRelationPredicate;
import Composestar.Core.LOLA.metamodel.LanguageModel;
import Composestar.Core.LOLA.metamodel.RelationPredicate;
import Composestar.Core.LOLA.metamodel.UnitDictionary;
import Composestar.Core.CpsProgramRepository.CpsConcern.SuperImposition.SimpleSelectorDef.PredicateSelector;

/**
 * @author havingaw
 *
 * This class implements builtin predicates used by the composestar Prolog metamodel 
 */
public class ComposestarBuiltins extends HashDict
{
  /* Public for convenience - should be readable by builtin-classes */
  public static UnitDictionary langUnits; 
  
  public static PredicateSelector currentSelector;
  public static LanguageModel currentLangModel;
  
  public static void setUnitDictionary(UnitDictionary dict)
  {
    langUnits = dict; 
  }
    
  public ComposestarBuiltins(LanguageModel langModel)
  {
    super();
    /* Register the unitTypeBuiltin and unitNameBuiltin predicates */
    register(new isUnitTypeBuiltin());
    register(new isUnitNameBuiltin());
    register(new hasAttributeBuiltin());
    register(new matchPattern());

    /* Register the relation predicates for this language model */
    Iterator relIterator = langModel.getRelationPredicates().values().iterator();
    while (relIterator.hasNext())
    {
      RelationPredicate rp = (RelationPredicate)relIterator.next();
      if (!(rp instanceof CompositeRelationPredicate))
        register(new binaryRelationBuiltin(rp));
    }
    
    currentLangModel = langModel;
    
  }
  
  /**
  registers a symbol as name of a builtin
  */
  public void register(Const proto)
  {
	String key=proto.name()+ '/' +proto.getArity();
	put(key,proto);
  }
}

/**
 * Implements the isUnitTypeBuiltin(Unit, TypeString).
 * This predicate has 2 arguments:
 *  Unit, a JavaObject containing a LanguageUnit
 *  TypeString, the FQN of the class that implements this kind of LanguageUnit.
 * The first argument can be bound or unbound. If it is unbound, a UnitSource will
 * be returned that generates all units of the specified type.
 * The second argument *must* be bound, or the predicate will always fail.  
 */
class isUnitTypeBuiltin extends FunBuiltin
{
  isUnitTypeBuiltin()
  {
    super("isUnitTypeBuiltin", 2);
  }
  
  public int exec(Prog p)
  {
    Term tUnit = getArg(0);
    Term tType = getArg(1);
    if (!(tType instanceof Const)) /* We don't support this for now */
    {
      IO.errmes("isUnitTypeBuiltin: 2nd argument 'Type' should be bound!");
      return 0; /* Failed */
    }
    String type = ((Const)tType).name();
    if (tUnit instanceof JavaObject) /* Easy case: the unit is specified */
    {
      if (!(tUnit.toObject() instanceof ProgramElement))
        return 0; // It's not even a language unit!
      // Check whether the unit is of the specified type; return true or false based on the result
      ProgramElement unit = (ProgramElement)tUnit.toObject();
      // add type information for incremental process
      ComposestarBuiltins.currentSelector.addTYMInfo(unit,"getUnitName");
      return unit.getUnitType().equals(type) ? 1 : 0;
    }
    else /* Unit not specified, so we return a source containing the list of units */
    {
      UnitResult unitsOfThisType = ComposestarBuiltins.langUnits.getByType(type);
      if (null == unitsOfThisType) // Type does not exist!
      {
        IO.errmes("internal error: unit type does not exist: " + type);
        return 0;
      }
      // add type information for incremental process
      ComposestarBuiltins.currentSelector.addTYMInfo(type,"getUnitName");
      return putArg(0, new UnitSource(p, unitsOfThisType.multiValue()), p);
    }
  }
}

/**
 * A Source of Language Units. Initialized by a Collection of Language Units,
 * this Source will return these Units one by one, wrapped in a JavaObject that
 * can be handed back to prolog.
 */
class UnitSource extends Source
{
  private Iterator i;
  
  UnitSource(Prog p, Collection c)
  {
    super(p);
    this.i = c.iterator(); 
  }
  
  public void stop()
  {
    this.i = null;
  }
  
  public Term getElement()
  {
    if (i.hasNext())
      return new JavaObject(i.next());
    else
      return null; /* End of list */
  }
}

/**
 * A Source of Strings.
 * This Source will return Strings in the specified collection one by one, wrapped as
 * a constant prolog Term.
 */
class StringSource extends Source
{
  private Iterator i;
  
  /**
   * @param p Prolog program
   * @param c A collection that contains only strings
   */
  StringSource(Prog p, Collection c)
  {
    super(p);
    this.i = c.iterator(); 
  }
  
  public void stop()
  {
    this.i = null;
  }
  
  public Term getElement()
  {
    if (i.hasNext())
      return new Const((String)i.next());
    else
      return null; /* End of list */
  }  
}

/**
 * Implement binary relations between Language Units.
 */
class binaryRelationBuiltin extends FunBuiltin
{
  String name;
  String relation1, relation2;
  boolean relation1unique, relation2unique;
  int arg0, arg1;

  public binaryRelationBuiltin(RelationPredicate relation)
  {
    super(relation.getPredicateName() + "Builtin", 2);
    this.name = relation.getPredicateName() + "Builtin";
    this.relation1 = relation.getRelationType(1).getName();
    this.relation2 = relation.getRelationType(2).getName();
    this.relation1unique = relation.getRelationType(1).isUnique();
    this.relation2unique = relation.getRelationType(2).isUnique();
    if (relation2unique && !relation1unique)
    { // In this case the arguments are swapped, so that we can use check_or_gen1 (faster)
      // instead of check_or_gen2. Also see ModelGenerator (!)
      this.arg0 = 1;
      this.arg1 = 0;
    }
    else
    {
      this.arg0 = 0;
      this.arg1 = 1;
    }
  }
  
  public binaryRelationBuiltin(String name,
	   						   String type1, String relation1, boolean relation1unique,
	                           String type2, String relation2, boolean relation2unique)
  {
    super(name, 2);
  	this.name = name;
  	this.relation1 = relation1;
  	this.relation2 = relation2;
  	this.relation1unique = relation1unique;
  	this.relation2unique = relation2unique;
  }
  
  public binaryRelationBuiltin(String name, 
  							   String relation1, boolean relation1unique,
							   String relation2, boolean relation2unique)
  {
  	this(name, "", relation1, relation1unique, "", relation2, relation2unique);
  }
  
  private ProgramElement termToUnit(Term t)
  {
    if (t.toObject() instanceof ProgramElement)
      return (ProgramElement)t.toObject();
   return null;
  }
  
  /**
   * This method executes the predicate logic. It is quite long because there are many
   * cases:
   * - Both the first and second argument can be bound or unbound (4 possibilities)
   *   If both are bound, the method checks whether a relation really exists between these units (fast&easy)
   *   If only one is bound, the method will return a Source of 'related' units in the unbound argument.
   *   If both are unbound, it fails. This case should not occur, because if this is what you want, a generator
   *   predicate should bind one of the arguments first (in prolog).
   * - Relations can be one-to-one, one-to-many, many-to-one, or many-to-many (again 4 possibilities).
   *    If one side is bound and the other side is 'unique', the other side can be directly bound to that value,
   *    instead of creating a Source. This is way faster. If the other side is not unique a Source will be created
   *    for it. The generator predicates (in prolog) have to check which case happened.
   * 
   * The good thing is: this part may be a bit complicated, but it handles *all* types of possible binary relation
   * predicates in one generic implementation ;)   
   */
  public int exec(Prog p)
  {
    Term term1 = getArg(arg0);
    Term term2 = getArg(arg1);
    if (term1 instanceof JavaObject)
    {
      if (null == termToUnit(term1))
      {
        IO.errmes(this.name + ": Error: Argument 1 is not a language unit");
        return 0;
      }
      ProgramElement unit1 = termToUnit(term1);
      
      if (term2 instanceof JavaObject)
      { // Both terms are bound!
        if (null == termToUnit(term2))
        {
          IO.errmes(this.name + ": Error: Argument 1 is not a language unit");
          return 0;          
        }
        ProgramElement unit2 = termToUnit(term2);

        // unit1 bound, unit2 bound
        // add type information for incremental process
        ArrayList params1 = new ArrayList();
        ArrayList params2 = new ArrayList();
        params1.add(relation1);
        params2.add(relation2);
        ComposestarBuiltins.currentSelector.addTYMInfo(unit1,"getUnitRelation",params1);
  	  	ComposestarBuiltins.currentSelector.addTYMInfo(unit2,"getUnitRelation",params2);
  	  	
        // If one of the relations is unique, use that side (faster)
        if (relation1unique && (unit1.getUnitRelation(relation1).singleValue() != null))
          return (unit1.getUnitRelation(relation1).singleValue().equals(unit2) ? 1 : 0);
        else if (relation2unique && (unit2.getUnitRelation(relation2).singleValue() != null))
          return (unit2.getUnitRelation(relation2).singleValue().equals(unit1) ? 1 : 0);
        else // many-to-many relation, check whether unit1 contains unit2 in the relation set
          return (unit1.getUnitRelation(relation1).multiValue().contains(unit2) ? 1 : 0);
        
      }
      else
      { // term1 is bound, term2 is not
        
      	// add type information for incremental process
        ArrayList params = new ArrayList();
        params.add(relation1);
  	  	ComposestarBuiltins.currentSelector.addTYMInfo(unit1,"getUnitRelation",params); 
  	  	
      	UnitResult otherside = unit1.getUnitRelation(relation1);
        if (null == otherside)
          IO.errmes(this.name + ": Error: Relation type does not exist: " + relation1);
        else if (relation1unique && otherside.isSingleValue())
        {
          if (otherside.singleValue() != null)
            return putArg(arg1, new JavaObject(otherside.singleValue()), p);
        }
        else if (!relation1unique && otherside.isMultiValue())
          return putArg(arg1, new UnitSource(p, otherside.multiValue()), p);
        else
          IO.errmes(this.name + ": Error: Relation multiplicity violation.");
      }
    }
    else if (term2 instanceof JavaObject)// term1 is unbound, term2 is bound
    {
      if (null == termToUnit(term2))
      {
        IO.errmes(this.name + ": Error: Argument 2 is not a language unit");
        return 0;          
      }
      ProgramElement unit2 = termToUnit(term2);
      
      // add type information for incremental process
      ArrayList params = new ArrayList();
      params.add(relation2);
	  ComposestarBuiltins.currentSelector.addTYMInfo(unit2,"getUnitRelation",params);
	  
      UnitResult otherside = unit2.getUnitRelation(relation2);
      if (null == otherside)
        IO.errmes(this.name + ": Error: Relation type does not exist: " + relation1);
      else if (relation2unique && otherside.isSingleValue())
      {
        if (otherside.singleValue() != null)
          return putArg(arg0, new JavaObject(otherside.singleValue()), p);
      }
      else if (!relation2unique && otherside.isMultiValue())
        return putArg(arg0, new UnitSource(p, otherside.multiValue()), p);
      else
        IO.errmes(this.name + ": Error: Relation multiplicity violation.");
    }
    else
      IO.errmes(this.name + ": At least one of the arguments must be bound!");
    // Both terms are unbound, no relations where found or an error was detected.
    // Default is to return 'fail'. Under normal circumstances this statement should not be
    // executed so you can set a breakpoint on it to warn you if it is reached ;)
    return 0;    
  }
}

/**
 * Unit name builtin predicate. Implements the isUnitNameBuiltin(Unit, Name, Type) predicate.
 * 
 * At least one of Unit and Name must be bound in the current version. The Type argument is
 * optional - when it is specified (recommended!) only units of the corresponding type and Name will
 * be selected.
 * 
 * When the Unit is bound, its Name will be bound directly.
 * When the Name is bound, there are 2 possibilies:
 *  - When the specified Type is a unitType with unique names, the Unit will be bound directly
 *  - If the Type is unspecified, or does not have unique names, a UnitSource will be returned.
 */

class isUnitNameBuiltin extends FunBuiltin
{
  isUnitNameBuiltin()
  {
    super("isUnitNameBuiltin", 3);
  }
  
  public int exec(Prog p)
  {
    Term tType = getArg(2);
    boolean knownType = true;
    String type = "";
    if (!(tType instanceof Const)){
      ComposestarBuiltins.currentSelector.setToBeCheckedByINCRE(false);// too many cases
      knownType = false; // Unit can be of any type; lookup will be slower
    }
    else
      type = ((Const)tType).name(); // Only look for units of this type
      
    Term tUnit = getArg(0);
    Term tName = getArg(1);
    if (tName instanceof Const) // Name is bound - lookup by unit name
    {
      String name = ((Const)tName).name();
      UnitResult result;
      if (knownType)
        result = ComposestarBuiltins.langUnits.getByName(name, type);
      else
        result = ComposestarBuiltins.langUnits.getByName(name);
      if (null == result)
        return 0; // No unit exists by this name
      if (tUnit instanceof JavaObject) // Unit is bound - check whether we have found the same unit
      {
      	// add type information for incremental process
        ComposestarBuiltins.currentSelector.addTYMInfo((ProgramElement)tUnit.toObject(),"getUnitName");
        
        if (result.isSingleValue())
          return (result.singleValue().equals(tUnit.toObject())) ? 1 : 0;
        else // Multiple values found
          return (result.multiValue().contains(tUnit.toObject())) ? 1 : 0;
      }
      else // Bind the uid to the unit found by name.
      {
      	// add type information for incremental process
        if(knownType)
        	ComposestarBuiltins.currentSelector.addTYMInfo(type,"getUnitName");
        
        if (result.isSingleValue())
          return putArg(0, new JavaObject(result.singleValue()), p);
        else
          return putArg(0, new UnitSource(p, result.multiValue()), p);
      }
    } 
    else
    { // Name is unbound - look it up by UID
      if (!(tUnit instanceof JavaObject))
      {
        IO.errmes("isUnitNameBuiltin: at least one of Unit and Name must be bound!");
        return 0;
      }
      if (!(tUnit.toObject() instanceof ProgramElement))
        return 0; // Invalid object; not a Language Unit.
      ProgramElement unit = (ProgramElement)tUnit.toObject();
      if (knownType && !(unit.getUnitType().equals(type)))
        return 0; // It is a unit, but not of the specified type! So this predicate fails.
      
      // add type information for incremental process
      ComposestarBuiltins.currentSelector.addTYMInfo(unit,"getUnitName");
      
      return putArg(1, new Const(unit.getUnitName()), p); // we found the unit and return its name
    }
  }
}
  
class hasAttributeBuiltin extends FunBuiltin
{
  hasAttributeBuiltin()
  {
    super("hasAttributeBuiltin", 3);
  }
  
  public int exec(Prog p)
  {
    Term tType = getArg(2);
    boolean knownType = true;
    String type = "";
    if (!(tType instanceof Const)){
      ComposestarBuiltins.currentSelector.setToBeCheckedByINCRE(false);// too many cases		
      knownType = false; // Unit can be of any type; lookup will be slower
    }
    else
      type = ((Const)tType).name(); // Only look for units of this type
      
    Term tUnit = getArg(0);
    Term tAttr = getArg(1);
    if (tAttr instanceof Const) // Attribute is bound - lookup units with this attr 
    {
      String attr = ((Const)tAttr).name();
      UnitResult result;
      if (knownType)
        result = ComposestarBuiltins.langUnits.getByType(type);
      else
        result = ComposestarBuiltins.langUnits.getAll();
      if (null == result)
        return 0; // No unit exists!?
      HashSet filtered = new HashSet();
      Iterator allIter = result.multiValue().iterator();
      while (allIter.hasNext())
      {
    	  ProgramElement unit = (ProgramElement)allIter.next();
        if (unit.hasUnitAttribute(attr))
          filtered.add(unit);
      }
      
      ArrayList params = new ArrayList();
	  params.add(attr);
		
      if (tUnit instanceof JavaObject){ // Unit is bound - check whether we have found the same unit
      	if((tUnit.toObject() instanceof ProgramElement)){
      	 	if(knownType){
      	 		// Add type information for incremental process
      	 		ProgramElement unit = (ProgramElement)tUnit.toObject();
      			ComposestarBuiltins.currentSelector.addTYMInfo(unit,"hasUnitAttribute",params);      	
      		}	
      	}
      	return filtered.contains(tUnit.toObject()) ? 1 : 0;
      }
      else { // Bind the uid to the units found.
      	if(knownType){
  	 		// Attribute bound, unit unbound, type bound
      		// Add type information for incremental process
  	 		ComposestarBuiltins.currentSelector.addTYMInfo(type,"hasUnitAttribute",params);      	
  		}	
      	return putArg(0, new UnitSource(p, filtered), p);
      }  
    } 
    else
    { // Attribute is unbound - generate list of attrs for this thing.
      if (!(tUnit instanceof JavaObject))
      {
        IO.errmes("hasAttributeBuiltin: at least one of Unit and Name must be bound!");
        return 0;
      }
      if (!(tUnit.toObject() instanceof ProgramElement))
        return 0; // Invalid object; not a Language Unit.
      ProgramElement unit = (ProgramElement)tUnit.toObject();
      if (knownType && !(unit.getUnitType().equals(type)))
        return 0; // It is a unit, but not of the specified type! So this predicate fails.
      
      // Attribute unbound, unit bound, type bound
      // Add type information for incremental process
      ComposestarBuiltins.currentSelector.addTYMInfo(unit,"getUnitAttributes");
      return putArg(1, new StringSource(p, unit.getUnitAttributes()), p); // we found the unit, return its attributes
    }
  }
}

/**
 * Implements the matchPattern(Str, Pattern) builtin predicate.
 * This predicate has 2 arguments:
 *  Str, a normal (string) constant
 *  Pattern, a string containing a Perl Regular Expression
 * Both arguments must be bound.
 * matchPatttern return 'true' if Str matches Pattern.
 */
class matchPattern extends FunBuiltin
{
  matchPattern()
  {
    super("matchPattern", 2);
  }
  
  public int exec(Prog p)
  {
  	// not incremental yet
  	ComposestarBuiltins.currentSelector.setToBeCheckedByINCRE(false);
  	
    Term tStr = getArg(0);
    Term tPattern = getArg(1);
    if (tStr instanceof Const && tPattern instanceof Const)
    {
    	String str = ((Const)tStr).name();
    	String pattern = ((Const)tPattern).name();
    	/* Why does Java have to make simple stuff like this so complicated!? */
    	Pattern pat = Pattern.compile(pattern);
    	Matcher match = pat.matcher(str);
    	return (match.matches() ? 1 : 0);
    }
    else
      IO.errmes("matchPattern: both arguments should be bound!");
    return 0; /* Failed */
  }
}
