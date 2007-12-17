/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */

package Composestar.DotNET2.LAMA;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import Composestar.Core.LAMA.Annotation;
import Composestar.Core.LAMA.FieldInfo;
import Composestar.Core.LAMA.ParameterInfo;
import Composestar.Core.LAMA.ProgramElement;
import Composestar.Core.LAMA.Type;
import Composestar.Core.LAMA.TypeMap;
import Composestar.Core.LAMA.UnitRegister;
import Composestar.Core.LAMA.UnitResult;

/**
 * Corresponds to the Type class in the .NET framework. For more information on
 * the methods and their meaning please refer to the microsoft documentation:
 * http://msdn.microsoft.com/library/default.asp?url=/library/en-us/cpref/html/frlr
 * fsystemtypeclasstopic.asp note that the name may be redundant, but should be
 * consistent with the name of the concern.
 */
public class DotNETType extends Type
{
	private static final long serialVersionUID = 5652622506200113401L;

	private boolean IsClass;

	private boolean IsInterface;

	private boolean IsEnum;

	private boolean IsValueType;

	private boolean IsPrimitive;

	private boolean IsAbstract;

	private boolean IsSealed;

	private boolean IsPublic;

	private DotNETType BaseType;

	private String BaseTypeString;

	private List<DotNETType> ImplementedInterfaces; // List of DotNETTypes

	private List<String> ImplementedInterfaceNames; // List of Strings

	private String Namespace;

	private String AssemblyName;

	private String fromSource;

	private int endPos;

	// for LOLA

	/*
	 * Added by the Language Model; this relation can be used in logic queries
	 */
	private ProgramElement parentNS;

	/*
	 * Added by the Language Model; this relation contains links to sub-types of
	 * this type.
	 */
	private Set<ProgramElement> childTypes;

	/*
	 * Added by the Language Model; this relation contains links to parameters
	 * of this type.
	 */
	private Set<ProgramElement> parameterTypes;

	/*
	 * Added by the Language Model; this relation contains links to methods that
	 * return this type.
	 */
	private Set<ProgramElement> methodReturnTypes;

	/*
	 * Added by the Language Model; this relation contains links to fields of
	 * this type.
	 */
	private Set<ProgramElement> fieldTypes;

	/*
	 * Added by the Language Model; this relation exists for interfaces and
	 * points to the Types that implement this interface
	 */
	private Set<ProgramElement> implementedBy;

	public DotNETType()
	{
		super();
		ImplementedInterfaceNames = new ArrayList<String>();
		ImplementedInterfaces = null;
		childTypes = new HashSet<ProgramElement>();
		parameterTypes = new HashSet<ProgramElement>();
		methodReturnTypes = new HashSet<ProgramElement>();
		fieldTypes = new HashSet<ProgramElement>();
		implementedBy = new HashSet<ProgramElement>();
	}

	public boolean isClass()
	{
		return IsClass;
	}

	public void setIsClass(boolean isClass)
	{
		IsClass = isClass;
	}

	public boolean isInterface()
	{
		return IsInterface;
	}

	public void setIsInterface(boolean isInterface)
	{
		IsInterface = isInterface;
	}

	public boolean isEnum()
	{
		return IsEnum;
	}

	public void setIsEnum(boolean isEnum)
	{
		IsEnum = isEnum;
	}

	public boolean isValueType()
	{
		return IsValueType;
	}

	public void setIsValueType(boolean isValueType)
	{
		IsValueType = isValueType;
	}

	public boolean isPrimitive()
	{
		return IsPrimitive;
	}

	public void setIsPrimitive(boolean isPrim)
	{
		IsPrimitive = isPrim;
	}

	public boolean isAbstract()
	{
		return IsAbstract;
	}

	public void setIsAbstract(boolean isAbstract)
	{
		IsAbstract = isAbstract;
	}

	public boolean isSealed()
	{
		return IsSealed;
	}

	public void setIsSealed(boolean isSealed)
	{
		IsSealed = isSealed;
	}

	public boolean isPublic()
	{
		return IsPublic;
	}

	public void setIsPublic(boolean isPublic)
	{
		IsPublic = isPublic;
	}

	public DotNETType baseType()
	{
		if (BaseType == null)
		{
			TypeMap map = TypeMap.instance();
			BaseType = (DotNETType) map.getType(BaseTypeString);
		}
		return BaseType;
	}

