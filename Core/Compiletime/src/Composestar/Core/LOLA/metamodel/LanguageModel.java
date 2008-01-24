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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import Composestar.Core.Exception.ModuleException;
import Composestar.Core.INCRE.MethodNode;
import Composestar.Core.LAMA.FieldInfo;
import Composestar.Core.LAMA.LangNamespace;
import Composestar.Core.LAMA.MethodInfo;
import Composestar.Core.LAMA.ParameterInfo;
import Composestar.Core.LAMA.ProgramElement;
import Composestar.Core.LAMA.Type;
import Composestar.Core.LAMA.UnitResult;
import Composestar.Core.LOLA.LOLA;
import Composestar.Utils.Logging.CPSLogger;

public abstract class LanguageModel
{
	protected static final CPSLogger logger = CPSLogger.getCPSLogger(LOLA.MODULE_NAME);

	protected Map<String, LanguageUnitType> languageUnitTypes;

	// Hashtable of typeName=>LanguageUnitType
	// protected Hashtable languageUnitTypesByName;

	protected Map<String, RelationPredicate> relationPredicates;

	/**
	 * Wrapper classes for program elements used during creation of the model;
	 */
	protected Class<?> mcNamespace, mcClass, mcInterface, mcType, mcMethod, mcField, mcParameter, mcAnnotation;

	public LanguageModel()
	{
		languageUnitTypes = new HashMap<String, LanguageUnitType>();
		// languageUnitTypesByName = new Hashtable();
		relationPredicates = new HashMap<String, RelationPredicate>();
	}

	/**
	 * @param type a registered LanguageUnit implementation class (e.g. one that
	 *            implements the LanguageUnit interface)
	 * @return The LangagueUnitType of which type is the implementation
	 * @throws ModelClashException, when the specified type is not a registered
	 *             LanguageUnitType.
	 */
	public LanguageUnitType getLanguageUnitType(String type) throws ModelClashException
	{
		if (null == languageUnitTypes.get(type))
		{
			throw new ModelClashException("The type " + type + " is not a registered LanguageUnitType");
		}
		return languageUnitTypes.get(type);
	}

	/*
	 * public LanguageUnitType getLanguageUnitTypeByName(String name) throws
	 * ModelClashException { if (null == languageUnitTypesByName.get(name))
	 * throw new ModelClashException("There exists no LanguageUnitType with
	 * name: " + name); return
	 * (LanguageUnitType)languageUnitTypesByName.get(name); }
	 */

	public void addLanguageUnitType(LanguageUnitType ut)
	{
		languageUnitTypes.put(ut.getType(), ut);
		// languageUnitTypesByName.put(ut.getPredicateName(), ut);
	}

	public void addRelationPredicate(RelationPredicate rp)
	{
		relationPredicates.put(rp.getPredicateName(), rp);
	}

	public RelationPredicate getRelationPredicate(String relName) throws ModelClashException
	{
		if (null == relationPredicates.get(relName))
		{
			throw new ModelClashException("The relation " + relName + " is not a registered relation predicate");
		}
		return relationPredicates.get(relName);
	}

