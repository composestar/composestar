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

//
// !! Compose* Runtime Warning !!
//
// This class is referenced in the Compose* Runtime for .NET 1.1
// Do not use Java features added after Java 2.0
//

public abstract class ParameterInfo extends ProgramElement
{
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

	public String toString()
	{
		return parameterTypeString + " " + name;
	}

	// Stuff for LOLA

	public String getUnitName()
	{
		return getName();
	}

	public String getUnitType()
	{
		return "Parameter";
	}

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