	public void setBaseType(String type)
	{
		BaseTypeString = type;
	}

	public void addImplementedInterface(String iface)
	{
		ImplementedInterfaceNames.add(iface);
	}

	public List<DotNETType> getImplementedInterfaces()
	{
		if (null == ImplementedInterfaces)
		{
			ImplementedInterfaces = new ArrayList<DotNETType>();
			Iterator<String> iter = ImplementedInterfaceNames.iterator();
			TypeMap map = TypeMap.instance();
			while (iter.hasNext())
			{
				DotNETType iface = (DotNETType) map.getType(iter.next());
				if (null != iface)
				{
					ImplementedInterfaces.add(iface);
				}
			}
		}
		return ImplementedInterfaces;
	}

	public String namespace()
	{
		return Namespace;
	}

	public void setNamespace(String space)
	{
		Namespace = space;
	}

	public String assemblyName()
	{
		return AssemblyName;
	}

	public void setAssemblyName(String name)
	{
		AssemblyName = name;
	}

	public boolean isFromSource()
	{
		return fromSource != null;
	}

	public String getFromSource()
	{
		return fromSource;
	}

	public void setFromSource(String fromFile)
	{
		fromSource = fromFile;
	}

	public int getEndPos()
	{
		return endPos;
	}

	public void setEndPos(int endPos)
	{
		this.endPos = endPos;
	}

	public List<DotNETMethodInfo> getConstructors()
	{
		List<DotNETMethodInfo> constructors = new ArrayList<DotNETMethodInfo>();
		Iterator<DotNETMethodInfo> it = methods.iterator();
		while (it.hasNext())
		{
			DotNETMethodInfo method = it.next();
			if (method.isConstructor())
			{
				constructors.add(method);
			}
		}
		return constructors;
	}

	public DotNETMethodInfo getConstructor(String[] types)
	{
		Iterator<DotNETMethodInfo> it = methods.iterator();
		while (it.hasNext())
		{
			DotNETMethodInfo method = it.next();
			if (method.isConstructor() && method.hasParameters(types))
			{
				return method;
			}
		}
		return null;
	}

	public DotNETFieldInfo getField(String name)
	{
		Iterator<DotNETFieldInfo> it = fields.iterator();
		while (it.hasNext())
		{
			DotNETFieldInfo field = it.next();
			if (field.getName().equals(name))
			{
				return field;
			}
		}
		return null;
	}

	// Stuff for LOLA

	private HashSet<ProgramElement> filterDeclaredHere(Collection<ProgramElement> in)
	{
		HashSet<ProgramElement> out = new HashSet<ProgramElement>();
		Iterator<ProgramElement> iter = in.iterator();
		while (iter.hasNext())
		{
			ProgramElement obj = iter.next();
			if (obj instanceof DotNETMethodInfo)
			{
				if (((DotNETMethodInfo) obj).isDeclaredHere())
				{
					out.add(obj);
				}
			}
			else if (obj instanceof DotNETFieldInfo)
			{
				if (((DotNETFieldInfo) obj).isDeclaredHere())
				{
					out.add(obj);
				}
			}
			else
			{
				out.add(obj); // No filtering on other kinds of objects
			}
		}
		return out;
	}

	public UnitResult getUnitRelationForClass(String argumentName)
	{
		if (argumentName.equals("ParentNamespace"))
		{
			return new UnitResult(parentNS);
		}
		else if (argumentName.equals("ParentClass"))
		{
			return new UnitResult(baseType()); // can be null!
		}
		else if (argumentName.equals("ChildClasses"))
		{
			return new UnitResult(childTypes);
		}
		else if (argumentName.equals("ChildMethods"))
		{
			return new UnitResult(filterDeclaredHere(methods));
		}
		else if (argumentName.equals("ChildFields"))
		{
			return new UnitResult(filterDeclaredHere(fields));
		}
		else if (argumentName.equals("ParameterClass"))
		{
			return new UnitResult(parameterTypes);
		}
		else if (argumentName.equals("MethodReturnClass"))
		{
			return new UnitResult(methodReturnTypes);
		}
		else if (argumentName.equals("FieldClass"))
		{
			return new UnitResult(fieldTypes);
		}
		else if (argumentName.equals("Implements"))
		{
			return new UnitResult(new HashSet<DotNETType>(getImplementedInterfaces()));
		}
		else if (argumentName.equals("Annotations"))
		{
			Iterator<Annotation> i = getAnnotations().iterator();
			Set<Type> res = new HashSet<Type>();
			while (i.hasNext())
			{
				res.add((i.next()).getType());
			}
			return new UnitResult(res);
		}

		return null;
	}

