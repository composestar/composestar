/*
 * Represents a Predicate Selector element in the composestar tree.
 * 
 * Connects the selector to the prolog engine; i.e. this class also contains the
 * code that interprets the predicate and returns a set of Language Units
 * 
 * Created on Nov 3, 2004 by wilke
 */
package Composestar.Core.CpsProgramRepository.CpsConcern.SuperImposition.SimpleSelectorDef;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import tarau.jinni.Clause;
import tarau.jinni.Const;
import tarau.jinni.Fun;
import tarau.jinni.JavaObject;
import tarau.jinni.Prog;
import tarau.jinni.PrologErrorState;
import tarau.jinni.Term;
import Composestar.Core.CpsProgramRepository.PrimitiveConcern;
import Composestar.Core.CpsProgramRepository.CpsConcern.CpsConcern;
import Composestar.Core.CpsProgramRepository.CpsConcern.Implementation.CompiledImplementation;
import Composestar.Core.CpsProgramRepository.CpsConcern.Implementation.Source;
import Composestar.Core.CpsProgramRepository.CpsConcern.References.ConcernReference;
import Composestar.Core.CpsProgramRepository.CpsConcern.References.ProgramElementReference;
import Composestar.Core.CpsProgramRepository.CpsConcern.References.Reference;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.LAMA.*;
import Composestar.Core.LOLA.connector.ComposestarBuiltins;
import Composestar.Core.LOLA.metamodel.ModelClashException;
import Composestar.Core.INCRE.INCRE;
import Composestar.Core.INCRE.INCRETimer;
import Composestar.Core.INCRE.MethodNode;
import Composestar.Core.RepositoryImplementation.DataStore;

import Composestar.Utils.*;

public class PredicateSelector extends SimpleSelExpression
{
  String outputVar;
  String query;
  
  Vector selectedUnitRefs; // contains references to selected units
  HashSet selectedUnits; // Contains set of selected language units

  boolean toBeCheckedByINCRE; // whether INCRE needs to check this
  HashMap TYMInfo; // contains TYM information extracted while executing query
  HashSet annotations; // contains the names of annotations extracted while executing query
  
  public PredicateSelector(String outputVar, String query)
  {
    super();
    this.outputVar = outputVar;
    this.query = query;
    selectedUnitRefs = new Vector();
    selectedUnits = new HashSet();
    toBeCheckedByINCRE = false;
    TYMInfo = new HashMap();
    annotations = new HashSet();
    Debug.out(Debug.MODE_DEBUG, "LOLA", "Creating a predicate selector(" + outputVar + ", " + query + ')');
  }
  
  public PredicateSelector()
  {
    this(null, null);
  }
  
  /* (non-Javadoc)
   * @see Composestar.Core.CpsProgramRepository.CpsConcern.SuperImposition.SimpleSelectorDef.SimpleSelExpression#interpret()
   */
  public Vector interpret()
  {
    return selectedUnitRefs;
  }
  
  /**
   * Interprets the predicate and stores the answers to be returned by interpret()  
   */
  public void run() throws ModuleException
  {
  	INCRE incre = INCRE.instance();
	INCRETimer executequery = incre.getReporter().openProcess("LOLA",query,INCRETimer.TYPE_NORMAL);	
	
	// tell ComposestarBuiltins that we are executing this selector
	ComposestarBuiltins.currentSelector = this;
	
    //Debug.out(Debug.MODE_DEBUG, "LOLA", "Interpret a predicate selector(" + outputVar + ", " + query + ")");
    Vector result = new Vector();
    HashSet resultSet = new HashSet();

    Clause prologGoal=Clause.goalFromString(query);
    Vector answers = evaluateGoal(prologGoal, outputVar);
    if (PrologErrorState.SUCCESS != PrologErrorState.getCode())
      throw new ModuleException("During predicate evaluation: " + PrologErrorState.getMessage(), "LOLA", this);
    
    // Ensure that list of answers does not contain duplicates, the lame way....
    HashSet uniqAnswers = new HashSet();
    for (int i = 0; i < answers.size(); i++)
    	uniqAnswers.add(answers.elementAt(i));
    
    for (Iterator iter = uniqAnswers.iterator(); iter.hasNext();)
    {
	  Object element = iter.next();
      if (!(element instanceof ProgramElement))
        Debug.out(Debug.MODE_WARNING, "LOLA", "Error: the output variable '" + outputVar + "' does not return (only) Language Units for this query:\n" + query, this);
      else
      {
    	  ProgramElement unit = (ProgramElement)element;
          Reference ref;
          ref = resolveUnit(unit);
          result.add(ref);
          resultSet.add(unit);      
      }
    }
    
    this.selectedUnitRefs = result; 
    this.selectedUnits = resultSet;
    
    // finish type information
    this.finishTypeInfo();
    executequery.stop();
  }

