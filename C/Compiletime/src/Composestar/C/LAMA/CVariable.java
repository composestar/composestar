/*
 * DotNETFieldInfo.java - Created on 18-okt-2004 by havingaw
 *
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2004 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 */

package Composestar.C.LAMA;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.HashSet;

import Composestar.Core.LAMA.FieldInfo;
import Composestar.Core.LAMA.UnitResult;

public class CVariable extends FieldInfo
{

	private static final long serialVersionUID = 235924601234730641L;

	public int hashCode;

	private CFile parent;

	public boolean isStaticVar;

	private int pointerLevel;

	private boolean isGlobal;

	private boolean isExtern;

	private boolean isInline;

	private int arrayLevel;

	public CVariable()
	{}

	public int getHashCode()
	{
		return hashCode;
	}

	public void setHashCode(int hashcode)
	{
		hashCode = hashcode;
	}

	/**
	 * test is dit nodig?? public CType fieldType() { if (this.FieldType ==
	 * null) { CTypeMap map = CTypeMap.instance(); this.FieldType =
	 * (CType)map.getType( FieldTypeString ); } return FieldType; }
	 */

	public boolean isStatic()
	{
		return isStaticVar;
	}

	public void setIsInline(boolean isInline)
	{
		this.isInline = isInline;
	}

	public boolean isInline()
	{
		return isInline;
	}

	public void setIsExtern(boolean isExtern)
	{
		this.isExtern = isExtern;
	}

	public boolean isExtern()
	{
		return isExtern;
	}

	public void setIsStatic(boolean inIsStatic)
	{
		isStaticVar = inIsStatic;
	}

	public boolean isPointer()
	{
		return pointerLevel > 0;
	}

	public void setPointerLevel(int pointer)
	{
		pointerLevel = pointer;
	}

	public int getPointerLevel()
	{
		return pointerLevel;
	}

	public boolean isArray()
	{
		return arrayLevel > 0;
	}

	public void setArrayLevel(int pointer)
	{
		arrayLevel = pointer;
	}

	public int getArrayLevel()
	{
		return arrayLevel;
	}

	public boolean isGlobal()
	{
		return isGlobal;
	}

	public void setIsGlobal(boolean isGlobal)
	{
		this.isGlobal = isGlobal;
	}

	public void setParent(CFile inParent)
	{
		parent = inParent;
	}

	public CFile getParent()
	{
		return parent;
	}

	public String getUnitName()
	{
		return getName();
	}

	public boolean hasUnitAttribute(String attribute)
	{
		if (attribute.equals("static") && isStaticVar)
		{
			return true;
		}
		if (attribute.equals("extern") && isExtern)
		{
			return true;
		}
		if (attribute.equals("inline") && isInline)
		{
			return true;
		}
		return false;
	}

	public UnitResult getUnitRelation(String argumentName)
	{
		/***********************************************************************
		 * Real language model if(isGlobal){ if
		 * ("ParentFile".equals(argumentName)) return new
		 * UnitResult(ParentFile); } else{
		 * if("ParentFunction".equals(argumentName)) return new
		 * UnitResult(ParentFunction); } if ("Type".equals(argumentName)) return
		 * new UnitResult(FieldType); return null;
		 **********************************************************************/
		if ("ParentType".equals(argumentName))
		{
			return new UnitResult(parent);
		}
		else if ("Class".equals(argumentName) && "Class".equals(getFieldType().getUnitType()))
		{
			return new UnitResult(getFieldType());
		}
		return null;
	}

	public Collection getUnitAttributes()
	{
		HashSet result = new HashSet();
		if (isStatic())
		{
			result.add("static");
		}
		if (isExtern)
		{
			result.add("extern");
		}
		if (isInline)
		{
			result.add("inline");
		}
		return result;
	}

	/**
	 * Custom deserialization of this object
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		hashCode = in.readInt();
		fieldTypeString = in.readUTF();
		isStaticVar = in.readBoolean();
		isGlobal = in.readBoolean();
		pointerLevel = in.readInt();
		arrayLevel = in.readInt();
		name = in.readUTF();
		parent = (CFile) in.readObject();
	}

	/**
	 * Custom serialization of this object
	 */
	private void writeObject(ObjectOutputStream out) throws IOException
	{
		out.writeInt(hashCode);
		out.writeUTF(fieldTypeString);
		out.writeBoolean(isStaticVar);
		out.writeBoolean(isGlobal);
		out.writeInt(pointerLevel);
		out.writeInt(arrayLevel);
		out.writeUTF(name);
		out.writeObject(parent);
	}

	@Override
	public boolean isPrivate()
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isProtected()
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isPublic()
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isDeclaredHere()
	{
		// TODO Auto-generated method stub
		return false;
	}
}
