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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

//
// !! Compose* Runtime Warning !!
//
// This class is referenced in the Compose* Runtime for .NET 1.1
// Do not use Java features added after Java 2.0
//

public abstract class MethodInfo extends ProgramElement
{

	public String Name;

	public String returnTypeString;

	public Type returnType;

	public ArrayList parameters;

	public Type parent;

	private HashSet callsToOtherMethods;

	private HashSet reifiedMessageBehavior;

	private HashSet resourceUsage;

	/**
	 * @param dummy if true don't register this language unit, should be used
	 *            when temporary clones are needed
	 */
	public MethodInfo()
	{
		parameters = new ArrayList();
		callsToOtherMethods = new HashSet();
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
		return Name;
	}

	/**
	 * @param inName
	 */
	public void setName(String inName)
	{
		Name = inName;
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
	public HashSet getCallsToOtherMethods()
	{
		return callsToOtherMethods;
	}

	public void setCallsToOtherMethods(HashSet value)
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
	public HashSet getResourceUsage()
	{
		return resourceUsage;
	}

	public void setResourceUsage(HashSet value)
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
	public HashSet getReifiedMessageBehavior()
	{
		return reifiedMessageBehavior;
	}

	public void setReifiedMessageBehavior(HashSet value)
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
			sb.append(au);
		}
		sb.append(" ");
		sb.append(returnTypeString);
		sb.append(" ");
		sb.append(Name);
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

	public String getUnitName()
	{
		return getName();
	}

	public String getUnitType()
	{
		return "Method";
	}

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
		Name = in.readUTF();
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
		out.writeUTF(Name);
		out.writeUTF(returnTypeString);
		out.writeObject(parameters);
		out.writeObject(parent);
		out.writeObject(callsToOtherMethods);
		out.writeObject(reifiedMessageBehavior);
		out.writeObject(resourceUsage);
	}

	public boolean checkEquals(MethodInfo method)
	{
		if (!method.Name.equals(this.Name))
		{
			return false;
		}

		if (!method.returnTypeString.equals(this.returnTypeString))
		{
			return false;
		}

		if (this.parameters.size() != method.parameters.size())
		{
			return false;
		}

		for (int i = 0; i < this.parameters.size(); i++)
		{
			ParameterInfo thisPar = (ParameterInfo) this.parameters.get(i);
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

		sb.append(this.getName()).append('%');
		sb.append(this.getReturnTypeString()).append('%');

		List pars = this.getParameters();
		for (int i = 0; i < pars.size(); i++)
		{
			ParameterInfo pi = (ParameterInfo) pars.get(i);
			sb.append(pi.getParameterTypeString()).append('%');
		}

		return sb.toString();
	}
}
