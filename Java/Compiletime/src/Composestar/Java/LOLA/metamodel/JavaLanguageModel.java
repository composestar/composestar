/*
 * Created on Feb 28, 2006
 *
 * Java implementation of the prolog language model.
 * 
 * @author roy_
 */
package Composestar.Java.LOLA.metamodel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import Composestar.Core.Exception.ModuleException;
import Composestar.Core.INCRE.MethodNode;
import Composestar.Core.LAMA.LangNamespace;
import Composestar.Core.LAMA.ProgramElement;
import Composestar.Core.LAMA.UnitResult;
import Composestar.Core.LOLA.metamodel.CompositeLanguageUnitType;
import Composestar.Core.LOLA.metamodel.CompositeRelationPredicate;
import Composestar.Core.LOLA.metamodel.InvalidModelException;
import Composestar.Core.LOLA.metamodel.LanguageModel;
import Composestar.Core.LOLA.metamodel.LanguageUnitType;
import Composestar.Core.LOLA.metamodel.ModelClashException;
import Composestar.Core.LOLA.metamodel.RelationPredicate;
import Composestar.Core.LOLA.metamodel.RelationType;
import Composestar.Core.LOLA.metamodel.UnitDictionary;
import Composestar.Java.LAMA.*;
import Composestar.Utils.Debug;

/**
 * Java implementation of the prolog language model. 
 */
public class JavaLanguageModel extends LanguageModel
{
	static JavaLanguageModel instance;

	/**
	 * Returns an instance of <code>JavaLanguageModel</code>.
	 */
	public static JavaLanguageModel instance()
	{
		if (null == instance) 
		{
			instance = new JavaLanguageModel();
		}
		return instance;
	}

	/**
	 * Constructor. 
	 */
	public JavaLanguageModel()
	{
		super();
	}