	public UnitResult getUnitRelationForInterface(String argumentName)
	{
		if (argumentName.equals("ParentNamespace"))
		{
			return new UnitResult(parentNS);
		}
		else if (argumentName.equals("ParentInterface"))
		{
			return new UnitResult(baseType()); // can be null!
		}
		else if (argumentName.equals("ChildInterfaces"))
		{
			return new UnitResult(childTypes);
		}
		else if (argumentName.equals("ChildMethods"))
		{
			return new UnitResult(filterDeclaredHere(methods));
		}
		else if (argumentName.equals("ParameterInterface"))
		{
			return new UnitResult(parameterTypes);
		}
		else if (argumentName.equals("MethodReturnInterface"))
		{
			return new UnitResult(methodReturnTypes);
		}
		else if (argumentName.equals("FieldInterface"))
		{
			return new UnitResult(fieldTypes);
		}
		else if (argumentName.equals("ImplementedBy"))
		{
			return new UnitResult(implementedBy);
		}
		else if (argumentName.equals("Annotations"))
		{
			Iterator<Annotation> i = getAnnotations().iterator();
			Set<Type> res = new HashSet<Type>();
			while (i.hasNext())
			{
				res.add((i.next()).getType());
			}
			return new UnitResult(res);
		}

		return null;
	}

	public UnitResult getUnitRelationForAnnotation(String argumentName)
	{
		Set<DotNETType> resClasses = new HashSet<DotNETType>();
		Set<DotNETType> resInterfaces = new HashSet<DotNETType>();
		Set<DotNETMethodInfo> resMethods = new HashSet<DotNETMethodInfo>();
		Set<FieldInfo> resFields = new HashSet<FieldInfo>();
		Set<ParameterInfo> resParameters = new HashSet<ParameterInfo>();

		Iterator<Annotation> i = getAnnotationInstances().iterator();
		while (i.hasNext())
		{
			ProgramElement unit = (i.next()).getTarget();
			if (unit instanceof DotNETType)
			{
				DotNETType type = (DotNETType) unit;
				if (type.isInterface())
				{
					resInterfaces.add(type);
				}
				else if (type.isClass())
				{
					resClasses.add(type);
				}
			}
			else if (unit instanceof DotNETMethodInfo)
			{
				resMethods.add((DotNETMethodInfo) unit);
			}
			else if (unit instanceof FieldInfo)
			{
				resFields.add((FieldInfo) unit);
			}
			else if (unit instanceof ParameterInfo)
			{
				resParameters.add((ParameterInfo) unit);
			}
		}

		if (argumentName.equals("AttachedClasses"))
		{
			return new UnitResult(resClasses);
		}
		else if (argumentName.equals("AttachedInterfaces"))
		{
			return new UnitResult(resInterfaces);
		}
		else if (argumentName.equals("AttachedMethods"))
		{
			return new UnitResult(resMethods);
		}
		else if (argumentName.equals("AttachedFields"))
		{
			return new UnitResult(resFields);
		}
		else if (argumentName.equals("AttachedParameters"))
		{
			return new UnitResult(resParameters);
		}

		return null;
	}

	@Override
	public UnitResult getUnitRelation(String argumentName)
	{
		if (getUnitType().equals("Class"))
		{
			return getUnitRelationForClass(argumentName);
		}
		else if (getUnitType().equals("Interface"))
		{
			return getUnitRelationForInterface(argumentName);
		}
		else if (getUnitType().equals("Annotation"))
		{
			return getUnitRelationForAnnotation(argumentName);
		}
		return null; // Should never happen!
	}

	/**
	 * Extra method for storing the parent namespace; called by
	 * DotNETLanguageModel::completeModel()
	 */
	public void setParentNamespace(ProgramElement parentNS)
	{
		this.parentNS = parentNS;
	}

	/**
	 * Extra method for adding links to child types of this type.
	 */
	public void addChildType(ProgramElement childType)
	{
		if (childTypes == null)
		{
			childTypes = new HashSet<ProgramElement>();
		}
		childTypes.add(childType);
	}

	/**
	 * Extra method for adding links to parameters of this type.
	 */
	public void addParameterType(ProgramElement paramType)
	{
		parameterTypes.add(paramType);
	}

