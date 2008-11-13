package Composestar.Java.LAMA;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import Composestar.Core.LAMA.Annotation;
import Composestar.Core.LAMA.FieldInfo;
import Composestar.Core.LAMA.MethodInfo;
import Composestar.Core.LAMA.ParameterInfo;
import Composestar.Core.LAMA.ProgramElement;
import Composestar.Core.LAMA.Type;
import Composestar.Core.LAMA.UnitResult;
import Composestar.Core.LOLA.metamodel.EUnitType;

/**
 * An instance of <code>JavaType</code> represents a class object in Java. It
 * extends the generic object <code>Type</code>.
 * 
 * @see Composestar.Core.LAMA.Type
 */
public class JavaType extends Type
{
	private static final long serialVersionUID = 3739255608747710756L;

	public Class<?> theClass;

	public boolean isAnnotation;

	private JavaType superClass;

	public List<String> implementedInterfaceNames; // List of Strings

	private List<JavaType> implementedInterfaces; // List of JavaTypes

	/**
	 * Added by the Language Model; this relation can be used in logic queries
	 */
	private ProgramElement parentNS;

	/**
	 * Added by the Language Model; this relation contains links to sub-types of
	 * this type.
	 */
	private Set<ProgramElement> childTypes;

	/**
	 * Added by the Language Model; this relation contains links to parameters
	 * of this type.
	 */
	private Set<ProgramElement> parameterTypes;

	/**
	 * Added by the Language Model; this relation contains links to methods that
	 * return this type.
	 */
	private Set<ProgramElement> methodReturnTypes;

	/**
	 * Added by the Language Model; this relation contains links to fields of
	 * this type.
	 */
	private Set<ProgramElement> fieldTypes;

	/**
	 * Added by the Language Model; this relation exists for interfaces and
	 * points to the Types that implement this interface
	 */
	private Set<ProgramElement> implementedBy;

	/**
	 * Constructor
	 * 
	 * @param c - <code>java.lang.Class</code> instance.
	 */
	public JavaType(Class<?> c)
	{
		super();
		theClass = c;
		implementedInterfaceNames = new ArrayList<String>();
		implementedInterfaces = new ArrayList<JavaType>();
		childTypes = new HashSet<ProgramElement>();
		parameterTypes = new HashSet<ProgramElement>();
		methodReturnTypes = new HashSet<ProgramElement>();
		fieldTypes = new HashSet<ProgramElement>();
		implementedBy = new HashSet<ProgramElement>();
		isAnnotation = c.isAnnotation();
	}

	/**
	 * Returns the <code>Class</code> instance wrapped inside this
	 * <code>JavaType</code> instance.
	 * 
	 * @return java.lang.Class
	 */
	public Class<?> getTypeClass()
	{
		return theClass;
	}

	/**
	 * Adds an interface to this <code>JavaType</code>.
	 * 
	 * @param iface the interface.
	 */
	public void addImplementedInterface(String iface)
	{
		implementedInterfaceNames.add(iface);
	}

	public void addImplementedInterface(JavaType iface)
	{
		implementedInterfaces.add(iface);
	}

	public List<String> getImplementedInterfaceNames()
	{
		return Collections.unmodifiableList(implementedInterfaceNames);
	}

	/**
	 * Returns a list of interfaces that this <code>JavaType</code> implements.
	 */
	public List<JavaType> getImplementedInterfaces()
	{
		return Collections.unmodifiableList(implementedInterfaces);
	}

	/**
	 * Returns the fully qualified package name of this class.
	 * 
	 * @return java.lang.String
	 */
	@Override
	public String namespace()
	{
		if (theClass.getPackage() == null)
		{
			return null;
		}
		return theClass.getPackage().getName();
	}

	/**
	 * Returns true if this class represents an <code>Annotation</code>.
	 */
	public boolean isAnnotation()
	{
		return isAnnotation;
	}

	/**
	 * Sets the annotation state of this <code>JavaType</code>.
	 * 
	 * @param b - true if this <code>JavaType</code> is an
	 *            <code>Annotation</code>.
	 */
	public void setIsAnnotation(boolean b)
	{
		isAnnotation = b;
	}

	/**
	 * Return true if this class represents an <code>Interface</code>.
	 */
	public boolean isInterface()
	{
		return theClass.isInterface();
	}

	/**
	 * Returns the superclass of this <code>JavaType<code>.
	 */
	public JavaType superClass()
	{
		return superClass;
	}

	public void setSuperClass(JavaType type)
	{
		superClass = type;
	}

	public String getSuperClassString()
	{
		Class<?> superclass = theClass.getSuperclass();
		if (superclass != null)
		{
			return theClass.getSuperclass().getName();
		}
		else
		{
			return null;
		}
	}

	/** Stuff for LOLA * */

