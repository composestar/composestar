/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */

//
package Composestar.C.LAMA;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import Composestar.Core.LAMA.MethodInfo;
import Composestar.Core.LAMA.ParameterInfo;
import Composestar.Core.LAMA.Type;
import Composestar.Core.LAMA.UnitResult;

/**
 * This is the methodinfo implementation for C, it extends the core methodinfo.
 * Its main task is to add unit-relations to the methods.
 */
public class CMethodInfo extends MethodInfo
{

	private static final long serialVersionUID = 5652622506200113201L;

	public int CallingConvention;

	private int HashCode;

	public ArrayList Variables = new ArrayList();

	public CMethodInfo()
	{
		super();
	}

	public int callingConvention()
	{
		return CallingConvention;
	}

	public List getParameters()
	{
		return Parameters;
	}

	public void setCallingConvention(int cv)
	{
		CallingConvention = cv;
	}

	public void setReturnType(String type)
	{
		ReturnTypeString = type;
	}

	public int getHashCode()
	{
		return HashCode;
	}

	public MethodInfo getClone(String n, Type actualParent)
	{
		CMethodInfo mi = new CMethodInfo();
		mi.setName(n);
		mi.Parent = actualParent;
		mi.Parameters = this.Parameters;
		mi.ReturnType = this.ReturnType;
		mi.ReturnTypeString = this.ReturnTypeString;
		mi.CallingConvention = this.CallingConvention;
		return mi;
	}

	public void setHashCode(int code)
	{
		HashCode = code;
	}

	private HashSet arrayListToHashSet(Collection in)
	{
		HashSet out = new HashSet();
		Iterator iter = in.iterator();
		for (Object obj : in)
		{
			out.add(obj);
		}
		return out;
	}

	public void addParameter(ParameterInfo param)
	{
		Parameters.add(param);
		param.setParent(this);
	}

	public void setParent(Type parent)
	{
		Parent = parent;
	}

	public UnitResult getUnitRelation(String argumentName)
	{
		/***********************************************************************
		 * Real C language model if (argumentName.equals("ParentFile")) //moet
		 * parent file worden return new UnitResult(Parent); if
		 * (argumentName.equals("Function")) //moet parent file worden return
		 * new UnitResult(function); if (argumentName.equals("ChildReturnType"))
		 * return new UnitResult(ReturnType); if
		 * (argumentName.equals("ChildParameters")) return new
		 * UnitResult(arrayListToHashSet(Parameters)); if
		 * (argumentName.equals("ChildLocalVariables")) return new
		 * UnitResult(arrayListToHashSet(Variables)); return null;
		 **********************************************************************/
		if (argumentName.equals("ParentClass"))
		{
			return new UnitResult(Parent);
		}
		return null;
	}

	public Collection getUnitAttributes()
	{
		return null;
	}

	public void setName(String name)
	{
		this.Name = name;
	}

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		CallingConvention = in.readInt();
		HashCode = in.readInt();
		Name = in.readUTF();
		ReturnTypeString = in.readUTF();
		Parameters = (ArrayList) in.readObject();
		Variables = (ArrayList) in.readObject();
		Parent = (CFile) in.readObject();
	}

	private void writeObject(ObjectOutputStream out) throws IOException
	{
		out.writeInt(CallingConvention);
		out.writeInt(HashCode);
		out.writeUTF(Name);
		out.writeUTF(ReturnTypeString);
		out.writeObject(Parameters);
		out.writeObject(Variables);
		out.writeObject(Parent);
	}
}
