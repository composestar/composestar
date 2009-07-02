package Composestar.Java.LAMA;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;

import Composestar.Core.LAMA.Annotation;
import Composestar.Core.LAMA.ParameterInfo;
import Composestar.Core.LAMA.Type;
import Composestar.Core.LAMA.UnitResult;
import Composestar.Core.LOLA.metamodel.ERelationType;
import Composestar.Core.LOLA.metamodel.EUnitType;

/**
 * An instance of <code>JavaParameterInfo</code> represents a parameter object
 * in Java. It extends the generic object <code>ParameterInfo</code>.
 * 
 * @see Composestar.Core.LAMA.ParameterInfo
 */
public class JavaParameterInfo extends ParameterInfo
{
	private static final long serialVersionUID = -6839040829288333952L;

	private Class<?> theParameter;

	/**
	 * Constructor.
	 * 
	 * @param p - <code>java.lang.Class</code> instance representing this
	 *            parameter.
	 */
	public JavaParameterInfo(Class<?> p)
	{
		super();
		theParameter = p;
	}

	/** Stuff for LOLA * */

	/**
	 * @see Composestar.core.LAMA.ProgramElement#getUnitAttributes()
	 */
	@Override
	public Collection<String> getUnitAttributes()
	{
		return Collections.emptySet();
	}

	/**
	 * @see Composestar.Core.LAMA.ProgramElement#getUnitRelation(java.lang.String)
	 */
	@Override
	public UnitResult getUnitRelation(String argumentName)
	{
		if (ERelationType.PARENT_METHOD.equals(argumentName))
		{
			return new UnitResult(parent);
		}
		else if (ERelationType.CLASS.equals(argumentName) && EUnitType.CLASS.equals(parameterType().getUnitType()))
		{
			return new UnitResult(parameterType());
		}
		else if (ERelationType.INTERFACE.equals(argumentName)
				&& ERelationType.INTERFACE.equals(parameterType().getUnitType()))
		{
			return new UnitResult(parameterType());
		}
		else if ("Annotation".equals(argumentName) && EUnitType.ANNOTATION.equals(parameterType().getUnitType()))
		{
			return new UnitResult(parameterType());
		}
		else if (ERelationType.ANNOTATIONS.equals(argumentName))
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