  /**
   * @return Returns the outputVar.
   */
  public String getOutputVar()
  {
    return outputVar;
  }
  
  /**
   * @param outputVar The outputVar to set.
   */
  public void setOutputVar(String outputVar)
  {
    this.outputVar = outputVar;
  }
  
  /**
   * @return Returns the query.
   */
  public String getQuery()
  {
    return query;
  }
  
  /**
   * @param predicate The query to set.
   */
  public void setQuery(String query)
  {
    this.query = query;
  }

  /**
   * @return set of selected program elements (the real units, not refs).
   * Only valid after 'run' has been called to execute the actual selector predicate.
   */
  public Set getSelectedUnits()
  {
  	return selectedUnits;
  }
  
  /**
   * @param b boolean whether this selector needs to be checked by INCRE
   */
  public void setToBeCheckedByINCRE(boolean b){
  	this.toBeCheckedByINCRE = b;   	
  }
  
  public boolean getToBeCheckedByINCRE(){
  	return this.toBeCheckedByINCRE;   	
  }
  
  public HashMap getTYMInfo(){
  	return this.TYMInfo;
  }
  
  public HashSet getAnnotations(){
  	return this.annotations;
  }
  
  /**
   * @param Goal - a predicate to be evaluated (containing answerVar as an unbound variable)
   * @param answerVar - the result variable that you're interested in, should become bound to JavaObject(s)!
   * @return A vector of values found for the answerVar (containing the real java Objects, e.g. dereferenced JavaObjects)
   */
  private Vector evaluateGoal(Clause Goal, String answerVar) throws ModuleException
  {
    Clause NamedGoal=Goal.cnumbervars(false);
    Term Names=NamedGoal.getHead();
    Vector answers = new Vector();
    int answerVarPos = -1;

    // First check wether the answer variable occurs at all in this prolog expression
    // Names should be a prolog Term containing a list of the variables that occur in Goal
    if (Names instanceof Fun)
    {
      for (int j = 0; j < Names.getArity(); j++)
      {
        if (answerVar.equals(((Fun)Names).getArg(j).toString()))
        { // We found our answervariable, which is cool
          answerVarPos = j;
          break;
        }
      }
    }
    else // if the Clause is not a function (as it should), probably the parser could
         // not convert the predicate into a clause because it contains a syntax error.
    {
      Term errorTerm = NamedGoal.getBody();
      if (errorTerm instanceof Fun)
      {
        Fun errorFun = (Fun)errorTerm;
        if (errorFun.name().equals("error") && errorFun.getArg(0) instanceof Const && errorFun.getArg(1) instanceof Const)
        { // it's an error/3-predicate, so let's pry the error message out of this error function
          String errorType = ((Const)errorFun.getArg(0)).name();
          String errorMsg = ((Const)errorFun.getArg(1)).name();
          // the 3rd argument contains the line number, which we ignore for now.
          throw new ModuleException("Error evaluating predicate: " + errorType + ": " + errorMsg, "LOLA", this);
        }
      }
    }
      
    if (answerVarPos < 0)
    { // The expression does not contain the answerVar; or maybe it does not even contain variables; this is bad.
      throw new ModuleException("Error: The expression does not contain the requested answer variable " + answerVar, "LOLA", this);
    }
    
    // Execute the prolog query and collect the results
    toBeCheckedByINCRE = true; // a valid query at this point
    Prog E=new Prog(Goal,null);

    Term r=Prog.ask_engine(E);
    while(r != null){ // While there are  answers
      Fun varBindings = (Fun)r.numbervars();
      Term answer = varBindings.getArg(answerVarPos);
      if (answer instanceof JavaObject){
        answers.add(answer.toObject());
      }
      else{
        Debug.out(Debug.MODE_ERROR, "LOLA", "Internal error: Query should, but did not return a java object!", this);
      }
      r=Prog.ask_engine(E);
    }
    
    if(answers.isEmpty())
    	toBeCheckedByINCRE = false; // no answers, do not skip this selector
    
    if (answers.size() >= 50000) // Arbitrary number...what would be reasonable to expect?
      Debug.out(Debug.MODE_WARNING, "LOLA", "Over 50k results; maybe this prolog expression generates infinite results:\n" + query, this);
    return answers;
 }
  
