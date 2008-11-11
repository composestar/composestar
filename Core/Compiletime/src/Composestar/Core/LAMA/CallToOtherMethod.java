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
import java.io.Serializable;

/**
 * @author Michiel van Oudheusden
 */
public class CallToOtherMethod implements Serializable
{
	private static final long serialVersionUID = 1718642064472551590L;

	/**
	 * The fully qualified name
	 */
	public String operationName;

	/**
	 * The name of the method
	 */
	public String methodName;

	public MethodInfo calledMethod;

	public String className;

	public Type parent;

	public CallToOtherMethod()
	{

	}

	public Type parent()
	{
		return parent;
	}

	/**
	 * @return the calledMethod
	 */
	public MethodInfo getCalledMethod()
	{
		return calledMethod;
	}

	/**
	 * @param inCalledMethod the calledMethod to set
	 */
	public void setCalledMethod(MethodInfo inCalledMethod)
	{
		calledMethod = inCalledMethod;
	}

	/**
	 * @return the className
	 */
	public String getClassName()
	{
		return className;
	}

	/**
	 * @param inClassName the className to set
	 */
	public void setClassName(String inClassName)
	{
		className = inClassName;
	}

	/**
	 * @return the operationName
	 */
	public String getOperationName()
	{
		return operationName;
	}

	/**
	 * @param inOperationName the operationName to set
	 */
	public void setOperationName(String inOperationName)
	{
		operationName = inOperationName;
	}

	/**
	 * @return the parent
	 */
	public Type getParent()
	{
		return parent;
	}

	public void setParent(Type inParent)
	{
		parent = inParent;
	}

	/**
	 * @return the methodName
	 */
	public String getMethodName()
	{
		return methodName;
	}

	/**
	 * @param inMethodName the methodName to set
	 */
	public void setMethodName(String inMethodName)
	{
		methodName = inMethodName;
	}

	/**
	 * Custom deserialization of this object
	 * 
	 * @param in
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		operationName = in.readUTF();
		parent = (Type) in.readObject();
		className = in.readUTF();
	}

	/**
	 * Custom serialization of this object
	 * 
	 * @param out
	 */
	private void writeObject(ObjectOutputStream out) throws IOException
	{
		out.writeUTF(operationName);
		out.writeObject(parent);
		out.writeUTF(className);
	}

}