	/**
	 * This method should be called after all language units have registered
	 * themselves. Depending on the language, some actions might have to be
	 * taken to assure that the model is complete. For example, in the .NET
	 * model Namespaces will not register themselves, so this method can add
	 * them. This is kindof a 'hacked-up' solution, but most models will need
	 * some adjustment between regestering the units and actually running the
	 * query engine.
	 * 
	 * @param dict, a dictionary of registered Language Units
	 * @throws ModelClashException, when 'impossible' conditions are encountered
	 */
	public void completeModel(UnitDictionary unitDict) throws ModelClashException
	{
		LangNamespace rootNS = new LangNamespace("");
		try
		{
			unitDict.addLanguageUnit(rootNS);
		}
		catch (ModuleException e)
		{
			logger.warn(e);
			return;
		}

		/* Create missing Language Unit links for Classes */
		UnitResult classes = unitDict.getByType("Class");
		if (null != classes)
		{
			for (Object o : classes.multiValue())
			{
				Type concern = (Type) o;
				LangNamespace ns = rootNS;
				if (null != concern.namespace() && !concern.namespace().equals(""))
				{
					ns = findOrAddNamespace(unitDict, rootNS, concern.namespace());
				}
				ns.addChildClass(concern);
				concern.setParentNamespace(ns);

				ProgramElement superType = concern.getUnitRelation("ParentClass").singleValue();
				if (null != superType)
				{
					// The concern has a registered superType
					// So also add the link the other way round.
					((Type) superType).addChildType(concern);
				}
				Collection<Type> implementedInterfaces = concern.getUnitRelation("Implements").multiValue();
				for (Type implementedInterface : implementedInterfaces)
				{
					// This class implements interfaces, also add the reverse
					// mapping from interface -> classes that implement it
					Type iface = implementedInterface;
					iface.addImplementedBy(concern);
				}
			}
		}

		/* Create missing Language Unit links for Interfaces */
		UnitResult interfaces = unitDict.getByType("Interface");
		if (null != interfaces)
		{
			for (Object o : interfaces.multiValue())
			{
				Type concern = (Type) o;
				LangNamespace ns = rootNS;
				if (!(null == concern.namespace() || !concern.namespace().equals("")))
				{
					ns = findOrAddNamespace(unitDict, rootNS, concern.namespace());
				}
				ns.addChildInterface(concern);
				concern.setParentNamespace(ns);

				ProgramElement superType = concern.getUnitRelation("ParentInterface").singleValue();
				if (null != superType)
				{
					// The concern has a registered superType
					// So also add the link the other way round.
					((Type) superType).addChildType(concern);
				}
			}
		}

		/* Create missing Language Unit links for Parameters */
		UnitResult parameters = unitDict.getByType("Parameter");
		if (null != parameters)
		{
			for (Object o : parameters.multiValue())
			{
				ParameterInfo param = (ParameterInfo) o;
				if (param.parameterType() != null)
				{
					ProgramElement paramType = param.getUnitRelation(param.parameterType().getUnitType()).singleValue();
					if (null != paramType && paramType instanceof Type)
					{
						// The parameter has a registered Type
						// So also add the link the other way round.
						((Type) paramType).addParameterType(param);
					}
				}
			}
		}

		/* Create missing Language Unit links for Methods */
		UnitResult methods = unitDict.getByType("Method");
		if (null != methods)
		{
			for (Object o : methods.multiValue())
			{
				MethodInfo method = (MethodInfo) o;
				if (method.getReturnType() != null)
				{
					ProgramElement methodReturnType = method.getUnitRelation(
							"Return" + method.getReturnType().getUnitType()).singleValue();
					if (null != methodReturnType && methodReturnType instanceof Type)
					{
						// The method has a registered return Type
						// So also add the link the other way round.
						((Type) methodReturnType).addMethodReturnType(method);
					}
				}
			}
		}

		/* Create missing Language Unit links for Fields */
		UnitResult fields = unitDict.getByType("Field");
		if (null != fields)
		{
			for (Object o : fields.multiValue())
			{
				FieldInfo field = (FieldInfo) o;
				if (null != field.getFieldType())
				{
					ProgramElement fieldType = field.getUnitRelation(field.getFieldType().getUnitType()).singleValue();
					if (null != fieldType && fieldType instanceof Type)
					{
						// The method has a registered return Type
						// So also add the link the other way round.
						((Type) fieldType).addFieldType(field);
					}
				}
			}
		}

	}

