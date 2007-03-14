/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2006 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */
package Composestar.Core.LAMA;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public abstract class FieldInfo extends ProgramElement
{

	public String name;

	public Type parent;

	public String fieldTypeString;

	private Type fieldType;

	public FieldInfo()
	{
		UnitRegister.instance().registerLanguageUnit(this);
	}

	public String getName()
	{
		return name;
	}

	public void setName(String inName)
	{
		name = inName;
	}

	public Type getFieldType()
	{
		if (this.fieldType == null)
		{
			TypeMap map = TypeMap.instance();
			fieldType = map.getType(fieldTypeString);
		}
		return fieldType;
	}
	
	public String getFieldTypeString()
	{
		return fieldTypeString;
	}

	public void setFieldType(String fieldtype)
	{
		this.fieldTypeString = fieldtype;
	}

	public Type getParent()
	{
		return parent;
	}

	public void setParent(Type inParent)
	{
		parent = inParent;
	}

	/** Stuff for LOLA * */

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.LAMA.ProgramElement#getUnitName()
	 */
	public String getUnitName()
	{
		return getName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.LAMA.ProgramElement#getUnitType()
	 */
	public String getUnitType()
	{
		return "Field";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.core.LAMA.ProgramElement#hasAttribute(java.lang.String)
	 */
	public boolean hasUnitAttribute(String attribute)
	{
		return getUnitAttributes().contains(attribute);
	}
	
	public abstract boolean isPublic();
	
	public abstract boolean isPrivate();
	
	public abstract boolean isProtected();

	/**
	 * Custom deserialization of this object
	 * 
	 * @param in
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		name = in.readUTF();
		parent = (Type) in.readObject();
		fieldTypeString = in.readUTF();
	}

	/**
	 * Custom serialization of this object
	 * 
	 * @param out
	 */
	private void writeObject(ObjectOutputStream out) throws IOException
	{
		out.writeUTF(name);
		out.writeObject(parent);
		out.writeUTF(fieldTypeString);
	}
}