	/**
	 * Creates specifications for all language units that can occur in java, and
	 * the relations between them.
	 */
	public void createMetaModel() throws InvalidModelException
	{
		try
		{
			Class namespaceImpl = Class.forName("Composestar.Core.LAMA.LangNamespace");
			Class classImpl = Class.forName("Composestar.Java.LAMA.JavaType");
			Class interfaceImpl = Class.forName("Composestar.Java.LAMA.JavaType");
			Class typeImpl = Class.forName("Composestar.Java.LAMA.JavaType");
			Class methodImpl = Class.forName("Composestar.Java.LAMA.JavaMethodInfo");
			Class fieldImpl = Class.forName("Composestar.Java.LAMA.JavaFieldInfo");
			Class parameterImpl = Class.forName("Composestar.Java.LAMA.JavaParameterInfo");
			Class annotationImpl = Class.forName("Composestar.Java.LAMA.JavaType");

			/** ******* Definition of unit types ********** */
			// Define the 'Namespace' language unit type
			LanguageUnitType utNamespace = new LanguageUnitType(namespaceImpl, "Namespace", true);
			addLanguageUnitType(utNamespace);

			// Define the 'Class' language unit type
			LanguageUnitType utClass = new LanguageUnitType(classImpl, "Class", true);
			addLanguageUnitType(utClass);

			// Define the 'Interface' language unit type
			LanguageUnitType utInterface = new LanguageUnitType(interfaceImpl, "Interface", true);
			addLanguageUnitType(utInterface);

			// Define the 'Type' composite language unit type (Type = Class | Interface)
			CompositeLanguageUnitType utType = new CompositeLanguageUnitType(typeImpl, "Type", true, utClass,
					utInterface);
			addLanguageUnitType(utType);

			// Define the 'Method' language unit type
			LanguageUnitType utMethod = new LanguageUnitType(methodImpl, "Method", false);
			addLanguageUnitType(utMethod);

			// Define the 'Field' language unit type
			LanguageUnitType utField = new LanguageUnitType(fieldImpl, "Field", false);
			addLanguageUnitType(utField);

			// Define the 'Parameter' language unit type
			LanguageUnitType utParameter = new LanguageUnitType(parameterImpl, "Parameter", false);
			addLanguageUnitType(utParameter);

			// Define the 'Annotation' language unit type
			LanguageUnitType utAnnotation = new LanguageUnitType(annotationImpl, "Annotation", true);
			addLanguageUnitType(utAnnotation);

			/** ******* Definition of unit relations ******** */

			/** Annotation * */
			RelationType annotationAttachedClasses = new RelationType("AttachedClasses", utClass, RelationType.MULTIPLE);
			utAnnotation.addRelationType(annotationAttachedClasses);

			RelationType annotationAttachedInterfaces = new RelationType("AttachedInterfaces", utInterface,
					RelationType.MULTIPLE);
			utAnnotation.addRelationType(annotationAttachedInterfaces);

			RelationType annotationAttachedMethods = new RelationType("AttachedMethods", utMethod,
					RelationType.MULTIPLE);
			utAnnotation.addRelationType(annotationAttachedMethods);

			RelationType annotationAttachedFields = new RelationType("AttachedFields", utField, RelationType.MULTIPLE);
			utAnnotation.addRelationType(annotationAttachedFields);

			RelationType annotationAttachedParameters = new RelationType("AttachedParameters", utField,
					RelationType.MULTIPLE);
			utAnnotation.addRelationType(annotationAttachedParameters);

			/** Class * */
			RelationType classParentNamespace = new RelationType("ParentNamespace", utNamespace, RelationType.UNIQUE);
			utClass.addRelationType(classParentNamespace);

			RelationType classSubClasses = new RelationType("ChildClasses", utClass, RelationType.MULTIPLE);
			utClass.addRelationType(classSubClasses);

			RelationType classParentClass = new RelationType("ParentClass", utClass, RelationType.UNIQUE);
			utClass.addRelationType(classParentClass);

			RelationType classChildMethods = new RelationType("ChildMethods", utMethod, RelationType.MULTIPLE);
			utClass.addRelationType(classChildMethods);

			RelationType classChildFields = new RelationType("ChildFields", utField, RelationType.MULTIPLE);
			utClass.addRelationType(classChildFields);

			RelationType classParameterClass = new RelationType("ParameterClass", utParameter, RelationType.MULTIPLE);
			utClass.addRelationType(classParameterClass);

			RelationType classMethodReturnClass = new RelationType("MethodReturnClass", utMethod, RelationType.MULTIPLE);
			utClass.addRelationType(classMethodReturnClass);

			RelationType classFieldClass = new RelationType("FieldClass", utField, RelationType.MULTIPLE);
			utClass.addRelationType(classFieldClass);

			RelationType classImplements = new RelationType("Implements", utInterface, RelationType.MULTIPLE);
			utClass.addRelationType(classImplements);

			RelationType classAnnotations = new RelationType("Annotations", utAnnotation, RelationType.MULTIPLE);
			utClass.addRelationType(classAnnotations);

			/** Interface * */
			RelationType interfaceParentNamespace = new RelationType("ParentNamespace", utNamespace,
					RelationType.UNIQUE);
			utInterface.addRelationType(interfaceParentNamespace);

			RelationType interfaceSubInterfaces = new RelationType("ChildInterfaces", utInterface,
					RelationType.MULTIPLE);
			utInterface.addRelationType(interfaceSubInterfaces);

			RelationType interfaceParentInterface = new RelationType("ParentInterface", utInterface,
					RelationType.UNIQUE);
			utInterface.addRelationType(interfaceParentInterface);

			RelationType interfaceChildMethods = new RelationType("ChildMethods", utMethod, RelationType.MULTIPLE);
			utInterface.addRelationType(interfaceChildMethods);

			RelationType interfaceImplementedBy = new RelationType("ImplementedBy", utClass, RelationType.MULTIPLE);
			utInterface.addRelationType(interfaceImplementedBy);

			/*
			 * RelationType InterfaceChildFields = new
			 * RelationType("ChildFields", utField, RelationType.MULTIPLE);
			 * utInterface.addRelationType(InterfaceChildFields); Interface does
			 * not have fields
			 */

			RelationType interfaceParameterInterface = new RelationType("ParameterInterface", utParameter,
					RelationType.MULTIPLE);
			utInterface.addRelationType(interfaceParameterInterface);

			RelationType interfaceMethodReturnInterface = new RelationType("MethodReturnInterface", utMethod,
					RelationType.MULTIPLE);
			utInterface.addRelationType(interfaceMethodReturnInterface);

			RelationType interfaceFieldInterface = new RelationType("FieldInterface", utField, RelationType.MULTIPLE);
			utInterface.addRelationType(interfaceFieldInterface);

			RelationType interfaceAnnotations = new RelationType("Annotations", utAnnotation, RelationType.MULTIPLE);
			utClass.addRelationType(interfaceAnnotations);

			/** Namespace * */
			RelationType namespaceChildClasses = new RelationType("ChildClasses", utClass, RelationType.MULTIPLE);
			utNamespace.addRelationType(namespaceChildClasses);

			RelationType namespaceChildInterfaces = new RelationType("ChildInterfaces", utInterface,
					RelationType.MULTIPLE);
			utNamespace.addRelationType(namespaceChildInterfaces);

			/** Method * */
			RelationType methodParentClass = new RelationType("ParentClass", utClass, RelationType.UNIQUE);
			utMethod.addRelationType(methodParentClass);

			RelationType methodParentInterface = new RelationType("ParentInterface", utInterface, RelationType.UNIQUE);
			utMethod.addRelationType(methodParentInterface);

			RelationType methodChildParameters = new RelationType("ChildParameters", utParameter, RelationType.MULTIPLE);
			utMethod.addRelationType(methodChildParameters);

			RelationType methodReturnClass = new RelationType("ReturnClass", utClass, RelationType.UNIQUE);
			utMethod.addRelationType(methodReturnClass);

			RelationType methodReturnInterface = new RelationType("ReturnInterface", utClass, RelationType.UNIQUE);
			utMethod.addRelationType(methodReturnInterface);

			RelationType methodAnnotations = new RelationType("Annotations", utAnnotation, RelationType.MULTIPLE);
			utMethod.addRelationType(methodAnnotations);

			/** Field * */
			RelationType fieldParentClass = new RelationType("ParentClass", utClass, RelationType.UNIQUE);
			utField.addRelationType(fieldParentClass);

			RelationType fieldClass = new RelationType("Class", utClass, RelationType.UNIQUE);
			utField.addRelationType(fieldClass);

			RelationType fieldInterface = new RelationType("Interface", utInterface, RelationType.UNIQUE);
			utField.addRelationType(fieldInterface);

			RelationType fieldAnnotations = new RelationType("Annotations", utAnnotation, RelationType.MULTIPLE);
			utField.addRelationType(fieldAnnotations);

			/** Parameter * */
			RelationType parameterParentMethod = new RelationType("ParentMethod", utMethod, RelationType.UNIQUE);
			utParameter.addRelationType(parameterParentMethod);

			RelationType parameterClass = new RelationType("Class", utClass, RelationType.UNIQUE);
			utParameter.addRelationType(parameterClass);

			RelationType parameterInterface = new RelationType("Interface", utClass, RelationType.UNIQUE);
			utParameter.addRelationType(parameterInterface);

			RelationType parameterAnnotations = new RelationType("Annotations", utAnnotation, RelationType.MULTIPLE);
			utParameter.addRelationType(parameterAnnotations);

			/** ******* Definition of relation predicates **** */
			/** Namespace * */
			RelationPredicate namespaceHasClass = new RelationPredicate("namespaceHasClass", namespaceChildClasses,
					"Namespace", classParentNamespace, "Class");
			addRelationPredicate(namespaceHasClass);

			RelationPredicate namespaceHasInterface = new RelationPredicate("namespaceHasInterface",
					namespaceChildInterfaces, "Namespace", classParentNamespace, "Interface");
			addRelationPredicate(namespaceHasInterface);

			/** Class/Interface * */

			RelationPredicate isSuperClass = new RelationPredicate("isSuperClass", classSubClasses, "SuperClass",
					classParentClass, "SubClass");
			addRelationPredicate(isSuperClass);

			RelationPredicate isSuperInterface = new RelationPredicate("isSuperInterface", interfaceSubInterfaces,
					"SuperInterface", interfaceParentInterface, "SubInterface");
			addRelationPredicate(isSuperInterface);

			CompositeRelationPredicate isSuperType = new CompositeRelationPredicate("isSuperType", isSuperClass,
					isSuperInterface);
			addRelationPredicate(isSuperType);

			RelationPredicate classImplementsInterface = new RelationPredicate("implements", classImplements, "Class",
					interfaceImplementedBy, "Interface");
			addRelationPredicate(classImplementsInterface);

			RelationPredicate classHasMethod = new RelationPredicate("classHasMethod", classChildMethods, "Class",
					methodParentClass, "Method");
			addRelationPredicate(classHasMethod);

			RelationPredicate interfaceHasMethod = new RelationPredicate("interfaceHasMethod", interfaceChildMethods,
					"Interface", methodParentInterface, "Method");
			addRelationPredicate(interfaceHasMethod);

			RelationPredicate classHasField = new RelationPredicate("classHasField", classChildFields, "Class",
					fieldParentClass, "Field");
			addRelationPredicate(classHasField);

			RelationPredicate classHasAnnotation = new RelationPredicate("classHasAnnotation", classAnnotations,
					"Class", annotationAttachedClasses, "Annotation");
			addRelationPredicate(classHasAnnotation);

			RelationPredicate interfaceHasAnnotation = new RelationPredicate("interfaceHasAnnotation",
					interfaceAnnotations, "Interface", annotationAttachedInterfaces, "Annotation");
			addRelationPredicate(interfaceHasAnnotation);

			CompositeRelationPredicate typeHasAnnotation = new CompositeRelationPredicate("typeHasAnnotation",
					classHasAnnotation, interfaceHasAnnotation);
			addRelationPredicate(typeHasAnnotation);

			/** Method * */
			RelationPredicate methodHasParameter = new RelationPredicate("methodHasParameter", methodChildParameters,
					"Method", parameterParentMethod, "Parameter");
			addRelationPredicate(methodHasParameter);

			RelationPredicate methodHasAnnotation = new RelationPredicate("methodHasAnnotation", methodAnnotations,
					"Method", annotationAttachedMethods, "Annotation");
			addRelationPredicate(methodHasAnnotation);

			RelationPredicate methodReturnClassRel = new RelationPredicate("methodReturnClass", methodReturnClass,
					"Method", classMethodReturnClass, "Class");
			addRelationPredicate(methodReturnClassRel);

			addRelationPredicate(methodReturnClassRel);

			/** Parameter * */
			RelationPredicate parameterClassRel = new RelationPredicate("parameterClass", parameterClass, "Parameter",
					classParameterClass, "Class");
			addRelationPredicate(parameterClassRel);

			addRelationPredicate(parameterClassRel);

			RelationPredicate parameterHasAnnotation = new RelationPredicate("parameterHasAnnotation",
					parameterAnnotations, "Parameter", annotationAttachedParameters, "Annotation");
			addRelationPredicate(parameterHasAnnotation);

			/** Field * */
			RelationPredicate fieldClassRel = new RelationPredicate("fieldClass", fieldClass, "Field", classFieldClass,
					"Class");
			addRelationPredicate(fieldClassRel);

			RelationPredicate fieldInterfaceRel = new RelationPredicate("fieldInterface", fieldInterface, "Field",
					interfaceFieldInterface, "Interface");
			addRelationPredicate(fieldInterfaceRel);

			RelationPredicate fieldHasAnnotation = new RelationPredicate("fieldHasAnnotation", fieldAnnotations,
					"Field", annotationAttachedFields, "Annotation");
			addRelationPredicate(fieldHasAnnotation);

		}
		catch (ClassNotFoundException cnfe)
		{
			throw new InvalidModelException("Exception occured during creation of meta model: " + cnfe.getMessage());
		}
	}