	/**
	 * This method will create an index of the units in collection, leaving out
	 * those that should not really be in the set of units.
	 * 
	 * @param units Set of units that should be indexed for faster processing
	 * @param dict Unitdictionary object that will be used for faster lookups by
	 *            the prolog engine
	 */
	public void createIndex(Collection<ProgramElement> units, UnitDictionary dict) throws ModuleException
	{
		// Loop 1: find methods, add only those that are ImplementedHere
		for (Object unit1 : units)
		{
			ProgramElement unit = (ProgramElement) unit1;
			if (unit instanceof MethodInfo)
			{
				MethodInfo method = (MethodInfo) unit;
				if (method.isDeclaredHere())
				{
					dict.addLanguageUnit(unit);
				}
				else
				{
					// Exclude this method because it is inherited; set the
					// parent of its child parameters
					// so they will be noticed for removal by the 2nd loop.
					Collection<ParameterInfo> params = method.getUnitRelation("ChildParameters").multiValue();
					for (Object param : params)
					{
						ParameterInfo paramInfo = (ParameterInfo) param;
						paramInfo.setParent(method); // Have to set this, so
						// we can later skip
						// this unit altogether
					}
				}
			}
		}
		// Loop 2: add all units, skipping fields that are not ImplementedHere
		// (i.e. inherited).
		// A parameter is skipped when its parent method is not ImplementedHere.
		// Methods are skipped altogether, because they have been added in loop
		// 1 already.
		Iterator<ProgramElement> unitIter = units.iterator();
		while (unitIter.hasNext())
		{
			ProgramElement unit = unitIter.next();
			if (unit instanceof FieldInfo)
			{
				FieldInfo field = (FieldInfo) unit;
				if (field.isDeclaredHere())
				{
					dict.addLanguageUnit(unit);
				}
			}
			else if (unit instanceof ParameterInfo)
			{
				ParameterInfo param = (ParameterInfo) unit;
				if (null == param.getParent() || (param.getParent()).isDeclaredHere())
				{
					// The parameter does not belong to an inherited method, so
					// add it.
					dict.addLanguageUnit(unit);
				}
			}
			else if (!(unit instanceof MethodInfo))
			{
				// Skip methods, add everything else that hasn't matched so far
				dict.addLanguageUnit(unit);
			}
		}

	}

