/*
 * Created on Oct 25, 2004
 *
 * DotNET implementation of the prolog language model.
 * 
 * @author wilke
 */
package Composestar.C.LOLA.metamodel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import Composestar.C.LAMA.*;
import Composestar.Core.INCRE.MethodNode;
import Composestar.Utils.Debug;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.LAMA.ProgramElement;
import Composestar.Core.LAMA.UnitResult;
import Composestar.Core.LOLA.metamodel.InvalidModelException;
import Composestar.Core.LOLA.metamodel.LanguageModel;
import Composestar.Core.LOLA.metamodel.LanguageUnitType;
import Composestar.Core.LOLA.metamodel.ModelClashException;
import Composestar.Core.LOLA.metamodel.RelationPredicate;
import Composestar.Core.LOLA.metamodel.RelationType;
import Composestar.Core.LOLA.metamodel.UnitDictionary;

public class CLanguageModel extends LanguageModel
{
	static CLanguageModel instance;

	public static CLanguageModel instance()
	{
	  if (null == instance)
		instance = new CLanguageModel();
	  return instance;
	}
	
	public CLanguageModel()
	{
	  super();
	}
	
	/**
	 * Creates specifications for all language units that can occur in .NET,
	 * ant the relations between them
	 */

	public void createMetaModel()  throws InvalidModelException
	{
		try {	
		  	  Class typeImpl = Class.forName("Composestar.Core.LAMA.Type");
			  Class fileImpl = Class.forName("Composestar.Core.LAMA.Type");
			  Class methodImpl = Class.forName("Composestar.Core.LAMA.MethodInfo");	
			  Class fieldImpl = Class.forName("Composestar.Core.LAMA.FieldInfo");	
			  Class parameterImpl = Class.forName("Composestar.Core.LAMA.ParameterInfo");	
			  Class annotationImpl = Class.forName("Composestar.Core.LAMA.Type");
			 // Class basictypeImpl = Class.forName("Composestar.C.LAMA.CBasicType");
			  
			  /********* Definition of unit types ***********/ 
			  // Define the 'Type' composite language unit type (Type = Class | Interface)
			  LanguageUnitType utType = new LanguageUnitType(typeImpl,"Type" , false);
			  addLanguageUnitType(utType);
			  
			  LanguageUnitType utFile = new LanguageUnitType(fileImpl,"Class" , false);
			  addLanguageUnitType(utFile);
			  
			  // Define the 'Method' language unit type
			  LanguageUnitType utMethod = new LanguageUnitType(methodImpl,"Method", true);
			  addLanguageUnitType(utMethod);
			  
			  // Define the 'Field' language unit type
			  LanguageUnitType utField = new LanguageUnitType(fieldImpl,"Field", false);
			  addLanguageUnitType(utField);	  
			  
			  // Define the 'Parameter' language unit type
			  LanguageUnitType utParameter = new LanguageUnitType(parameterImpl,"Parameter", false);
			  addLanguageUnitType(utParameter);
			  
			  // Define the 'Annotation' language unit type
			  LanguageUnitType utAnnotation = new LanguageUnitType(annotationImpl,"Annotation", true);
			  addLanguageUnitType(utAnnotation);
			  
			 // LanguageUnitType utBType = new LanguageUnitType(basictypeImpl,"BasicType" , false);
			 // addLanguageUnitType(utBType);
			 
			  /********* Definition of unit relations *********/
			  
			  /**Annotation**/
			  RelationType annotationAttachedMethods = new RelationType("AttachedMethods", utMethod, RelationType.MULTIPLE);
			  utAnnotation.addRelationType(annotationAttachedMethods);	  
			  		  
			  /** Class **/
			  
			  RelationType fileChildMethods = new RelationType("ChildMethods", utMethod, RelationType.MULTIPLE);
			  utFile.addRelationType(fileChildMethods);
			  
			  RelationType fileChildFields = new RelationType("ChildFields", utField, RelationType.MULTIPLE);
			  utFile.addRelationType(fileChildFields);
			
			  /** Method **/
			  RelationType methodParentClass = new RelationType("ParentClass", utFile, RelationType.UNIQUE);
			  utMethod.addRelationType(methodParentClass);
		  
			  RelationType methodChildParameters = new RelationType("ChildParameters", utParameter, RelationType.MULTIPLE);
			  utMethod.addRelationType(methodChildParameters);
		
			  RelationType methodReturnType = new RelationType("ReturnClass", utType, RelationType.UNIQUE);
			  utMethod.addRelationType(methodReturnType);
			
			  RelationType methodAnnotations = new RelationType("Annotations", utAnnotation, RelationType.MULTIPLE);
			  utMethod.addRelationType(methodAnnotations);	  
			  			  
			  /** Field **/
			  RelationType fieldFile = new RelationType("Class", utFile, RelationType.UNIQUE);
			  utField.addRelationType(fieldFile);
			  
			  RelationType fieldParentMethod = new RelationType("FieldParentMethod", utMethod, RelationType.UNIQUE);
			  utMethod.addRelationType(fieldParentMethod);
			  
			  RelationType fieldType = new RelationType("fieldType", utType, RelationType.UNIQUE);
			  utMethod.addRelationType(fieldType);
			  
		
			  /** Parameter **/
			  RelationType parameterParentMethod = new RelationType("ParentMethod", utMethod, RelationType.UNIQUE);
			  utParameter.addRelationType(parameterParentMethod);
		
			  /********* Definition of relation predicates *****/
			  
			  /** Class **/
				  
			  RelationPredicate classHasMethod = 
			    new RelationPredicate("classHasMethod", fileChildMethods, "Class",
			                                            methodParentClass, "Method");	  
			  addRelationPredicate(classHasMethod);
			   
			  RelationPredicate classHasField = 
			    new RelationPredicate("classHasField", fileChildFields, "Class",
			                                           fieldFile, "Field");	  
			  addRelationPredicate(classHasField);
			  
			  /** Method **/
			  RelationPredicate methodHasParameter = 
			    new RelationPredicate("methodHasParameter", methodChildParameters, "Method",
			                                                parameterParentMethod, "Parameter");	  
			  addRelationPredicate(methodHasParameter);  
			  
			  RelationPredicate methodHasAnnotation = 
				    new RelationPredicate("methodHasAnnotation", methodAnnotations, "Method",
				                                            	annotationAttachedMethods, "Annotation");	  
			  addRelationPredicate(methodHasAnnotation);	  
				  
	  }
	  catch(ClassNotFoundException cnfe){
	  	throw new InvalidModelException("Exception occured during creation of meta model: "+cnfe.getMessage());
	  }
	}
	
