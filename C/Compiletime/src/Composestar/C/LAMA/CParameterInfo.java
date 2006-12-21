/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id: CParameterInfo.java,v 1.2 2006/09/30 12:20:30 johantewinkel Exp $
 */

package Composestar.C.LAMA;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.HashSet;

import Composestar.Core.LAMA.ParameterInfo;
import Composestar.Core.LAMA.UnitRegister;
import Composestar.Core.LAMA.UnitResult;

/**
 * Corresponds to the ParameterInfo class in the .NET framework. For more
 * information on the methods and their meaning please refer to the microsoft
 * documentation:
 * http://msdn.microsoft.com/library/default.asp?url=/library/en-us/cpref/html/frlr
 * fsystemreflectionparameterinfoclasstopic.asp
 */
public class CParameterInfo extends ParameterInfo /* implements LanguageUnit */
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -527486572514730318L;

	private int HashCode;

	public int Position;

	// public boolean IsIn;
	// public boolean IsLcid;
	// public boolean IsOptional;
	// public boolean IsOut;
	// public boolean IsRetval;
	public CMethodInfo Parent;

	public String Name;

	/**
	 * @roseuid 401B84CF0220
	 */
	public CParameterInfo()
	{
		UnitRegister.instance().registerLanguageUnit(this);
	}

	/**
	 * @param name
	 * @roseuid 402A072800EE
	 */
	public void setName(String name)
	{
		Name = name;
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
		ParameterTypeString = paramType;
	}

	/**
	 * @return int
	 * @roseuid 401B84CF021E
	 */
	public int position()
	{
		return Position;
	}

	/**
	 * @param pos
	 * @roseuid 402A074A0061
	 */
	public void setPosition(int pos)
	{
		Position = pos;
	}

	/**
	 * @return int
	 * @roseuid 401B84CF021F
	 */
	public int getHashCode()
	{
		return HashCode;
	}

	/**
	 * @param code
	 * @roseuid 402A075602A3
	 */
	public void setHashCode(int code)
	{
		HashCode = code;
	}

	public void setParent(CMethodInfo parent)
	{
		Parent = parent;
	}

	/** **** Implementation of Language Unit interface ********* */

	public String getUnitName()
	{
		return Name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.LAMA.ProgramElement#getUnitType()
	 */
	public String getUnitType()
	{
		return "Parameter";
	}

	public boolean hasUnitAttribute(String attribute)
	{
		return false;
	}

	public String getParameterTypeString()
	{
		return ParameterTypeString;
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
			return new UnitResult(Parent);
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
		Name = in.readUTF();
		Position = in.readInt();
		ParameterTypeString = in.readUTF();
		Parent = (CMethodInfo) in.readObject();
	}

	/**
	 * Custom serialization of this object
	 */
	private void writeObject(ObjectOutputStream out) throws IOException
	{
		out.writeInt(HashCode);
		out.writeUTF(Name);
		out.writeInt(Position);
		out.writeUTF(ParameterTypeString);
		out.writeObject(Parent);
	}
}
