package Composestar.Java.LAMA;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import Composestar.Core.LAMA.MethodInfo;
import Composestar.Core.LAMA.Type;
import Composestar.Core.LAMA.UnitResult;
import Composestar.Utils.StringConverter;

/**
 * An instance of <code>JavaMethodInfo</code> represents a method in Java. It
 * extends the generic object <code>MethodInfo</code>.
 * 
 * @see Composestar.Core.LAMA.MethodInfo
 */
public class JavaMethodInfo extends MethodInfo
{

	private static final long serialVersionUID = 308590098089260675L;

	public Method theMethod;

	/**
	 * Default constructor.
	 */
	public JavaMethodInfo()
	{
		super();
	}
	
	public JavaMethodInfo(boolean dummy)
	{
		super(dummy);
	}

	/**
	 * Constructor.
	 * 
	 * @param m - <code>java.lang.reflect.Method</code> instance.
	 */
	public JavaMethodInfo(Method m)
	{
		super();
		this.theMethod = m;
	}

	/**
	 * Returns true if this <code>JavaMethodInfo</code> is declared inside its
	 * parent <code>Type</code>.
	 */
	public boolean isDeclaredHere()
	{
		if ((parent.getFullName()).equals(theMethod.getDeclaringClass().getName())) 
		{
			return true;
		}
		return false;
	}

	/**
	 * This method should make a clone of the MethodInfo with the name and
	 * parentType changed to the given name and actualParent. The parameters and
	 * return type should stay the same.
	 */
	public MethodInfo getClone(String name, Type actualParent)
	{
		JavaMethodInfo mi = new JavaMethodInfo(true);
		mi.setName(name);

		// set MethodInfo variables
		mi.parent = actualParent;
		mi.parameters = this.parameters;
		mi.returnType = this.returnType;
		mi.returnTypeString = this.returnTypeString;

		// set JavaMethodInfo variables
		mi.theMethod = this.theMethod;

		return mi;
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
	 * @see Composestar.core.LAMA.ProgramElement#getUnitAttributes()
	 */
	public Collection getUnitAttributes()
	{
		HashSet result = new HashSet();
		Iterator modifierIt = StringConverter.stringToStringList(Modifier.toString(theMethod.getModifiers()), " ");
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
		if (argumentName.equals("ParentClass") && parent.getUnitType().equals("Class")) 
		{
			return new UnitResult(parent);
		}
		else if (argumentName.equals("ParentInterface") && parent.getUnitType().equals("Interface")) 
		{
			return new UnitResult(parent);
		}
		else if (argumentName.equals("ChildParameters")) 
		{
			return new UnitResult(toHashSet(parameters));
		}
		else if (argumentName.equals("ReturnClass") && getReturnType().getUnitType().equals("Class")) 
		{	
			return new UnitResult(getReturnType());
		}
		else if (argumentName.equals("ReturnInterface") && getReturnType().getUnitType().equals("Interface")) 
		{
			return new UnitResult(getReturnType());
		}
		else if (argumentName.equals("ReturnAnnotation") && getReturnType().getUnitType().equals("Annotation")) 
		{
			return new UnitResult(getReturnType());
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
	
	@Override
	public boolean isPrivate()
	{
		return javassist.Modifier.isPrivate(theMethod.getModifiers());
	}

	@Override
	public boolean isProtected()
	{
		return javassist.Modifier.isProtected(theMethod.getModifiers());
	}

	@Override
	public boolean isPublic()
	{
		return javassist.Modifier.isPublic(theMethod.getModifiers());
	}

	/**
	 * Custom deserialization of this object
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
	{

	}

	/**
	 * Custom serialization of this object
	 */
	private void writeObject(ObjectOutputStream out) throws IOException
	{

	}
}
