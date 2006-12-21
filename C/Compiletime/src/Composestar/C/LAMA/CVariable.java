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
import Composestar.Core.LAMA.UnitRegister;
import Composestar.Core.LAMA.UnitResult;
import Composestar.Core.RepositoryImplementation.SerializableRepositoryEntity;

public class CVariable extends FieldInfo implements SerializableRepositoryEntity
{

	private static final long serialVersionUID = 235924601234730641L;

	public int HashCode;

	public String Name;

	public String FieldTypeString;

	private CFile ParentFile;

	public boolean IsStatic;

	private int pointerLevel;

	private boolean isGlobal;

	private boolean isExtern;

	private boolean isInline;

	private int arrayLevel;

	public CVariable()
	{
		UnitRegister.instance().registerLanguageUnit(this);
	}

	public int getHashCode()
	{
		return this.HashCode;
	}

	public void setHashCode(int hashcode)
	{
		this.HashCode = hashcode;
	}

	/**
	 * test is dit nodig?? public CType fieldType() { if (this.FieldType ==
	 * null) { CTypeMap map = CTypeMap.instance(); this.FieldType =
	 * (CType)map.getType( FieldTypeString ); } return FieldType; }
	 */

	public void setFieldType(String fieldtype)
	{
		this.FieldTypeString = fieldtype;
	}

	public boolean isStatic()
	{
		return this.IsStatic;
	}

	public void setIsInline(boolean isInline)
	{
		this.isInline = isInline;
	}

	public boolean isInline()
	{
		return this.isInline;
	}

	public void setIsExtern(boolean isExtern)
	{
		this.isExtern = isExtern;
	}

	public boolean isExtern()
	{
		return this.isExtern;
	}

	public void setIsStatic(boolean isStatic)
	{
		this.IsStatic = isStatic;
	}

	public boolean isPointer()
	{
		return this.pointerLevel > 0;
	}

	public void setPointerLevel(int pointer)
	{
		this.pointerLevel = pointer;
	}

	public int getPointerLevel()
	{
		return pointerLevel;
	}

	public boolean isArray()
	{
		return this.arrayLevel > 0;
	}

	public void setArrayLevel(int pointer)
	{
		this.arrayLevel = pointer;
	}

	public int getArrayLevel()
	{
		return arrayLevel;
	}

	public boolean isGlobal()
	{
		return this.isGlobal;
	}

	public void setIsGlobal(boolean isGlobal)
	{
		this.isGlobal = isGlobal;
	}

	public String name()
	{
		return this.Name;
	}

	public void setName(String name)
	{
		this.Name = name;
	}

	public void setParent(CFile parent)
	{
		this.ParentFile = parent;
	}

	public String getUnitName()
	{
		return name();
	}

	public boolean hasUnitAttribute(String attribute)
	{
		if (attribute.equals("static") && IsStatic)
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
			return new UnitResult(ParentFile);
		}
		else if ("Class".equals(argumentName) && "Class".equals(fieldType().getUnitType()))
		{
			return new UnitResult(
					fieldType());
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
		HashCode = in.readInt();
		FieldTypeString = in.readUTF();
		IsStatic = in.readBoolean();
		isGlobal = in.readBoolean();
		pointerLevel = in.readInt();
		arrayLevel = in.readInt();
		Name = in.readUTF();
		ParentFile = (CFile) in.readObject();
	}

	/**
	 * Custom serialization of this object
	 */
	private void writeObject(ObjectOutputStream out) throws IOException
	{
		out.writeInt(HashCode);
		out.writeUTF(FieldTypeString);
		out.writeBoolean(IsStatic);
		out.writeBoolean(isGlobal);
		out.writeInt(pointerLevel);
		out.writeInt(arrayLevel);
		out.writeUTF(Name);
		out.writeObject(ParentFile);
	}
}