	public void completeModel(UnitDictionary unitDict) throws ModelClashException
	{		
      /* Create missing Language Unit links for Classes */
      //UnitResult classes = unitDict.getByType("Class");
     /**
      if (null != classes)
      {
	    Iterator classIter = classes.multiValue().iterator();
		while (classIter.hasNext())
		{
		  System.out.println("LOLA.metamodel.Clanguagemodel.completemodel() adapted class");
		  
		  Type concern = (Type)classIter.next();
		 
		  LangNamespace ns = rootNS;
		  if ((null != concern.namespace()) && (!concern.namespace().equals("")))
		    ns = findOrAddNamespace(unitDict, rootNS, concern.namespace());
		  ns.addChildClass(concern);
		  concern.setParentNamespace(ns);
		    
		  ProgramElement superType = concern.getUnitRelation("ParentClass").singleValue();
		  if (null != superType) // The concern has a registered superType
		    ((DotNETType)superType).addChildType(concern); // So also add the link the other way round.
		    
		  Collection implementedInterfaces = concern.getUnitRelation("Implements").multiValue();
		  Iterator ifaceIter = implementedInterfaces.iterator();
		  while (ifaceIter.hasNext())
		  { // This class implements interfaces, also add the reverse mapping from interface -> classes that implement it
		    DotNETType iface = (DotNETType)ifaceIter.next();
		    iface.addImplementedBy(concern);
		  }
		}
      }**/
   	  
      /* Create missing Language Unit links for Parameters */
      UnitResult parameters = unitDict.getByType("Parameter");
      if (null != parameters)
      {
	  	  Iterator paramIter = parameters.multiValue().iterator();
		  while (paramIter.hasNext())
		  {
		    CParameterInfo param = (CParameterInfo)paramIter.next();
		    if (param.parameterType() != null)
		    {
		    	ProgramElement paramType = param.getUnitRelation(param.parameterType().getUnitType()).singleValue();
			    if ((null != paramType) && (paramType instanceof CParameterInfo)) // The parameter has a registered Type
			    	System.out.println("Composestar.C.LOLA.METAMODEL>CLANGUAGEMODEL.Completemodel() missing add parametertype");
			    	//((CParameterInfo)paramType).addParameterType(param); // So also add the link the other way round.
		    }
		  }        
      }

	  /* Create missing Language Unit links for Methods */
      UnitResult methods = unitDict.getByType("Method");
      if (null != methods)
      {
	  	  Iterator methodIter = methods.multiValue().iterator();
		  while (methodIter.hasNext())
		  {
		    CMethodInfo method = (CMethodInfo)methodIter.next();
		    if (method.returnType() != null)
		    {
		    	ProgramElement methodReturnType = method.getUnitRelation("Return" + method.returnType().getUnitType()).singleValue();
		      if ((null != methodReturnType) && (methodReturnType instanceof CMethodInfo)) // The method has a registered return Type
		    	  System.out.println("Composestar.C.LOLA.METAMODEL>CLANGUAGEMODEL.Completemodel() missing add methodtype");
		    	 //((CMethodInfo)methodReturnType).addMethodReturnType(method); // So also add the link the other way round.
		    }
		  }        
      }

	  /* Create missing Language Unit links for Fields */
      UnitResult fields = unitDict.getByType("Field");
      if (null != fields)
      {
	  	  Iterator fieldIter = fields.multiValue().iterator();
		  while (fieldIter.hasNext())
		  {
		    CVariable field = (CVariable)fieldIter.next();
		    if (null != field.fieldType())
		    {
		    	ProgramElement fieldType = field.getUnitRelation(field.fieldType().getUnitType()).singleValue();
		      if ((null != fieldType) && (fieldType instanceof CVariable)) // The method has a registered return Type
		    	  System.out.println("Composestar.C.LOLA.METAMODEL>CLANGUAGEMODEL.Completemodel() missing add fieldtype");
		    	  //((Type)fieldType).addFieldType(field); // So also add the link the other way round.
		    }
		  }        
      }	  
	}

