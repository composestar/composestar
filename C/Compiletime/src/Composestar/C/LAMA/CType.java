package Composestar.C.LAMA;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import Composestar.Core.LAMA.Type;
import Composestar.Core.LAMA.UnitRegister;
import Composestar.Core.LAMA.UnitResult;

public class CType extends Type
{

	private static final long serialVersionUID = 5652622506200113401L;

	public int HashCode;

	public String name;

	public String fullName;

	public boolean isStruct = false;

	public boolean isEnum = false;

	public boolean isUnion = false;

	public boolean isTypeDef = false;

	public int pointerLevel = 0;

	public int arrayLevel = 0;

	public CType(String name)
	{
		UnitRegister.instance().registerLanguageUnit(this);
		this.name = name;
		fullName = name;
	}

	public void setStruct(boolean isStruct)
	{
		this.isStruct = isStruct;
	}

	public void setEnum(boolean isEnum)
	{
		this.isEnum = isEnum;
	}

	public void setUnion(boolean isUnion)
	{
		this.isUnion = isUnion;
	}

	public void setTypeDef(boolean isTypeDef)
	{
		this.isTypeDef = isTypeDef;
	}

	public void setFullName(String fullName)
	{
		this.fullName = fullName;
	}

	public void setPointerLevel(int pointerLevel)
	{
		this.pointerLevel = pointerLevel;
	}

	public void setArrayLevel(int arrayLevel)
	{
		this.arrayLevel = arrayLevel;
	}

	public String stringRepresentation()
	{
		String type = "";
		if (isStruct)
		{
			type += "struct ";
		}
		else if (isEnum)
		{
			type += "enum ";
		}
		else if (isUnion)
		{
			type += "union ";
		}
		type = type + name;
		if (pointerLevel > 0)
		{
			for (int i = 0; i < pointerLevel; i++)
			{
				type = type + "*";
			}
		}
		if (arrayLevel > 0)
		{
			for (int i = 0; i < arrayLevel; i++)
			{
				type = type + "[]";
			}
		}
		return type;

	}

	private HashSet arrayListToHashSet(Collection in)
	{
		HashSet out = new HashSet();
		Iterator iter = in.iterator();
		for (Object obj : in)
		{
			out.add(obj);
			// System.out.println(((ProgramElement)obj).getUnitName());
		}
		return out;
	}

	public String getUnitName()
	{
		return stringRepresentation();
	}

	public UnitResult getUnitRelation(String argumentName)
	{
		/***********************************************************************
		 * Type does not contain program elements
		 */
		return null; // Should never happen!
	}

	public boolean hasUnitAttribute(String attribute)
	{
		return false;
	}

	public String getUnitType()
	{
		return "Type";
	}

	public Collection getUnitAttributes()
	{
		return null;
	}

	/**
	 * Custom deserialization of this object
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		HashCode = in.readInt();
		name = (String) in.readObject();
		fullName = (String) in.readObject();
		isStruct = in.readBoolean();
		isEnum = in.readBoolean();
		isUnion = in.readBoolean();
		isTypeDef = in.readBoolean();
		pointerLevel = in.readInt();
		arrayLevel = in.readInt();
	}

	/**
	 * Custom serialization of this object
	 */
	private void writeObject(ObjectOutputStream out) throws IOException
	{
		out.writeInt(HashCode);
		out.writeObject(name);
		out.writeObject(fullName);
		out.writeBoolean(isStruct);
		out.writeBoolean(isEnum);
		out.writeBoolean(isUnion);
		out.writeBoolean(isTypeDef);
		out.writeInt(pointerLevel);
		out.writeInt(arrayLevel);
	}

}