	/**
	 * Extra method for adding links to methods that return this type
	 */
	public void addMethodReturnType(ProgramElement returnType)
	{
		methodReturnTypes.add(returnType);
	}

	/**
	 * Extra method for adding links to methods that return this type
	 */
	public void addFieldType(ProgramElement fieldType)
	{
		fieldTypes.add(fieldType);
	}

	/**
	 * Extra method for adding links to classes that implement this interface
	 */
	public void addImplementedBy(ProgramElement aClass)
	{
		implementedBy.add(aClass);
	}

	@Override
	public String getUnitType()
	{
		// TODO: enum?
		if (isInterface())
		{
			return "Interface";
		}
		else if (isAttribute())
		{
			return "Annotation";
		}
		else
		{
			return "Class";
		}
	}

	// Stuff for annotations

	/**
	 * A class is an annotation (attribute) type, if it inherits from
	 * System.Attribute.
	 */
	public boolean isAttribute()
	{
		DotNETType baseType = baseType();
		while (baseType != null)
		{
			String unitName = baseType.getUnitName();
			if ("System.Attribute".equals(unitName))
			{
				return true;
			}

			baseType = baseType.baseType();
		}
		return false;
	}

	@Override
	public Collection<String> getUnitAttributes()
	{
		Set<String> result = new HashSet<String>();
		if (isPublic())
		{
			result.add("public");
		}
		return result;
	}

	public void reset()
	{
		childTypes = new HashSet<ProgramElement>();
		parameterTypes = new HashSet<ProgramElement>();
		methodReturnTypes = new HashSet<ProgramElement>();
		fieldTypes = new HashSet<ProgramElement>();
		implementedBy = new HashSet<ProgramElement>();
		UnitRegister.instance().registerLanguageUnit(this);

		// register fields
		Iterator<DotNETFieldInfo> fiter = fields.iterator();
		while (fiter.hasNext())
		{
			DotNETFieldInfo field = fiter.next();
			if (null != field)
			{
				UnitRegister.instance().registerLanguageUnit(field);
			}
		}

		// register methods and its parameters
		Iterator<DotNETMethodInfo> miter = methods.iterator();
		while (miter.hasNext())
		{
			DotNETMethodInfo method = miter.next();
			if (null != method)
			{
				UnitRegister.instance().registerLanguageUnit(method);
			}

			Iterator<DotNETParameterInfo> piter = method.getParameters().iterator();
			while (piter.hasNext())
			{
				DotNETParameterInfo param = piter.next();
				if (null != param)
				{
					UnitRegister.instance().registerLanguageUnit(param);
				}
			}
		}
	}

	/**
	 * Returns a string representation of this object.
	 */
	@Override
	public String toString()
	{
		return fullName;
	}

	/**
	 * Custom deserialization of this object
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		IsClass = in.readBoolean();
		IsInterface = in.readBoolean();
		IsEnum = in.readBoolean();
		IsValueType = in.readBoolean();
		IsPrimitive = in.readBoolean();
		IsAbstract = in.readBoolean();
		IsSealed = in.readBoolean();
		IsPublic = in.readBoolean();

		BaseTypeString = in.readUTF();
		ImplementedInterfaceNames = (List<String>) in.readObject();
		Namespace = in.readUTF();
		// fromFile = in.readUTF();
		annotationInstances = (ArrayList<Annotation>) in.readObject();

		if ("".equals(BaseTypeString))
		{
			BaseTypeString = null;
		}
		if ("".equals(Namespace))
		{
			Namespace = null;
		}
	}

	/**
	 * Custom serialization of this object
	 */
	private void writeObject(ObjectOutputStream out) throws IOException
	{
		out.writeBoolean(IsClass);
		out.writeBoolean(IsInterface);
		out.writeBoolean(IsEnum);
		out.writeBoolean(IsValueType);
		out.writeBoolean(IsPrimitive);
		out.writeBoolean(IsAbstract);
		out.writeBoolean(IsSealed);
		out.writeBoolean(IsPublic);

		out.writeUTF(BaseTypeString != null ? BaseTypeString : "");
		out.writeObject(ImplementedInterfaceNames);
		out.writeUTF(Namespace != null ? Namespace : "");
		// out.writeUTF(fromFile != null ? fromFile : "");
		out.writeObject(annotationInstances);
	}
}
