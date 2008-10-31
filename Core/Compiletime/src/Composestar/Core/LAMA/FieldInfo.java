/*
 * This file is part of the Compose* project.
 * http://composestar.sourceforge.net
 * Copyright (C) 2006-2008 University of Twente.
 *
 * Compose* is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as 
 * published by the Free Software Foundation; either version 2.1 of 
 * the License, or (at your option) any later version.
 *
 * Compose* is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public 
 * License along with this program. If not, see 
 * <http://www.gnu.org/licenses/>.
 *
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.txt
 *
 * $Id$
 */
package Composestar.Core.LAMA;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public abstract class FieldInfo extends ProgramElement
{

	private static final long serialVersionUID = 5933829502250438682L;

	public String name;

	public Type parent;

	public String fieldTypeString;

	private Type fieldType;

	public FieldInfo()
	{}

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
		return fieldType;
	}

	public String getFieldTypeString()
	{
		return fieldTypeString;
	}

	public void setFieldType(String fieldtype)
	{
		fieldTypeString = fieldtype;
	}

	public void setFieldType(Type value)
	{
		fieldType = value;
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
	 * @see Composestar.Core.LAMA.ProgramElement#getUnitName()
	 */
	@Override
	public String getUnitName()
	{
		return getName();
	}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Core.LAMA.ProgramElement#getUnitType()
	 */
	@Override
	public String getUnitType()
	{
		return "Field";
	}

	/*
	 * (non-Javadoc)
	 * @see Composestar.core.LAMA.ProgramElement#hasAttribute(java.lang.String)
	 */
	@Override
	public boolean hasUnitAttribute(String attribute)
	{
		return getUnitAttributes().contains(attribute);
	}

	public abstract boolean isPublic();

	public abstract boolean isPrivate();

	public abstract boolean isProtected();

	public abstract boolean isDeclaredHere();

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