	/**
	 * Filters declared objects. Only methods or fields declared in this class
	 * are returned.
	 * 
	 * @param in - a set of <code>MethodInfo</code> or <code>FieldInfo</code>.
	 * @return a <code>HashSet</code> containing objects declared in this class.
	 */
	private Set<MethodInfo> filterMethodInfo(Collection<MethodInfo> in)
	{
		Set<MethodInfo> out = new HashSet<MethodInfo>();
		for (MethodInfo obj : in)
		{
			if (obj instanceof JavaMethodInfo)
			{
				if (((JavaMethodInfo) obj).isDeclaredHere())
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

	private Set<FieldInfo> filterFieldInfo(Collection<FieldInfo> in)
	{
		Set<FieldInfo> out = new HashSet<FieldInfo>();
		for (FieldInfo obj : in)
		{
			if (obj instanceof JavaFieldInfo)
			{
				if (((JavaFieldInfo) obj).isDeclaredHere())
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

	/**
	 * @see Composestar.core.LAMA.ProgramElement#getUnitAttributes()
	 */
	@Override
	public Collection<String> getUnitAttributes()
	{
		HashSet<String> result = new HashSet<String>(Arrays.asList(Modifier.toString(theClass.getModifiers())
				.split(" ")));
		return result;
	}

	/**
	 * @see Composestar.Core.LAMA.ProgramElement#getUnitRelation(java.lang.String)
	 */
	@Override
	public UnitResult getUnitRelation(String argumentName)
	{
		if (getUnitType().equals(EUnitType.CLASS.toString()))
		{
			return getUnitRelationForClass(argumentName);
		}
		else if (getUnitType().equals(EUnitType.INTERFACE.toString()))
		{
			return getUnitRelationForInterface(argumentName);
		}
		else if (getUnitType().equals(EUnitType.ANNOTATION.toString()))
		{
			return getUnitRelationForAnnotation(argumentName);
		}
		return null; // Should never happen!
	}

	public UnitResult getUnitRelationForAnnotation(String argumentName)
	{
		HashSet<JavaType> resClasses = new HashSet<JavaType>();
		HashSet<JavaType> resInterfaces = new HashSet<JavaType>();
		HashSet<JavaMethodInfo> resMethods = new HashSet<JavaMethodInfo>();
		HashSet<FieldInfo> resFields = new HashSet<FieldInfo>();
		HashSet<ParameterInfo> resParameters = new HashSet<ParameterInfo>();

		for (Object o : getAnnotationInstances())
		{
			ProgramElement unit = ((Annotation) o).getTarget();
			if (unit instanceof JavaType)
			{
				JavaType type = (JavaType) unit;
				if (type.isInterface())
				{
					resInterfaces.add(type);
				}
				else
				{
					resClasses.add(type);
				}
			}
			else if (unit instanceof JavaMethodInfo)
			{
				resMethods.add((JavaMethodInfo) unit);
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

	public UnitResult getUnitRelationForClass(String argumentName)
	{
		if (argumentName.equals("ParentNamespace"))
		{
			return new UnitResult(parentNS);
		}
		else if (argumentName.equals("ParentClass"))
		{
			return new UnitResult(superClass()); // can be null!
		}
		else if (argumentName.equals("ChildClasses"))
		{
			return new UnitResult(childTypes);
		}
		else if (argumentName.equals("ChildMethods"))
		{
			return new UnitResult(filterMethodInfo(methods));
		}
		else if (argumentName.equals("ChildFields"))
		{
			return new UnitResult(filterFieldInfo(fields));
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
			return new UnitResult(new HashSet<Type>(getImplementedInterfaces()));
		}
		else if (argumentName.equals("Annotations"))
		{
			Iterator<Annotation> i = getAnnotations().iterator();
			Set<Type> res = new HashSet<Type>();
			while (i.hasNext())
			{
				res.add(i.next().getType());
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
			return new UnitResult(superClass()); // can be null!
		}
		else if (argumentName.equals("ChildInterfaces"))
		{
			return new UnitResult(childTypes);
		}
		else if (argumentName.equals("ChildMethods"))
		{
			return new UnitResult(filterMethodInfo(methods));
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
			HashSet<Type> res = new HashSet<Type>();
			while (i.hasNext())
			{
				res.add(i.next().getType());
			}
			return new UnitResult(res);
		}

		return null;
	}

	/**
	 * @see Composestar.core.LAMA.ProgramElement#getUnitType()
	 */
	@Override
	public String getUnitType()
	{
		if (isAnnotation())
		{
			return EUnitType.ANNOTATION.toString();
		}
		else if (isInterface())
		{
			return EUnitType.INTERFACE.toString();
		}
		else
		{
			return EUnitType.CLASS.toString();
		}
	}

	/** * Extra method for adding links to child types of this type */
	@Override
	public void addChildType(ProgramElement childType)
	{
		childTypes.add(childType);
	}

	/** * Extra method for adding links to methods that return this type */
	@Override
	public void addFieldType(ProgramElement fieldType)
	{
		fieldTypes.add(fieldType);
	}

	/** * Extra method for adding links to classes that implement this interface */
	@Override
	public void addImplementedBy(ProgramElement aClass)
	{
		implementedBy.add(aClass);
	}

	/** * Extra method for adding links to methods that return this type */
	@Override
	public void addMethodReturnType(ProgramElement returnType)
	{
		methodReturnTypes.add(returnType);
	}

	/** * Extra method for adding links to parameters of this type */
	@Override
	public void addParameterType(ProgramElement paramType)
	{
		parameterTypes.add(paramType);
	}

	/**
	 * Extra method for storing the parent namespace; called by
	 * JavaLanguageModel::completeModel()
	 */
	@Override
	public void setParentNamespace(ProgramElement parentNS)
	{
		this.parentNS = parentNS;
	}

	/**
	 * Custom deserialization of this object
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		implementedInterfaceNames = (List<String>) in.readObject();
	}

	/**
	 * Custom serialization of this object
	 */
	private void writeObject(ObjectOutputStream out) throws IOException
	{
		out.writeObject(implementedInterfaceNames);
	}

	@Override
	public String toString()
	{
		return fullName;
	}
}