	/**
	 * Creates specifications for all language units, and the relations between
	 * them.
	 */
	public void createMetaModel() throws InvalidModelException
	{
		/** ******* Definition of unit types ********** */
		// Define the 'Namespace' language unit type
		LanguageUnitType utNamespace = null;
		if (mcNamespace != null)
		{
			utNamespace = new LanguageUnitType(mcNamespace, EUnitType.NAMESPACE, true);
			addLanguageUnitType(utNamespace);
		}

		// Define the 'Class' language unit type
		LanguageUnitType utClass = null;
		if (mcClass != null)
		{
			utClass = new LanguageUnitType(mcClass, EUnitType.CLASS, true);
			addLanguageUnitType(utClass);
		}

		// Define the 'Interface' language unit type
		LanguageUnitType utInterface = null;
		if (mcInterface != null)
		{
			utInterface = new LanguageUnitType(mcInterface, EUnitType.INTERFACE, true);
			addLanguageUnitType(utInterface);
		}

		// Define the 'Type' composite language unit type (Type = Class |
		// Interface)
		LanguageUnitType utType = null;
		if (utClass != null && utInterface != null)
		{
			utType = new CompositeLanguageUnitType(mcType, EUnitType.TYPE, true, utClass, utInterface);
			addLanguageUnitType(utType);
		}
		else
		{
			utType = new LanguageUnitType(mcType, EUnitType.TYPE, true);
			addLanguageUnitType(utType);
		}

		// Define the 'Method' language unit type
		LanguageUnitType utMethod = null;
		if (mcMethod != null)
		{
			utMethod = new LanguageUnitType(mcMethod, EUnitType.METHOD, false);
			addLanguageUnitType(utMethod);
		}

		// Define the 'Field' language unit type
		LanguageUnitType utField = null;
		if (mcField != null)
		{
			utField = new LanguageUnitType(mcField, EUnitType.FIELD, false);
			addLanguageUnitType(utField);
		}

		// Define the 'Parameter' language unit type
		LanguageUnitType utParameter = null;
		if (mcParameter != null)
		{
			utParameter = new LanguageUnitType(mcParameter, EUnitType.PARAMETER, false);
			addLanguageUnitType(utParameter);
		}

		// Define the 'Annotation' language unit type
		LanguageUnitType utAnnotation = null;
		if (mcAnnotation != null)
		{
			utAnnotation = new LanguageUnitType(mcAnnotation, EUnitType.ANNOTATION, true);
			addLanguageUnitType(utAnnotation);
		}

		/** ******* Definition of unit relations ******** */

		/** Annotation * */
		RelationType annotationAttachedClasses = null;
		RelationType annotationAttachedInterfaces = null;
		RelationType annotationAttachedMethods = null;
		RelationType annotationAttachedFields = null;
		RelationType annotationAttachedParameters = null;
		if (utAnnotation != null)
		{
			if (utClass != null)
			{
				annotationAttachedClasses = new RelationType(ERelationType.ATTACHED_CLASSES, utClass,
						RelationType.MULTIPLE);
				utAnnotation.addRelationType(annotationAttachedClasses);
			}

			if (utInterface != null)
			{
				annotationAttachedInterfaces = new RelationType(ERelationType.ATTACHED_INTERFACES, utInterface,
						RelationType.MULTIPLE);
				utAnnotation.addRelationType(annotationAttachedInterfaces);
			}

			if (utMethod != null)
			{
				annotationAttachedMethods = new RelationType(ERelationType.ATTACHED_METHODS, utMethod,
						RelationType.MULTIPLE);
				utAnnotation.addRelationType(annotationAttachedMethods);
			}

			if (utField != null)
			{
				annotationAttachedFields = new RelationType(ERelationType.ATTACHED_FIELDS, utField,
						RelationType.MULTIPLE);
				utAnnotation.addRelationType(annotationAttachedFields);
			}

			if (utField != null)
			{
				annotationAttachedParameters = new RelationType(ERelationType.ATTACHED_PARAMETERS, utField,
						RelationType.MULTIPLE);
				utAnnotation.addRelationType(annotationAttachedParameters);
			}
		}

		/** Class * */
		RelationType classParentNamespace = null;
		RelationType classSubClasses = null;
		RelationType classParentClass = null;
		RelationType classChildMethods = null;
		RelationType classChildFields = null;
		RelationType classParameterClass = null;
		RelationType classMethodReturnClass = null;
		RelationType classFieldClass = null;
		RelationType classImplements = null;
		RelationType classAnnotations = null;
		if (utClass != null)
		{
			if (utNamespace != null)
			{
				classParentNamespace = new RelationType(ERelationType.PARENT_NAMESPACE, utNamespace,
						RelationType.UNIQUE);
				utClass.addRelationType(classParentNamespace);
			}

			classSubClasses = new RelationType(ERelationType.CHILD_CLASSES, utClass, RelationType.MULTIPLE);
			utClass.addRelationType(classSubClasses);

			classParentClass = new RelationType(ERelationType.PARENT_CLASS, utClass, RelationType.UNIQUE);
			utClass.addRelationType(classParentClass);

			if (utMethod != null)
			{
				classChildMethods = new RelationType(ERelationType.CHILD_METHODS, utMethod, RelationType.MULTIPLE);
				utClass.addRelationType(classChildMethods);
			}

			if (utField != null)
			{
				classChildFields = new RelationType(ERelationType.CHILD_FIELDS, utField, RelationType.MULTIPLE);
				utClass.addRelationType(classChildFields);
			}

			if (utParameter != null)
			{
				classParameterClass = new RelationType(ERelationType.PARAMETER_CLASS, utParameter,
						RelationType.MULTIPLE);
				utClass.addRelationType(classParameterClass);
			}

			if (utMethod != null)
			{
				classMethodReturnClass = new RelationType(ERelationType.METHOD_RETURN_CLASS, utMethod,
						RelationType.MULTIPLE);
				utClass.addRelationType(classMethodReturnClass);
			}

			if (utField != null)
			{
				classFieldClass = new RelationType(ERelationType.FIELD_CLASS, utField, RelationType.MULTIPLE);
				utClass.addRelationType(classFieldClass);
			}

			if (utInterface != null)
			{
				classImplements = new RelationType(ERelationType.IMPLEMENTS, utInterface, RelationType.MULTIPLE);
				utClass.addRelationType(classImplements);
			}

			if (utAnnotation != null)
			{
				classAnnotations = new RelationType(ERelationType.ANNOTATIONS, utAnnotation, RelationType.MULTIPLE);
				utClass.addRelationType(classAnnotations);
			}
		}

		/** Interface * */
		RelationType interfaceParentNamespace = null;
		RelationType interfaceSubInterfaces = null;
		RelationType interfaceParentInterface = null;
		RelationType interfaceChildMethods = null;
		RelationType interfaceImplementedBy = null;
		RelationType interfaceParameterInterface = null;
		RelationType interfaceMethodReturnInterface = null;
		RelationType interfaceFieldInterface = null;
		RelationType interfaceAnnotations = null;
		if (utInterface != null)
		{
			if (utNamespace != null)
			{
				interfaceParentNamespace = new RelationType(ERelationType.PARENT_NAMESPACE, utNamespace,
						RelationType.UNIQUE);
				utInterface.addRelationType(interfaceParentNamespace);
			}

			interfaceSubInterfaces = new RelationType(ERelationType.CHILD_INTERFACES, utInterface,
					RelationType.MULTIPLE);
			utInterface.addRelationType(interfaceSubInterfaces);

			interfaceParentInterface = new RelationType(ERelationType.PARENT_INTERFACE, utInterface,
					RelationType.UNIQUE);
			utInterface.addRelationType(interfaceParentInterface);

			if (utMethod != null)
			{
				interfaceChildMethods = new RelationType(ERelationType.CHILD_METHODS, utMethod, RelationType.MULTIPLE);
				utInterface.addRelationType(interfaceChildMethods);
			}

			if (utClass != null)
			{
				interfaceImplementedBy = new RelationType(ERelationType.IMPLEMENTED_BY, utClass, RelationType.MULTIPLE);
				utInterface.addRelationType(interfaceImplementedBy);
			}

			/*
			 * RelationType InterfaceChildFields = new
			 * RelationType("ChildFields", utField, RelationType.MULTIPLE);
			 * utInterface.addRelationType(InterfaceChildFields); Interface does
			 * not have fields
			 */

			if (utParameter != null)
			{
				interfaceParameterInterface = new RelationType(ERelationType.PARAMETER_INTERFACE, utParameter,
						RelationType.MULTIPLE);
				utInterface.addRelationType(interfaceParameterInterface);
			}

			if (utMethod != null)
			{
				interfaceMethodReturnInterface = new RelationType(ERelationType.METHOD_RETURN_INTERFACE, utMethod,
						RelationType.MULTIPLE);
				utInterface.addRelationType(interfaceMethodReturnInterface);
			}

			if (utField != null)
			{
				interfaceFieldInterface = new RelationType(ERelationType.FIELD_INTERFACE, utField,
						RelationType.MULTIPLE);
				utInterface.addRelationType(interfaceFieldInterface);
			}

			if (utAnnotation != null)
			{
				interfaceAnnotations = new RelationType(ERelationType.ANNOTATIONS, utAnnotation, RelationType.MULTIPLE);
				utInterface.addRelationType(interfaceAnnotations);
			}
		}

		/** Namespace * */
		RelationType namespaceChildClasses = null;
		RelationType namespaceChildInterfaces = null;
		if (utNamespace != null)
		{
			if (utClass != null)
			{
				namespaceChildClasses = new RelationType(ERelationType.CHILD_CLASSES, utClass, RelationType.MULTIPLE);
				utNamespace.addRelationType(namespaceChildClasses);
			}

			if (utInterface != null)
			{
				namespaceChildInterfaces = new RelationType(ERelationType.CHILD_INTERFACES, utInterface,
						RelationType.MULTIPLE);
				utNamespace.addRelationType(namespaceChildInterfaces);
			}
		}

		/** Method * */
		RelationType methodParentClass = null;
		RelationType methodParentInterface = null;
		RelationType methodChildParameters = null;
		RelationType methodReturnClass = null;
		RelationType methodReturnInterface = null;
		RelationType methodAnnotations = null;
		if (utMethod != null)
		{
			if (utClass != null)
			{
				methodParentClass = new RelationType(ERelationType.PARENT_CLASS, utClass, RelationType.UNIQUE);
				utMethod.addRelationType(methodParentClass);
			}

			if (utInterface != null)
			{
				methodParentInterface = new RelationType(ERelationType.PARENT_INTERFACE, utInterface,
						RelationType.UNIQUE);
				utMethod.addRelationType(methodParentInterface);
			}

			if (utParameter != null)
			{
				methodChildParameters = new RelationType(ERelationType.CHILD_PARAMETERS, utParameter,
						RelationType.MULTIPLE);
				utMethod.addRelationType(methodChildParameters);
			}

			if (utClass != null)
			{
				methodReturnClass = new RelationType(ERelationType.RETURN_CLASS, utClass, RelationType.UNIQUE);
				utMethod.addRelationType(methodReturnClass);
			}

			if (utInterface != null)
			{
				methodReturnInterface = new RelationType(ERelationType.RETURN_INTERFACE, utInterface,
						RelationType.UNIQUE);
				utMethod.addRelationType(methodReturnInterface);
			}

			if (utAnnotation != null)
			{
				methodAnnotations = new RelationType(ERelationType.ANNOTATIONS, utAnnotation, RelationType.MULTIPLE);
				utMethod.addRelationType(methodAnnotations);
			}
		}

		/** Field * */
		RelationType fieldParentClass = null;
		RelationType fieldClass = null;
		RelationType fieldInterface = null;
		RelationType fieldAnnotations = null;
		if (utField != null)
		{
			if (utClass != null)
			{
				fieldParentClass = new RelationType(ERelationType.PARENT_CLASS, utClass, RelationType.UNIQUE);
				utField.addRelationType(fieldParentClass);
			}

			if (utClass != null)
			{
				fieldClass = new RelationType(ERelationType.CLASS, utClass, RelationType.UNIQUE);
				utField.addRelationType(fieldClass);
			}

			if (utInterface != null)
			{
				fieldInterface = new RelationType(ERelationType.INTERFACE, utInterface, RelationType.UNIQUE);
				utField.addRelationType(fieldInterface);
			}

			if (utAnnotation != null)
			{
				fieldAnnotations = new RelationType(ERelationType.ANNOTATIONS, utAnnotation, RelationType.MULTIPLE);
				utField.addRelationType(fieldAnnotations);
			}
		}

		/** Parameter * */
		RelationType parameterParentMethod = null;
		RelationType parameterClass = null;
		RelationType parameterInterface = null;
		RelationType parameterAnnotations = null;
		if (utParameter != null)
		{
			if (utMethod != null)
			{
				parameterParentMethod = new RelationType(ERelationType.PARENT_METHOD, utMethod, RelationType.UNIQUE);
				utParameter.addRelationType(parameterParentMethod);
			}

			if (utClass != null)
			{
				parameterClass = new RelationType(ERelationType.CLASS, utClass, RelationType.UNIQUE);
				utParameter.addRelationType(parameterClass);
			}

			if (utInterface != null)
			{
				parameterInterface = new RelationType(ERelationType.INTERFACE, utInterface, RelationType.UNIQUE);
				utParameter.addRelationType(parameterInterface);
			}

			if (utAnnotation != null)
			{
				parameterAnnotations = new RelationType(ERelationType.ANNOTATIONS, utAnnotation, RelationType.MULTIPLE);
				utParameter.addRelationType(parameterAnnotations);
			}
		}

		/** ******* Definition of relation predicates **** */

		/** Namespace * */
		if (namespaceChildClasses != null && classParentNamespace != null)
		{
			RelationPredicate namespaceHasClass = new RelationPredicate("namespaceHasClass", namespaceChildClasses,
					"Namespace", classParentNamespace, "Class");
			addRelationPredicate(namespaceHasClass);
		}

		if (namespaceChildInterfaces != null && classParentNamespace != null)
		{
			RelationPredicate namespaceHasInterface = new RelationPredicate("namespaceHasInterface",
					namespaceChildInterfaces, "Namespace", classParentNamespace, "Interface");
			addRelationPredicate(namespaceHasInterface);
		}

		/** Class/Interface * */

		RelationPredicate isSuperClass = null;
		if (classSubClasses != null && classParentClass != null)
		{
			isSuperClass = new RelationPredicate("isSuperClass", classSubClasses, "SuperClass", classParentClass,
					"SubClass");
			addRelationPredicate(isSuperClass);
		}

		RelationPredicate isSuperInterface = null;
		if (interfaceSubInterfaces != null && interfaceParentInterface != null)
		{
			isSuperInterface = new RelationPredicate("isSuperInterface", interfaceSubInterfaces, "SuperInterface",
					interfaceParentInterface, "SubInterface");
			addRelationPredicate(isSuperInterface);
		}

		if (isSuperClass != null && isSuperInterface != null)
		{
			CompositeRelationPredicate isSuperType = new CompositeRelationPredicate("isSuperType", isSuperClass,
					isSuperInterface);
			addRelationPredicate(isSuperType);
		}

		if (classImplements != null && interfaceImplementedBy != null)
		{
			RelationPredicate classImplementsInterface = new RelationPredicate("implements", classImplements, "Class",
					interfaceImplementedBy, "Interface");
			addRelationPredicate(classImplementsInterface);
		}

		if (classChildMethods != null && methodParentClass != null)
		{
			RelationPredicate classHasMethod = new RelationPredicate("classHasMethod", classChildMethods, "Class",
					methodParentClass, "Method");
			addRelationPredicate(classHasMethod);
		}

		if (interfaceChildMethods != null && methodParentInterface != null)
		{
			RelationPredicate interfaceHasMethod = new RelationPredicate("interfaceHasMethod", interfaceChildMethods,
					"Interface", methodParentInterface, "Method");
			addRelationPredicate(interfaceHasMethod);
		}

		if (classChildFields != null && fieldParentClass != null)
		{
			RelationPredicate classHasField = new RelationPredicate("classHasField", classChildFields, "Class",
					fieldParentClass, "Field");
			addRelationPredicate(classHasField);
		}

		RelationPredicate classHasAnnotation = null;
		if (classAnnotations != null && annotationAttachedClasses != null)
		{
			classHasAnnotation = new RelationPredicate("classHasAnnotation", classAnnotations, "Class",
					annotationAttachedClasses, "Annotation");
			addRelationPredicate(classHasAnnotation);
		}

		RelationPredicate interfaceHasAnnotation = null;
		if (interfaceAnnotations != null && annotationAttachedInterfaces != null)
		{
			interfaceHasAnnotation = new RelationPredicate("interfaceHasAnnotation", interfaceAnnotations, "Interface",
					annotationAttachedInterfaces, "Annotation");
			addRelationPredicate(interfaceHasAnnotation);
		}

		if (classHasAnnotation != null && interfaceHasAnnotation != null)
		{
			CompositeRelationPredicate typeHasAnnotation = new CompositeRelationPredicate("typeHasAnnotation",
					classHasAnnotation, interfaceHasAnnotation);
			addRelationPredicate(typeHasAnnotation);
		}

		/** Method * */
		if (methodChildParameters != null && parameterParentMethod != null)
		{
			RelationPredicate methodHasParameter = new RelationPredicate("methodHasParameter", methodChildParameters,
					"Method", parameterParentMethod, "Parameter");
			addRelationPredicate(methodHasParameter);
		}

		if (methodAnnotations != null && annotationAttachedMethods != null)
		{
			RelationPredicate methodHasAnnotation = new RelationPredicate("methodHasAnnotation", methodAnnotations,
					"Method", annotationAttachedMethods, "Annotation");
			addRelationPredicate(methodHasAnnotation);
		}

		if (methodReturnClass != null && classMethodReturnClass != null)
		{
			RelationPredicate methodReturnClassRel = new RelationPredicate("methodReturnClass", methodReturnClass,
					"Method", classMethodReturnClass, "Class");
			addRelationPredicate(methodReturnClassRel);
		}

		/** Parameter * */
		if (parameterClass != null && classParameterClass != null)
		{
			RelationPredicate parameterClassRel = new RelationPredicate("parameterClass", parameterClass, "Parameter",
					classParameterClass, "Class");
			addRelationPredicate(parameterClassRel);
		}

		if (parameterAnnotations != null && annotationAttachedParameters != null)
		{
			RelationPredicate parameterHasAnnotation = new RelationPredicate("parameterHasAnnotation",
					parameterAnnotations, "Parameter", annotationAttachedParameters, "Annotation");
			addRelationPredicate(parameterHasAnnotation);
		}

		/** Field * */
		if (fieldClass != null && classFieldClass != null)
		{
			RelationPredicate fieldClassRel = new RelationPredicate("fieldClass", fieldClass, "Field", classFieldClass,
					"Class");
			addRelationPredicate(fieldClassRel);
		}

		if (fieldInterface != null && interfaceFieldInterface != null)
		{
			RelationPredicate fieldInterfaceRel = new RelationPredicate("fieldInterface", fieldInterface, "Field",
					interfaceFieldInterface, "Interface");
			addRelationPredicate(fieldInterfaceRel);
		}

		if (fieldAnnotations != null && annotationAttachedFields != null)
		{
			RelationPredicate fieldHasAnnotation = new RelationPredicate("fieldHasAnnotation", fieldAnnotations,
					"Field", annotationAttachedFields, "Annotation");
			addRelationPredicate(fieldHasAnnotation);
		}
	}