  public void finishTypeInfo(){
  	
  	// look at current type info and search for relations.
  	// Correct the 'dead links' to those relations
  	Iterator typesItr = TYMInfo.keySet().iterator();
  	try {
  		String classType = ComposestarBuiltins.currentLangModel.getLanguageUnitType("Class").getImplementingClass().getName();
  		while(typesItr.hasNext()){
  			String keyType = (String)typesItr.next();
  			HashMap relations = ComposestarBuiltins.currentLangModel.getPathOfUnitRelations(classType,keyType);
    		if(relations!=null){
    			Iterator keys = relations.keySet().iterator();
    			while(keys.hasNext()){
    				String key = (String)keys.next();
    				Object obj = relations.get(key);
    				addTYMInfo(key,(MethodNode)obj);
    			}
    		}
    		else // relations cannot be found
    			this.toBeCheckedByINCRE = false;
  		}
  	}
  	catch(ModelClashException mce){
    	Debug.out(Debug.MODE_WARNING, "LOLA", "Error while finishing type information:  "+mce.getMessage());
    }
  	
  	
  	
  	// add tym information according to type of answer
    // Currently only queries with DotNETTypes as answer supported by INCRE
  	// Because of current repository design
  	// LanguageUnits like methods and parameters cannot be found without using UnitDictionary
  	// We would like to skip the initialization of the Dictionary
  	// Also not able to find methods and parameters by ID or by a reference
  	/*
    if(!selectedUnits.isEmpty()){
    	try {
    		String classType = ComposestarBuiltins.currentLangModel.getLanguageUnitType("Class").getImplementingClass().getName();
    		String answerType = selectedUnits.toArray()[0].getClass().getName();
    		HashMap relations = ComposestarBuiltins.currentLangModel.getPathOfUnitRelations(classType,answerType);
    		if(relations!=null){
    			Iterator keys = relations.keySet().iterator();
    			while(keys.hasNext()){
    				String key = (String)keys.next();
    				Object obj = relations.get(key);
    				System.out.println("Add tym info: "+key+":"+((MethodNode)obj).getObjectRef());
    				addTYMInfo(key,obj);
    			}
    		}
    		else // relations cannot be found
    			this.toBeCheckedByINCRE = false;	
    	}
    	catch(ModelClashException mce){
      		Debug.out(Debug.MODE_WARNING, "LOLA", "Error while adding TYM information according to type of answer:  "+mce.getMessage());
      	}
    }*/
  }
  
  /**
   * @param key - FullName of a class
   * @param obj - Type information represented FieldNode,MethodNode or Path
   */
  public void addTYMInfo(String key, MethodNode method){
  		
  		HashMap list = new HashMap();
		if(this.TYMInfo.containsKey(key))
			list = (HashMap)TYMInfo.get(key);
				
		list.put(method.getReference(),method);
		this.TYMInfo.put(key,list);
  }
  
  /**
   * @param type - Type of TYM information: 'Class','Method'...
   * @param methodname - Name of method
   */
  public void addTYMInfo(String type, String methodname){
	
  	try {
  		String fullname = ComposestarBuiltins.currentLangModel.getLanguageUnitType(type).getImplementingClass().getName();
  		MethodNode method = new MethodNode(methodname);
  		this.addTYMInfo(fullname,method);
  	}
  	catch(ModelClashException mce){
  		Debug.out(Debug.MODE_WARNING, "LOLA", "Cannot add TYM information of type "+type);
  	}
  } 
  
