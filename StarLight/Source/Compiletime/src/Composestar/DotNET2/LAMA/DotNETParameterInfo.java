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
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import Composestar.Core.LAMA.Annotation;
import Composestar.Core.LAMA.ParameterInfo;
import Composestar.Core.LAMA.Type;
import Composestar.Core.LAMA.UnitResult;
import Composestar.Core.LOLA.metamodel.ERelationType;
import Composestar.Core.LOLA.metamodel.EUnitType;

/**
 * Corresponds to the ParameterInfo class in the .NET framework. For more
 * information on the methods and their meaning please refer to the microsoft
 * documentation:
 * http://msdn.microsoft.com/library/default.asp?url=/library/en-us
 * /cpref/html/frlr fsystemreflectionparameterinfoclasstopic.asp
 */
public class DotNETParameterInfo extends ParameterInfo
{
	private static final long serialVersionUID = -527486572514730318L;

	private int dotNetHashCode;

	public int position;

	private boolean flagIsIn;

	private boolean flagIsOptional;

	private boolean flagIsOut;

	private boolean flagIsRetval;

	/**

	 */
	public DotNETParameterInfo()
	{
		super();
	}

	/**
	 * @return boolean
	 */
	public boolean isIn()
	{
		return flagIsIn;
	}

	/**
	 * @param isln
	 */
	public void setIsln(boolean isln)
	{
		flagIsIn = isln;
	}

	/**
	 * @return boolean
	 */
	public boolean isOptional()
	{
		return flagIsOptional;
	}

	/**
	 * @param isOptional
	 */
	public void setIsOptional(boolean isOptional)
	{
		flagIsOptional = isOptional;
	}

	/**
	 * @return boolean
	 */
	public boolean isOut()
	{
		return flagIsOut;
	}

	/**
	 * @param isOut
	 */
	public void setIsOut(boolean isOut)
	{
		flagIsOut = isOut;
	}

	/**
	 * @return boolean
	 */
	public boolean isRetval()
	{
		return flagIsRetval;
	}

	/**
	 * @param isRetval
	 */
	public void setIsRetVal(boolean isRetval)
	{
		flagIsRetval = isRetval;
	}

	/**
	 * @return int
	 */
	public int position()
	{
		return position;
	}

	/**
	 * @param pos
	 */
	public void setPosition(int pos)
	{
		position = pos;
	}

	/**
	 * @return int
	 */
	public int getHashCode()
	{
		return dotNetHashCode;
	}

	/**
	 * @param code
	 */
	public void setHashCode(int code)
	{
		dotNetHashCode = code;
	}

	/** Stuff for LOLA * */

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.LAMA.ProgramElement#getUnitRelation(java.lang.String)
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
				&& EUnitType.INTERFACE.equals(parameterType().getUnitType()))
		{
			return new UnitResult(parameterType());
		}
		else if (ERelationType.ANNOTATION.equals(argumentName)
				&& EUnitType.ANNOTATION.equals(parameterType().getUnitType()))
		{
			return new UnitResult(parameterType());
		}
		else if (ERelationType.ANNOTATIONS.equals(argumentName))
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

	/*
	 * (non-Javadoc)
	 * @see Composestar.Core.LAMA.ProgramElement#getUnitAttributes()
	 */
	@Override
	public Collection<String> getUnitAttributes()
	{
		return Collections.emptySet();
	}

	/**
	 * Custom deserialization of this object
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		dotNetHashCode = in.readInt();
		position = in.readInt();
		flagIsIn = in.readBoolean();
		flagIsOptional = in.readBoolean();
		flagIsOut = in.readBoolean();
		flagIsRetval = in.readBoolean();
	}

	/**
	 * Custom serialization of this object
	 */
	private void writeObject(ObjectOutputStream out) throws IOException
	{
		out.writeInt(dotNetHashCode);
		out.writeInt(position);
		out.writeBoolean(flagIsIn);
		out.writeBoolean(flagIsOptional);
		out.writeBoolean(flagIsOut);
		out.writeBoolean(flagIsRetval);
	}
}