	/**
	 * Create index on the language units. This index is also known as the UnitDictionary.
	 *  All units in the given collection will be added to the dictionary, except:
	 *   - Fields that are inherited from a parent type
	 *   - Methods that are inherited from a parent type
	 *   - Parameters that belong to a method that is inherited from a parent type.
	 * @param units - the collection of units to be indexed
	 * @param dict - the resulting unitdictionary object (should be initialized!)
	 * 
	 * Note: this is kindof ugly, but has to be done because we reuse repository
	 * objects as the fact base for selector expressions. However, the repository needs 
	 * inherited methods to be visible, while in the selector expressions we don't want that.
	 */
	public void createIndex(Collection units, UnitDictionary dict) throws ModuleException
	{
	    // Loop 1: find methods, add only those that are ImplementedHere
	    Iterator unitIter = units.iterator();
	    while (unitIter.hasNext())
	    {
	      ProgramElement unit = (ProgramElement)unitIter.next();
	      Debug.out(Debug.MODE_WARNING, "CLanguagemodel", "Including: " + unit.getUnitType() + " " + unit.getUnitName());
		  dict.addLanguageUnit(unit);
	    }
	}
	
	public static void consistentyCheck(UnitDictionary dict) throws ModelClashException
	{
	  // Check whether all references to other langunits exist, stuff like that	
	}
	
	public static void modelComplianceCheck(UnitDictionary dict) throws ModelClashException
	{
		// Check whether the specified relations can indeed exist and stuf like that;
		// whether unique names are indeed unique etc. etc.
	}
	
