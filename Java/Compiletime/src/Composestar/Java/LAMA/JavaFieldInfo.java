package Composestar.Java.LAMA;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import Composestar.Core.LAMA.FieldInfo;
import Composestar.Core.LAMA.Type;
import Composestar.Core.LAMA.UnitResult;

/**
 * An instance of <code>JavaFieldInfo</code> represents a field object in
 * Java. It extends the generic object <code>FieldInfo</code>.
 * 
 * @see Composestar.Core.LAMA.FieldInfo
 */
public class JavaFieldInfo extends FieldInfo
{
	private static final long serialVersionUID = -3329314904670932060L;

	public transient Field theField;

	/**
	 * Constructor.
	 * 
	 * @param f - <code>java.lang.reflect.Field</code> instance.
	 */
	public JavaFieldInfo(Field f)
	{
		super();
		theField = f;
	}

	/**
	 * Returns true if this <code>JavaFieldInfo</code> is declared inside its
	 * parent <code>Type</code>.
	 */
	@Override
	public boolean isDeclaredHere()
	{
		if (parent.getFullName().equals(theField.getDeclaringClass().getName()))
		{
			return true;
		}
		return false;
	}

	/** Stuff for LOLA * */

	/**
	 * @see Composestar.core.LAMA.ProgramElement#getUnitAttributes()
	 */
	@Override
	public Collection<String> getUnitAttributes()
	{
		Set<String> result = new HashSet<String>(Arrays.asList(Modifier.toString(theField.getModifiers()).split(" ")));
		return result;
	}

	/**
	 * @see Composestar.Core.LAMA.ProgramElement#getUnitRelation(java.lang.String)
	 */
	@Override
	public UnitResult getUnitRelation(String argumentName)
	{
		if ("ParentType".equals(argumentName))
		{
			return new UnitResult(parent);
		}
		else if ("Class".equals(argumentName) && "Class".equals(getFieldType().getUnitType()))
		{
			return new UnitResult(getFieldType());
		}
		else if ("Interface".equals(argumentName) && "Interface".equals(getFieldType().getUnitType()))
		{
			return new UnitResult(getFieldType());
		}
		else if ("Annotation".equals(argumentName) && "Annotation".equals(getFieldType().getUnitType()))
		{
			return new UnitResult(getFieldType());
		}
		else if ("Annotations".equals(argumentName))
		{
			Iterator<JavaAnnotation> i = getAnnotations().iterator();
			Set<Type> res = new HashSet<Type>();
			while (i.hasNext())
			{
				res.add(i.next().getType());
			}
			return new UnitResult(res);
		}

		return null;
	}

	@Override
	public boolean isPrivate()
	{
		return javassist.Modifier.isPrivate(theField.getModifiers());
	}

	@Override
	public boolean isProtected()
	{
		return javassist.Modifier.isProtected(theField.getModifiers());
	}

	@Override
	public boolean isPublic()
	{
		return javassist.Modifier.isPublic(theField.getModifiers());
	}

	/**
	 * Custom deserialization of this object
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
	{}

	/**
	 * Custom serialization of this object
	 */
	private void writeObject(ObjectOutputStream out) throws IOException
	{}
}