	public void completeModel(UnitDictionary unitDict) throws ModelClashException
	{
		Debug.out(Debug.MODE_DEBUG, "LOLA", "complete java language model..");

		LangNamespace rootNS = new LangNamespace("");
		try
		{
			unitDict.addLanguageUnit(rootNS);
		}
		catch (ModuleException e)
		{
			Debug.out(Debug.MODE_WARNING, "LOLA", e.getMessage());
			return;
		}

		/* Create missing Language Unit links for Classes */
		UnitResult classes = unitDict.getByType("Class");
		if (null != classes)
		{
			Iterator classIter = classes.multiValue().iterator();
            for (Object o : classes.multiValue()) {
                JavaType concern = (JavaType) o;
                LangNamespace ns = rootNS;
                if ((null != concern.namespace()) && (!concern.namespace().equals(""))) {
                    ns = findOrAddNamespace(unitDict, rootNS, concern.namespace());
                }
                ns.addChildClass(concern);
                concern.setParentNamespace(ns);
                ProgramElement superType = concern.getUnitRelation("ParentClass").singleValue();
                if (null != superType) {
                    // The concern has a registered superType
                    // So also add the link the other way round.
                    ((JavaType) superType).addChildType(concern);
                }
                Collection implementedInterfaces = concern.getUnitRelation("Implements").multiValue();
                Iterator ifaceIter = implementedInterfaces.iterator();
                for (Object implementedInterface : implementedInterfaces) {
                    // This class implements interfaces, also add the reverse
                    // mapping from interface -> classes that implement it
                    JavaType iface = (JavaType) implementedInterface;
                    iface.addImplementedBy(concern);
                }
            }
        }

		/* Create missing Language Unit links for Interfaces */
		UnitResult interfaces = unitDict.getByType("Interface");
		if (null != interfaces)
		{
			Iterator ifaceIter = interfaces.multiValue().iterator();
            for (Object o : interfaces.multiValue()) {
                JavaType concern = (JavaType) o;
                LangNamespace ns = rootNS;
                if (!(null == concern.namespace() || !concern.namespace().equals(""))) {
                    ns = findOrAddNamespace(unitDict, rootNS, concern.namespace());
                }
                ns.addChildInterface(concern);
                concern.setParentNamespace(ns);

                ProgramElement superType = concern.getUnitRelation("ParentInterface").singleValue();
                if (null != superType) {
                    // The concern has a registered superType
                    // So also add the link the other way round.
                    ((JavaType) superType).addChildType(concern);
                }
            }
        }

		/* Create missing Language Unit links for Parameters */
		UnitResult parameters = unitDict.getByType("Parameter");
		if (null != parameters)
		{
			Iterator paramIter = parameters.multiValue().iterator();
            for (Object o : parameters.multiValue()) {
                JavaParameterInfo param = (JavaParameterInfo) o;
                if (param.parameterType() != null) {
                    ProgramElement paramType = param.getUnitRelation(param.parameterType().getUnitType()).singleValue();
                    if ((null != paramType) && (paramType instanceof JavaType)) {
                        // The parameter has a registered Type
                        // So also add the link the other way round.
                        ((JavaType) paramType).addParameterType(param);
                    }
                }
            }
        }

		/* Create missing Language Unit links for Methods */
		UnitResult methods = unitDict.getByType("Method");
		if (null != methods)
		{
			Iterator methodIter = methods.multiValue().iterator();
            for (Object o : methods.multiValue()) {
                JavaMethodInfo method = (JavaMethodInfo) o;
                if (method.returnType() != null) {
                    ProgramElement methodReturnType = method.getUnitRelation(
                            "Return" + method.returnType().getUnitType()).singleValue();
                    if ((null != methodReturnType) && (methodReturnType instanceof JavaType)) {
                        // The method has a registered return Type
                        // So also add the link the other way round.
                        ((JavaType) methodReturnType).addMethodReturnType(method);
                    }
                }
            }
        }

		/* Create missing Language Unit links for Fields */
		UnitResult fields = unitDict.getByType("Field");
		if (null != fields)
		{
			Iterator fieldIter = fields.multiValue().iterator();
            for (Object o : fields.multiValue()) {
                JavaFieldInfo field = (JavaFieldInfo) o;
                if (null != field.fieldType()) {
                    ProgramElement fieldType = field.getUnitRelation(field.fieldType().getUnitType()).singleValue();
                    if ((null != fieldType) && (fieldType instanceof JavaType)) {
                        // The method has a registered return Type
                        // So also add the link the other way round.
                        ((JavaType) fieldType).addFieldType(field);
                    }
                }
            }
        }
	}