	/**
	 * @param from type of languageunit
	 * @param to type of languageunit
	 * Returns path of unitrelations between two languageunits
	 * e.g 
	 * from DotNETType to DotNETMethodInfo: 
	 * 		- getUnitRelation(ChildMethods)
	 * from DotNETType to DotNETFieldInfo: 
	 * 		- DotNETType.getUnitRelation(ChildMethods)
	 * 		- DotNETMethodInfo.getUnitRelation(ChildFields) 
	 * Returns null if path cannot be found
	 */
	public HashMap getPathOfUnitRelations(String from,String to){
	 	HashMap relations = new HashMap();  
	  	ArrayList params = new ArrayList();
	  	MethodNode m = new MethodNode("getUnitRelation");
	  	
	  	try {
	  		if(from.equals(to)) // empty path
	  			return relations;
	  		else if(from.equals(getLanguageUnitType("Class").getImplementingClass().getName())){
	  			if(to.equals(getLanguageUnitType("Method").getImplementingClass().getName())){
	  				// From Class to Method
	  				// Directly with relation ChildMethods
	  				params.add("ChildMethods");
	  				m.setParameters(params);
	  				relations.put(from,m);
	  				return relations;
	  			}
	  			else if(to.equals(getLanguageUnitType("Parameter").getImplementingClass().getName())){
	  				// From Class to Parameter
	  				// Class -> Method -> Parameter
	  				// By realtions ChildMethods and ChildParameters
	  				params.add("ChildMethods");
	  				m.setParameters(params);
	  				relations.put(from,m);
	  				
	  				params.clear();
	  				params.add("ChildParameters");
	  				m.setParameters(params);
	  				relations.put(getLanguageUnitType("Method").getImplementingClass().getName(),m);
	  				return relations;	
	  			}
	  			else {
	  				//TODO add more cases 
	  			}
	  		}
	  	}	
	  	catch(ModelClashException mce){
	  		Debug.out(Debug.MODE_WARNING, "LOLA", "Unable to generate map of relations from "+from+" to "+to+": "+mce.getMessage());
	  	}
	  	
	  	return null;
	}
	
	/********************************* private helper methods **************************/
	
  /**
   * Because namespaces do not exist as 'standalone' language units as such, we create the hierarchy
   * from the list of full class names.
   * This method will try to find, and if needed add, the 'namespace' (e.g. 'com.myproject.gui.widgets') hierarchy
   * to the specified unitDictionary.
   * 
   * It starts at a root namespace 'rootNS', and checks whether it has a child namespace that matches the
   * first part of the specified namespace (e.g. 'com'). 
   * As long as this is the case the loop will add another part of the namespace (e.g. com.myproject).
   * 
   * If at some point the namespace doesn't match to an existing one anymore, the first part of the non-matching part
   * is created and added to 'dict' (e.g. 'com.myproject.gui' is created and added as child of 'com.myproject').
   * Then the next part is added as a child of that one, etc.
   * 
   * Be careful you really understand what's happening before you change this thing (which should not be needed anyway ;)  
   */
 /**
  public LangNamespace findOrAddNamespace(UnitDictionary dict, LangNamespace rootNS, String namespace)
  {
    LangNamespace currNS = rootNS;
    boolean lostMatch = false;
    while (!lostMatch && !namespace.equals(currNS.getUnitName()))
    {
      lostMatch = true; // assume we are not going to find a child that matches
      UnitResult children = currNS.getUnitRelation("ChildNamespaces");
      Iterator childIter = children.multiValue().iterator();
      while (childIter.hasNext())
      {
        LangNamespace ns = (LangNamespace)childIter.next();
        if (namespace.startsWith(ns.getUnitName() + '.') || namespace.equals(ns.getUnitName()))
        { // we found a matching child! Update the current node and try another iteration
          currNS = ns;
          lostMatch = false;  
          break;
        }
      } 
      
      // Did not match at some point? If so we'll have to add the remainder of the namespace.
      if (lostMatch)
      {
        try
        {
          // Determine which part of the namespace is still left to add (the 'remainder').
          int parentLen = currNS.getUnitName().length();
          String nsToAdd = namespace.substring( parentLen > 0 ?  parentLen + 1 : 0);
          
          // While there are more parts, determine where this one ends, and add it to the dictionary.
          int pos = nsToAdd.indexOf('.');
          if (pos < 0)
            pos = nsToAdd.length();
          while (pos > 0)
          {
            String subnsName = currNS.getUnitName() + (currNS.getUnitName().length() > 0 ? "." : "") + nsToAdd.substring(0,pos);
            LangNamespace subns = new LangNamespace(subnsName);
            dict.addLanguageUnit(subns);
            currNS.addChildNamespace(subns);
            subns.setParentNamespace(currNS);
            if (pos < nsToAdd.length())
            {
              nsToAdd = nsToAdd.substring(pos+1);
              pos = nsToAdd.indexOf('.');
              if (pos < 0)
                pos = nsToAdd.length();
            }
            else
              pos = -1;
            currNS = subns;
          }
        }
        catch (IndexOutOfBoundsException e)
        { // if you get this error, you're in real trouble.
          Debug.out(Debug.MODE_WARNING, "LOLA", "Internal error: " + e.getMessage());
          e.printStackTrace();
        }
        catch (ModuleException e)
        {
          System.err.println("Internal error: " + e.getMessage());
          e.printStackTrace();
        }
      }
    }
    return currNS;
  }    **/
}