	/**
	 * @return Returns the languageUnitTypes.
	 */
	public Collection<LanguageUnitType> getLanguageUnitTypes()
	{
		return languageUnitTypes.values();
	}

	/**
	 * @param languageUnitTypes The languageUnitTypes to set.
	 */
	public void setLanguageUnitTypes(Map<String, LanguageUnitType> languageUnitTypes)
	{
		this.languageUnitTypes = languageUnitTypes;
	}

	/**
	 * @return Returns the relationPredicates.
	 */
	public Map<String, RelationPredicate> getRelationPredicates()
	{
		return relationPredicates;
	}

	/**
	 * @param relationPredicates The relationPredicates to set.
	 */
	public void setRelationPredicates(Map<String, RelationPredicate> relationPredicates)
	{
		this.relationPredicates = relationPredicates;
	}

	/**
	 * Returns path of unitrelations between two languageunits
	 * 
	 * @param from Type of languageunit
	 * @param to Type of languageunit
	 */
	public Map<String, MethodNode> getPathOfUnitRelations(String from, String to)
	{
		Map<String, MethodNode> relations = new HashMap<String, MethodNode>();
		List<String> params = new ArrayList<String>();
		MethodNode m = new MethodNode("getUnitRelation");

		try
		{
			if (from.equals(to))
			{
				return relations;
			}
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
					// TODO: add more cases
				}
			}
		}
		catch (ModelClashException mce)
		{
			logger
					.warn("Unable to generate map of relations from " + from + " to " + to + ": " + mce.getMessage(),
							mce);
		}

		return null;
	}

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
			lostMatch = true; // assume we are not going to find a child that
			// matches
			UnitResult children = currNS.getUnitRelation("ChildNamespaces");
			for (Object o : children.multiValue())
			{
				LangNamespace ns = (LangNamespace) o;
				if (namespace.startsWith(ns.getUnitName() + '.') || namespace.equals(ns.getUnitName()))
				{
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
					if (pos < 0)
					{
						pos = nsToAdd.length();
					}
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
							if (pos < 0)
							{
								pos = nsToAdd.length();
							}
						}
						else
						{
							pos = -1;
						}
						currNS = subns;
					}
				}
				catch (IndexOutOfBoundsException e)
				{
					// if you get this error, you're in real trouble.
					logger.warn("Internal error: " + e.getMessage(), e);
				}
				catch (ModuleException e)
				{
					logger.error("Internal error: " + e.getMessage(), e);
				}
			}
		}
		return currNS;
	}

}
