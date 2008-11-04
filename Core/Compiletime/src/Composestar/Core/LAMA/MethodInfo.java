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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public abstract class MethodInfo extends ProgramElement
{

	private static final long serialVersionUID = -1982981581623913708L;

	public String name;

	public String returnTypeString;

	public Type returnType;

	public List parameters;

	public Type parent;

	private Set<CallToOtherMethod> callsToOtherMethods;

	private Set reifiedMessageBehavior;

	private Set resourceUsage;

	/**
	 * @param dummy if true don't register this language unit, should be used
	 *            when temporary clones are needed
	 */
	public MethodInfo()
	{
		parameters = new ArrayList();
		callsToOtherMethods = new HashSet<CallToOtherMethod>();
		reifiedMessageBehavior = new HashSet();
		resourceUsage = new HashSet();
	}

	/**
	 * @param param
	 */
	public void addParameter(ParameterInfo param)
	{
		parameters.add(param);
		// param.setParent(this);
	}

	/**
	 * This method should make a clone of the MethodInfo with the name and
	 * parentType changed to the given name and actualParent. The parameters and
	 * return type should stay the same. The clone is NOT added to the
	 * UnitRegister.
	 * 
	 * @param name
	 * @param actualParent
	 */
	public abstract MethodInfo getClone(String name, Type actualParent);

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
	 * @return java.util.List
	 */
	public List getParameters()
	{
		return parameters;
	}

	/**
	 * Get a list with information about the calls to other methods from within
	 * this method.
	 * 
	 * @return java.util.List
	 */
	public Set<CallToOtherMethod> getCallsToOtherMethods()
	{
		return callsToOtherMethods;
	}

	public void setCallsToOtherMethods(Set<CallToOtherMethod> value)
	{
		callsToOtherMethods = value;
	}

	/**
	 * Indicates if this instance had information about the calls to other
	 * methods.
	 * 
	 * @return
	 */
	public boolean hasCallsToOtherMethodsInformation()
	{
		return !callsToOtherMethods.isEmpty();
	}

	/**
	 * Get the resource usage information.
	 * 
	 * @return
	 */
	public Set getResourceUsage()
	{
		return resourceUsage;
	}

	public void setResourceUsage(Set value)
	{
		resourceUsage = value;
	}

	/**
	 * Indicates if this instance had information about the resource usage.
	 * 
	 * @return
	 */
	public boolean hasResourceUsage()
	{
		return !resourceUsage.isEmpty();
	}

	/**
	 * Get a list of strings describing the behaviour of the ReifiedMessage
	 * 
	 * @return java.util.List
	 */
	public Set getReifiedMessageBehavior()
	{
		return reifiedMessageBehavior;
	}

	public void setReifiedMessageBehavior(Set value)
	{
		reifiedMessageBehavior = value;
	}

	/**
	 * Indicates if the message has a specified ReifiedMessage Behavior
	 * collection.
	 * 
	 * @return boolean
	 */
	public boolean hasReifiedMessageBehavior()
	{
		return !reifiedMessageBehavior.isEmpty();
	}

	/**
	 * @param types Check if the methods has these types
	 * @return true if there is a signature match. False otherwise
	 */
	public boolean hasParameters(String[] types)
	{
		if (parameters.size() != types.length)
		{
			return false;
		}

		for (int i = 0; i < types.length; i++)
		{
			ParameterInfo parameter = (ParameterInfo) parameters.get(i);
			if (!parameter.getParameterTypeString().equals(types[i]))
			{
				return false;
			}
		}

		return true;
	}

	public Type parent()
	{
		return parent;
	}

	public void setParent(Type inParent)
	{
		parent = inParent;
	}

	public Type getReturnType()
	{
		return returnType;
	}

	public void setReturnType(Type type)
	{
		returnType = type;
	}

	public void setReturnType(String type)
	{
		returnTypeString = type;
	}

	public String getReturnTypeString()
	{
		return returnTypeString;
	}

	public abstract boolean isDeclaredHere();

	@Override
	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		Iterator au = getUnitAttributes().iterator();
		while (au.hasNext())
		{
			if (sb.length() > 0)
			{
				sb.append(" ");
			}
			sb.append(au.next());
		}
		sb.append(" ");
		sb.append(returnTypeString);
		sb.append(" ");
		if (parent != null)
		{
			sb.append(parent.fullName);
			sb.append(".");
		}
		sb.append(name);
		sb.append("(");
		for (int i = 0; i < parameters.size(); i++)
		{
			if (i > 0)
			{
				sb.append(", ");
			}
			sb.append(parameters.get(i));
		}
		sb.append(")");
		return sb.toString();
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
		return "Method";
	}

	@Override
	public boolean hasUnitAttribute(String attribute)
	{
		return getUnitAttributes().contains(attribute);
	}

	// custom (de)serialization

	/**
	 * Custom deserialization of this object
	 * 
	 * @param in
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		name = in.readUTF();
		returnTypeString = in.readUTF();
		parameters = (ArrayList) in.readObject();
		parent = (Type) in.readObject();
		callsToOtherMethods = (HashSet) in.readObject();
		reifiedMessageBehavior = (HashSet) in.readObject();
		resourceUsage = (HashSet) in.readObject();
	}

	/**
	 * Custom serialization of this object
	 * 
	 * @param out
	 */
	private void writeObject(ObjectOutputStream out) throws IOException
	{
		out.writeUTF(name);
		out.writeUTF(returnTypeString);
		out.writeObject(parameters);
		out.writeObject(parent);
		out.writeObject(callsToOtherMethods);
		out.writeObject(reifiedMessageBehavior);
		out.writeObject(resourceUsage);
	}

	public boolean checkEquals(MethodInfo method)
	{
		if (!method.name.equals(name))
		{
			return false;
		}

		if (!method.returnTypeString.equals(returnTypeString))
		{
			return false;
		}

		if (parameters.size() != method.parameters.size())
		{
			return false;
		}

		for (int i = 0; i < parameters.size(); i++)
		{
			ParameterInfo thisPar = (ParameterInfo) parameters.get(i);
			ParameterInfo objPar = (ParameterInfo) method.parameters.get(i);
			if (!thisPar.parameterTypeString.equals(objPar.parameterTypeString))
			{
				return false;
			}
		}

		return true;
	}

	public abstract boolean isPublic();

	public abstract boolean isPrivate();

	public abstract boolean isProtected();

	public String getHashKey()
	{
		StringBuffer sb = new StringBuffer();

		sb.append(getName()).append('%');
		sb.append(getReturnTypeString()).append('%');

		List pars = getParameters();
		for (int i = 0; i < pars.size(); i++)
		{
			ParameterInfo pi = (ParameterInfo) pars.get(i);
			sb.append(pi.getParameterTypeString()).append('%');
		}

		return sb.toString();
	}
}
