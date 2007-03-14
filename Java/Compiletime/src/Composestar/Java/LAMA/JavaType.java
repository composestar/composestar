package Composestar.Java.LAMA;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import Composestar.Core.LAMA.FieldInfo;
import Composestar.Core.LAMA.ParameterInfo;
import Composestar.Core.LAMA.ProgramElement;
import Composestar.Core.LAMA.Type;
import Composestar.Core.LAMA.TypeMap;
import Composestar.Core.LAMA.UnitResult;
import Composestar.Utils.StringConverter;

/**
 * An instance of <code>JavaType</code> represents a class object in Java. It
 * extends the generic object <code>Type</code>.
 * 
 * @see Composestar.Core.LAMA.Type
 */
public class JavaType extends Type
{
	private static final long serialVersionUID = 3739255608747710756L;

	public Class theClass;

	public boolean isAnnotation;

	private JavaType superClass;

	public ArrayList ImplementedInterfaceNames; // List of Strings

	private ArrayList ImplementedInterfaces; // List of JavaTypes

	private ProgramElement parentNS; // Added by the Language Model; this

	// relation can be used in logic queries

	private HashSet childTypes; // Added by the Language Model; this relation

	// contains links to sub-types of this type.

	private HashSet parameterTypes; // Added by the Language Model; this

	// relation contains links to parameters of
	// this type.

	private HashSet methodReturnTypes; // Added by the Language Model; this

	// relation contains links to methods
	// that return this type.

	private HashSet fieldTypes; // Added by the Language Model; this relation

	// contains links to fields of this type.

	private HashSet implementedBy; // Added by the Language Model; this

	// relation exists for interfaces and points
	// to the Types that implement this
	// interface

	/**
	 * Constructor
	 * 
	 * @param c - <code>java.lang.Class</code> instance.
	 */
	public JavaType(Class c)
	{
		super();
		this.theClass = c;
		ImplementedInterfaceNames = new ArrayList();
		ImplementedInterfaces = null;
		childTypes = new HashSet();
		parameterTypes = new HashSet();
		methodReturnTypes = new HashSet();
		fieldTypes = new HashSet();
		implementedBy = new HashSet();
		isAnnotation = false;
	}

