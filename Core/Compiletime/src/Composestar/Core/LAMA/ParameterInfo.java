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

public abstract class ParameterInfo extends ProgramElement
{
	private static final long serialVersionUID = -1258210215588047498L;

	public String parameterTypeString;

	private Type parameterType;

	public String name;

	public MethodInfo parent;

	public ParameterInfo()
	{}

	/**
	 * @return java.lang.String
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * @param inName
	 */
	public void setName(String inName)
	{
		name = inName;
	}

	/**
	 * @return Composestar.Core.LAMA.Type
	 */
	public Type parameterType()
	{
		return parameterType;
	}

	public void setParameterType(Type paramType)
	{
		parameterType = paramType;
	}

	/**
	 * @return the parameterTypeString
	 */
	public String getParameterTypeString()
	{
		return parameterTypeString;
	}

	/**
	 * @param paramType
	 */
	public void setParameterType(String paramType)
	{
		parameterTypeString = paramType;
	}

	/**
	 * @return Returns the parent.
	 */
	public MethodInfo getParent()
	{
		return parent;
	}

	/**
	 * @param inParent The parent to set.
	 */
	public void setParent(MethodInfo inParent)
	{
		parent = inParent;
	}

	@Override
	public String toString()
	{
		return parameterTypeString + " " + name;
	}

	// Stuff for LOLA

	@Override
	public String getUnitName()
	{
		return getName();
	}

	@Override
	public String getUnitType()
	{
		return "Parameter";
	}

	@Override
	public boolean hasUnitAttribute(String attribute)
	{
		return false;
	}

	/**
	 * Custom deserialization of this object
	 * 
	 * @param in
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		name = in.readUTF();
		parameterTypeString = in.readUTF();
		parent = (MethodInfo) in.readObject();
	}

	/**
	 * Custom serialization of this object
	 * 
	 * @param out
	 */
	private void writeObject(ObjectOutputStream out) throws IOException
	{
		out.writeUTF(name);
		out.writeUTF(parameterTypeString);
		out.writeObject(parent);
	}
}
