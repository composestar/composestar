/*
 * DotNETFieldInfo.java - Created on 18-okt-2004 by havingaw
 *
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2004 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 */

package Composestar.DotNET.LAMA;

import Composestar.Core.LAMA.*;
import Composestar.Core.LAMA.FieldInfo;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * @author havingaw Contains the .Net reflection information of a Field See
 *         documentation of .NET System.Reflection.FieldInfo (on MSDN).
 */

public class DotNETFieldInfo extends FieldInfo
{
	private static final long serialVersionUID = 235924601234730641L;

	public int HashCode; // .NET hashcode

	public boolean IsAssembly; // Visible on Assembly level?

	public boolean IsFamily; // Visible on Family level?

	public boolean IsFamilyAndAssembly; // Visible at both levels?

	public boolean IsFamilyOrAssembly; // Visible at (at least) one level?

	public boolean IsInitOnly; // Can only be set in body of constructor?

	public boolean IsLiteral; // Written at compile time (i.e. cannot be
								// changed)?

	// public boolean IsNotSerialized; // Field has NotSerialized attribute?
	// public boolean IsPinvokeImpl;
	public boolean IsPrivate; // Private field?

	public boolean IsPublic; // Public field?

	public boolean IsStatic; // Static field ('global')?

	public boolean IsDeclaredHere; // Declared in this Type, or inherited from
									// parent type?

	private DotNETType Parent; // Type that this field belongs to.

	public DotNETFieldInfo()
	{
		super();
	}

	public int getHashCode()
	{
		return this.HashCode;
	}

	public void setHashCode(int hashcode)
	{
		this.HashCode = hashcode;
	}

	public boolean isAssembly()
	{
		return this.IsAssembly;
	}

	public void setIsAssembly(boolean isAssembly)
	{
		this.IsAssembly = isAssembly;
	}

	public boolean isFamily()
	{
		return this.IsFamily;
	}

	public void setIsFamily(boolean isFamily)
	{
		this.IsFamily = isFamily;
	}

	public boolean isFamilyAndAssembly()
	{
		return this.IsFamilyAndAssembly;
	}

	public void setIsFamilyAndAssembly(boolean isFamilyAndAssembly)
	{
		this.IsFamilyAndAssembly = isFamilyAndAssembly;
	}

	public boolean isFamilyOrAssembly()
	{
		return this.IsFamilyOrAssembly;
	}

	public void setIsFamilyOrAssembly(boolean isFamilyOrAssembly)
	{
		this.IsFamilyOrAssembly = isFamilyOrAssembly;
	}

	public boolean isInitOnly()
	{
		return this.IsInitOnly;
	}

	public void setIsInitOnly(boolean isInitOnly)
	{
		this.IsInitOnly = isInitOnly;
	}

	public boolean isLiteral()
	{
		return this.IsLiteral;
	}

	public void setIsLiteral(boolean isLiteral)
	{
		this.IsLiteral = isLiteral;
	}

	public boolean isPrivate()
	{
		return this.IsPrivate;
	}

	public void setIsPrivate(boolean isPrivate)
	{
		this.IsPrivate = isPrivate;
	}

	public boolean isPublic()
	{
		return this.IsPublic;
	}

	public void setIsPublic(boolean isPublic)
	{
		this.IsPublic = isPublic;
	}

	public boolean isStatic()
	{
		return this.IsStatic;
	}

	public void setIsStatic(boolean isStatic)
	{
		this.IsStatic = isStatic;
	}

	public boolean isDeclaredHere()
	{
		return IsDeclaredHere;
	}

	public void setIsDeclaredHere(boolean isDeclaredHere)
	{
		IsDeclaredHere = isDeclaredHere;
	}

	/** Stuff for LOLA* */

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.LAMA.ProgramElement#getUnitRelation(java.lang.String)
	 */
	public UnitResult getUnitRelation(String argumentName)
	{
		if ("ParentType".equals(argumentName)) return new UnitResult(Parent);
		else if ("Class".equals(argumentName) && "Class".equals(fieldType().getUnitType())) return new UnitResult(
				fieldType());
		else if ("Interface".equals(argumentName) && "Interface".equals(fieldType().getUnitType())) return new UnitResult(
				fieldType());
		else if ("Annotation".equals(argumentName) && "Annotation".equals(fieldType().getUnitType())) return new UnitResult(
				fieldType());
		else if ("Annotations".equals(argumentName))
		{
			Iterator i = getAnnotations().iterator();
			HashSet res = new HashSet();
			while (i.hasNext())
				res.add(((Annotation) i.next()).getType());
			return new UnitResult(res);
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.LAMA.ProgramElement#getUnitAttributes()
	 */
	public Collection getUnitAttributes()
	{
		HashSet result = new HashSet();
		if (isPublic()) result.add("public");
		if (isPrivate()) result.add("private");
		// TODO: For some reasons, there is no 'protected' attr for fields?
		if (isStatic()) result.add("static");
		return result;
	}

	/**
	 * Custom deserialization of this object
	 * 
	 * @param in
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		HashCode = in.readInt();
		IsAssembly = in.readBoolean();
		IsFamily = in.readBoolean();
		IsFamilyAndAssembly = in.readBoolean();
		IsFamilyOrAssembly = in.readBoolean();
		IsInitOnly = in.readBoolean();
		IsLiteral = in.readBoolean();
		IsPrivate = in.readBoolean();
		IsPublic = in.readBoolean();
		IsStatic = in.readBoolean();
		IsDeclaredHere = in.readBoolean();
	}

	/**
	 * Custom serialization of this object
	 * 
	 * @param out
	 */
	private void writeObject(ObjectOutputStream out) throws IOException
	{
		out.writeInt(HashCode);
		out.writeBoolean(IsAssembly);
		out.writeBoolean(IsFamily);
		out.writeBoolean(IsFamilyAndAssembly);
		out.writeBoolean(IsFamilyOrAssembly);
		out.writeBoolean(IsInitOnly);
		out.writeBoolean(IsLiteral);
		out.writeBoolean(IsPrivate);
		out.writeBoolean(IsPublic);
		out.writeBoolean(IsStatic);
		out.writeBoolean(IsDeclaredHere);
		out.writeObject(Parent);
	}
}