	/**
	 * Create index on the language units. This index is also known as the
	 * UnitDictionary. All units in the given collection will be added to the
	 * dictionary, except: - Fields that are inherited from a parent type -
	 * Methods that are inherited from a parent type - Parameters that belong to
	 * a method that is inherited from a parent type.
	 * 
	 * @param units - the collection of units to be indexed
	 * @param dict - the resulting unitdictionary object (should be
	 *            initialized!) Note: this is kindof ugly, but has to be done
	 *            because we reuse repository objects as the fact base for
	 *            selector expressions. However, the repository needs inherited
	 *            methods to be visible, while in the selector expressions we
	 *            don't want that.
	 */
	public void createIndex(Collection units, UnitDictionary dict) throws ModuleException
	{
		Debug.out(Debug.MODE_DEBUG, "LOLA", "Creating index on the language units");
		// Loop 1: find methods, add only those that are ImplementedHere
        for (Object unit1 : units) {
            ProgramElement unit = (ProgramElement) unit1;
            if (unit instanceof JavaMethodInfo) {
                JavaMethodInfo method = (JavaMethodInfo) unit;
                if (method.isDeclaredHere()) {
                    dict.addLanguageUnit(unit);
                } else {
                    // Exclude this method because it is inherited; set the
                    // parent of its child parameters
                    // so they will be noticed for removal by the 2nd loop.
                    Collection params = method.getUnitRelation("ChildParameters").multiValue();
                    Iterator paramsIter = params.iterator();
                    for (Object param : params) {
                        JavaParameterInfo paramInfo = (JavaParameterInfo) param;
                        paramInfo.setParent(method); // Have to set this, so we can later skip this unit altogether
                    }
                }
            }
        }
        // Loop 2: add all units, skipping fields that are not ImplementedHere (i.e. inherited).
		// A parameter is skipped when its parent method is not ImplementedHere.
		// Methods are skipped altogether, because they have been added in loop 1 already.
        Iterator unitIter = units.iterator();
        while (unitIter.hasNext())
		{
			ProgramElement unit = (ProgramElement) unitIter.next();
			if (unit instanceof JavaFieldInfo)
			{
				JavaFieldInfo field = (JavaFieldInfo) unit;
				if (field.isDeclaredHere())
				{
					dict.addLanguageUnit(unit);
				}
			}
			else if (unit instanceof JavaParameterInfo)
			{
				JavaParameterInfo param = (JavaParameterInfo) unit;
				if ((null == param.getParent()) || ((JavaMethodInfo) param.getParent()).isDeclaredHere())
				{
					// The parameter does not belong to an inherited method, so add it.
					dict.addLanguageUnit(unit);
				}
			}
			else if (!(unit instanceof JavaMethodInfo))
			{
				// Skip methods, add everything else that hasn't matched so far
				dict.addLanguageUnit(unit);
			}
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
	 * Returns path of unitrelations between two languageunits e.g
	 * <p> - from JavaType to JavaMethodInfo: getUnitRelation(ChildMethods)
	 * <p> - from JavaType to JavaFieldInfo:
	 * JavaType.getUnitRelation(ChildMethods);
	 * JavaMethodInfo.getUnitRelation(ChildFields)
	 * <p>
	 * Returns null if path cannot be found.
	 * 
	 * @param from type of languageunit
	 * @param to type of languageunit
	 */
	public HashMap getPathOfUnitRelations(String from, String to)
	{
		HashMap relations = new HashMap();
		ArrayList params = new ArrayList();
		MethodNode m = new MethodNode("getUnitRelation");

		try
		{
			if (from.equals(to)) // empty path
			return relations;
			else if (from.equals(getLanguageUnitType("Class").getImplementingClass().getName()))
			{
				if (to.equals(getLanguageUnitType("Method").getImplementingClass().getName()))
				{
					// From Class to Method
					// Directly with relation ChildMethods
					params.add("ChildMethods");
					m.setParameters(params);
					relations.put(from, m);
					return relations;
				}
				else if (to.equals(getLanguageUnitType("Parameter").getImplementingClass().getName()))
				{
					// From Class to Parameter
					// Class -> Method -> Parameter
					// By relations ChildMethods and ChildParameters
					params.add("ChildMethods");
					m.setParameters(params);
					relations.put(from, m);

					params.clear();
					params.add("ChildParameters");
					m.setParameters(params);
					relations.put(getLanguageUnitType("Method").getImplementingClass().getName(), m);
					return relations;
				}
				else
				{
					// TODO add more cases
				}
			}
		}
		catch (ModelClashException mce)
		{
			Debug.out(Debug.MODE_WARNING, "LOLA", "Unable to generate map of relations from " + from + " to " + to
					+ ": " + mce.getMessage());
		}

		return null;
	}

	/****** private helper methods ******/

	/**
	 * Because namespaces do not exist as 'standalone' language units as such,
	 * we create the hierarchy from the list of full class names. This method
	 * will try to find, and if needed add, the 'namespace' (e.g.
	 * 'com.myproject.gui.widgets') hierarchy to the specified unitDictionary.
	 * It starts at a root namespace 'rootNS', and checks whether it has a child
	 * namespace that matches the first part of the specified namespace (e.g.
	 * 'com'). As long as this is the case the loop will add another part of the
	 * namespace (e.g. com.myproject). If at some point the namespace doesn't
	 * match to an existing one anymore, the first part of the non-matching part
	 * is created and added to 'dict' (e.g. 'com.myproject.gui' is created and
	 * added as child of 'com.myproject'). Then the next part is added as a
	 * child of that one, etc. Be careful you really understand what's happening
	 * before you change this thing (which should not be needed anyway ;)
	 */
	public LangNamespace findOrAddNamespace(UnitDictionary dict, LangNamespace rootNS, String namespace)
	{
		LangNamespace currNS = rootNS;
		boolean lostMatch = false;
		while (!lostMatch && !namespace.equals(currNS.getUnitName()))
		{
			lostMatch = true; // assume we are not going to find a child that matches
			UnitResult children = currNS.getUnitRelation("ChildNamespaces");
			Iterator childIter = children.multiValue().iterator();
            for (Object o : children.multiValue()) {
                LangNamespace ns = (LangNamespace) o;
                if (namespace.startsWith(ns.getUnitName() + '.') || namespace.equals(ns.getUnitName())) {
                    // we found a matching child! Update the current node and
                    // try another iteration
                    currNS = ns;
                    lostMatch = false;
                    break;
                }
            }

            // Did not match at some point? If so we'll have to add the
			// remainder of the namespace.
			if (lostMatch)
			{
				try
				{
					// Determine which part of the namespace is still left to
					// add (the 'remainder').
					int parentLen = currNS.getUnitName().length();
					String nsToAdd = namespace.substring(parentLen > 0 ? parentLen + 1 : 0);

					// While there are more parts, determine where this one
					// ends, and add it to the dictionary.
					int pos = nsToAdd.indexOf('.');
					if (pos < 0) pos = nsToAdd.length();
					while (pos > 0)
					{
						String subnsName = currNS.getUnitName() + (currNS.getUnitName().length() > 0 ? "." : "")
								+ nsToAdd.substring(0, pos);
						LangNamespace subns = new LangNamespace(subnsName);
						dict.addLanguageUnit(subns);
						currNS.addChildNamespace(subns);
						subns.setParentNamespace(currNS);
						if (pos < nsToAdd.length())
						{
							nsToAdd = nsToAdd.substring(pos + 1);
							pos = nsToAdd.indexOf('.');
							if (pos < 0) pos = nsToAdd.length();
						}
						else pos = -1;
						currNS = subns;
					}
				}
				catch (IndexOutOfBoundsException e)
				{ 
					// if you get this error, you're in real trouble.
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
	}
}