	/**
	 * Returns the <code>Class</code> instance wrapped inside this
	 * <code>JavaType</code> instance.
	 * 
	 * @return java.lang.Class
	 */
	public Class getclass()
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
		ImplementedInterfaceNames.add(iface);
	}

	/**
	 * Returns a list of interfaces that this <code>JavaType</code>
	 * implements.
	 */
	public List getImplementedInterfaces()
	{

		if (null == ImplementedInterfaces)
		{
			ImplementedInterfaces = new ArrayList();
			Iterator iter = ImplementedInterfaceNames.iterator();
			TypeMap map = TypeMap.instance();
			while (iter.hasNext())
			{
				JavaType iface = (JavaType) map.getType((String) iter.next());
				if (null != iface) 
				{
					ImplementedInterfaces.add(iface);
				}
			}
		}
		return ImplementedInterfaces;
	}

	/**
	 * Returns the fully qualified package name of this class.
	 * 
	 * @return java.lang.String
	 */
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
		this.isAnnotation = b;
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
		if (superClass == null)
		{
			Class superclass = theClass.getSuperclass();
			if (null != superclass)
			{
				TypeMap map = TypeMap.instance();
				superClass = (JavaType) map.getType(theClass.getSuperclass().getName());
			}
		}
		return superClass;
	}

	/** Stuff for LOLA * */

	/**
	 * Helper method. Converts a <code>Collection</code> to a
	 * <code>HashSet</code>.
	 */
	private HashSet toHashSet(Collection c)
	{
		HashSet result = new HashSet();
		for (Object aC : c)
		{
			result.add(aC);
		}
		return result;
	}

	/**
	 * Filters declared objects. Only methods or fields declared in this class
	 * are returned.
	 * 
	 * @param in - a set of <code>MethodInfo</code> or <code>FieldInfo</code>.
	 * @return a <code>HashSet</code> containing objects declared in this
	 *         class.
	 */
	private HashSet filterDeclaredHere(Collection in)
	{
		HashSet out = new HashSet();
		for (Object obj : in)
		{
			if (obj instanceof JavaMethodInfo)
			{
				if (((JavaMethodInfo) obj).isDeclaredHere()) 
				{
					out.add(obj);
				}
			}
			else if (obj instanceof JavaFieldInfo)
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
	public Collection getUnitAttributes()
	{
		HashSet result = new HashSet();
		Iterator modifierIt = StringConverter.stringToStringList(Modifier.toString(theClass.getModifiers()), " ");
		while (modifierIt.hasNext())
		{
			result.add(modifierIt.next());
		}
		return result;
	}

	/**
	 * @see Composestar.Core.LAMA.ProgramElement#getUnitRelation(java.lang.String)
	 */
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

	public UnitResult getUnitRelationForAnnotation(String argumentName)
	{
		HashSet resClasses = new HashSet();
		HashSet resInterfaces = new HashSet();
		HashSet resMethods = new HashSet();
		HashSet resFields = new HashSet();
		HashSet resParameters = new HashSet();

		for (Object o : getAnnotationInstances())
		{
			ProgramElement unit = ((JavaAnnotation) o).getTarget();
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
				resMethods.add(unit);
			}
			else if (unit instanceof FieldInfo)
			{
				resFields.add(unit);
			}
			else if (unit instanceof ParameterInfo)
			{
				resParameters.add(unit);
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
			return new UnitResult(filterDeclaredHere(this.methods));
		}
		else if (argumentName.equals("ChildFields")) 
		{
			return new UnitResult(filterDeclaredHere(this.fields));
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
			return new UnitResult(toHashSet(getImplementedInterfaces()));
		}
		else if (argumentName.equals("Annotations"))
		{
			Iterator i = getAnnotations().iterator();
			HashSet res = new HashSet();
			while (i.hasNext())
			{
				res.add(((JavaAnnotation) i.next()).getType());
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
			return new UnitResult(filterDeclaredHere(this.methods));
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
			Iterator i = getAnnotations().iterator();
			HashSet res = new HashSet();
			while (i.hasNext())
			{
				res.add(((JavaAnnotation) i.next()).getType());
			}
			return new UnitResult(res);
		}

		return null;
	}

	/**
	 * @see Composestar.core.LAMA.ProgramElement#getUnitType()
	 */
	public String getUnitType()
	{
		if (isInterface()) 
		{
			return "Interface";
		}
		else if (isAnnotation()) 
		{
			return "Annotation";
		}
		else 
		{
			return "Class";
		}
	}

	/** * Extra method for adding links to child types of this type */
	public void addChildType(ProgramElement childType)
	{
		this.childTypes.add(childType);
	}

	/** * Extra method for adding links to methods that return this type */
	public void addFieldType(ProgramElement fieldType)
	{
		this.fieldTypes.add(fieldType);
	}

	/** * Extra method for adding links to classes that implement this interface */
	public void addImplementedBy(ProgramElement aClass)
	{
		implementedBy.add(aClass);
	}

	/** * Extra method for adding links to methods that return this type */
	public void addMethodReturnType(ProgramElement returnType)
	{
		this.methodReturnTypes.add(returnType);
	}

	/** * Extra method for adding links to parameters of this type */
	public void addParameterType(ProgramElement paramType)
	{
		this.parameterTypes.add(paramType);
	}

	/**
	 * Extra method for storing the parent namespace; called by
	 * JavaLanguageModel::completeModel()
	 */
	public void setParentNamespace(ProgramElement parentNS)
	{
		this.parentNS = parentNS;
	}

	/**
	 * Custom deserialization of this object
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		ImplementedInterfaceNames = (ArrayList) in.readObject();
	}

	/**
	 * Custom serialization of this object
	 */
	private void writeObject(ObjectOutputStream out) throws IOException
	{
		out.writeObject(ImplementedInterfaceNames);
	}
}
