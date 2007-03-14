/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */

package Composestar.C.LAMA;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.HashSet;

import Composestar.Core.LAMA.MethodInfo;
import Composestar.Core.LAMA.ParameterInfo;
import Composestar.Core.LAMA.UnitRegister;
import Composestar.Core.LAMA.UnitResult;

public class CParameterInfo extends ParameterInfo
{
	private static final long serialVersionUID = -527486572514730318L;

	private int HashCode;

	public int Position;

	// public boolean IsIn;
	// public boolean IsLcid;
	// public boolean IsOptional;
	// public boolean IsOut;
	// public boolean IsRetval;

	/**

	 */
	public CParameterInfo()
	{
		UnitRegister.instance().registerLanguageUnit(this);
	}

	// public CType parameterType()
	// {
	// if( ParameterType == null )
	// {
	// CTypeMap map = CTypeMap.instance();
	// ParameterType = map.getType( ParameterTypeString );
	// }
	// return ParameterType;
	// }

	public void setParameterType(String paramType)
	{
		parameterTypeString = paramType;
	}

	/**
	 * @return int
	 */
	public int position()
	{
		return Position;
	}

	/**
	 * @param pos
	 */
	public void setPosition(int pos)
	{
		Position = pos;
	}

	/**
	 * @return int
	 */
	public int getHashCode()
	{
		return HashCode;
	}

	/**
	 * @param code
	 */
	public void setHashCode(int code)
	{
		HashCode = code;
	}

	/** **** Implementation of Language Unit interface ********* */

	public boolean hasUnitAttribute(String attribute)
	{
		return false;
	}

	public String getParameterTypeString()
	{
		return parameterTypeString;
	}

	public UnitResult getUnitRelation(String argumentName)
	{
		/***********************************************************************
		 * Real C Languagemodel if (argumentName.equals("ParentFunction"))
		 * return new UnitResult(ParentFunction); if
		 * (argumentName.equals("ParentMethod")) return new UnitResult(Parent);
		 * if (argumentName.equals("ChildType")) return new
		 * UnitResult(ParameterType); return null;
		 **********************************************************************/
		if (argumentName.equals("ParentMethod"))
		{
			return new UnitResult(parent);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.CTCommon.LOLA.metamodel.LanguageUnit#getUnitAttributes()
	 */
	public Collection getUnitAttributes()
	{
		return new HashSet();
	}

	/**
	 * Custom deserialization of this object
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		HashCode = in.readInt();
		name = in.readUTF();
		Position = in.readInt();
		parameterTypeString = in.readUTF();
		parent = (MethodInfo) in.readObject();
	}

	/**
	 * Custom serialization of this object
	 */
	private void writeObject(ObjectOutputStream out) throws IOException
	{
		out.writeInt(HashCode);
		out.writeUTF(name);
		out.writeInt(Position);
		out.writeUTF(parameterTypeString);
		out.writeObject(parent);
	}
}