  /**
   * @param type - Type of TYM information: 'Class','Method'...
   * @param methodname - Name of method
   * @param params - List of method parameters
   */
  public void addTYMInfo(String type, String methodname,ArrayList params){
  	try {
  		String fullname = ComposestarBuiltins.currentLangModel.getLanguageUnitType(type).getImplementingClass().getName();
  		MethodNode method = new MethodNode(methodname);
  		method.setParameters(params);
  		this.addTYMInfo(fullname,method);
  	}
  	catch(ModelClashException mce){
  		Debug.out(Debug.MODE_WARNING, "LOLA", "Cannot add TYM information of type "+type);
  	}
  } 
  
  /**
   * @param unit - a bounded LanguageUnit
   * @param methodname - Name of a method
   */
  public void addTYMInfo(ProgramElement unit, String methodname){
	
  	if(unit.getUnitType().equals("Annotation")){
  		//add annotation to the set of annotations  
  		this.annotations.add(unit.getUnitName());
  	}
  	
  	String fullname = unit.getClass().getName();
  	//String type = unit.getUnitType();
  	MethodNode method = new MethodNode(methodname);
  	this.addTYMInfo(fullname,method);
  } 
  
  /**
   * @param unit - a bounded LanguageUnit
   * @param methodname - Name of a method
   * @param params - List of method parameters
   */
  public void addTYMInfo(ProgramElement unit, String methodname,ArrayList params){
	
  	if(unit.getUnitType().equals("Annotation")){
  		//add annotation to the set of annotations  
  		this.annotations.add(unit.getUnitName());
  	}
  	
  	String fullname = unit.getClass().getName();
  	//String type = unit.getUnitType();
  	MethodNode method = new MethodNode(methodname);
  	method.setParameters(params);
  	this.addTYMInfo(fullname,method);
  } 
  
  public Reference resolveUnit(ProgramElement unit){
  
  	Reference ref;
  	if (unit instanceof Type)
    {
  		Type type = (Type)unit;
        ConcernReference concernRef = new ConcernReference();
        concernRef.setRef(type.getParentConcern());
        concernRef.setName(type.getParentConcern().getQualifiedName());
        concernRef.setResolved(true);
        ref = concernRef;
    }
    else
    {
    	ProgramElementReference elemRef = new ProgramElementReference();
    	elemRef.setRef(unit);
    	elemRef.setResolved(true);
    	ref = elemRef;
    	this.toBeCheckedByINCRE = false;
    }
  	return ref;
  }
  
  public boolean resolveAnswers(){
  	
    Vector resolvedSelectedUnitRefs = new Vector(); 
  	HashSet resolvedSelectedUnits = new HashSet();
  	boolean answersResolved = true;
  	
  	// iterate over answers
  	Iterator units = this.selectedUnits.iterator();
  	while(units.hasNext()){
  		
  		// find unit in current repository
  		ProgramElement unit = (ProgramElement)units.next();
  		ProgramElement unit2 = null;
  		boolean unitfound = false;
  		
  		Object obj = DataStore.instance().getObjectByID(unit.getUnitName());
  		  		
  		if(obj instanceof PrimitiveConcern){
  			PrimitiveConcern pc = (PrimitiveConcern)obj;
  			unit2 = (ProgramElement)pc.platformRepr;
  		}
  		else if(obj instanceof CpsConcern){
  			HashMap typeMap = TypeMap.instance().map();
  			CpsConcern concern = (CpsConcern)obj;
  			Object impl = concern.getImplementation();
    		String className = "";
			
    		if( impl instanceof Source )
			{ 
				Source source = (Source)impl;
				className = source.getClassName();
			}
    		else if( impl instanceof CompiledImplementation )
				className = ((CompiledImplementation)impl).getClassName();
			
    		unit2 = (ProgramElement)typeMap.get(className);
  		}
    		
  		if(unit2!=null){
  			Reference ref = resolveUnit(unit2);
  			resolvedSelectedUnitRefs.add(ref);
  			resolvedSelectedUnits.add(unit2);
  			unitfound = true;
  		}
  		
  		if(!unitfound){
  			Debug.out(Debug.MODE_WARNING, "LOLA", "Cannot resolve Unit "+unit.getUnitName());
  			answersResolved = false;
  		}
  		
  	}	
  	  	
  	this.selectedUnitRefs = resolvedSelectedUnitRefs;
  	this.selectedUnits = resolvedSelectedUnits;
  	return answersResolved;
  	
  } 

}
