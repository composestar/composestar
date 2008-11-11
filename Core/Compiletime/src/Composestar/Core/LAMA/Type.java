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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import Composestar.Core.CpsRepository2.Concern;
import Composestar.Core.LAMA.Signatures.Signature;

public abstract class Type extends ProgramElement
{
	private static final long serialVersionUID = 670886830455938414L;

	// note: it must be an implemented type and not an interface (for .net 1.1
	// deserialization)
	public ArrayList methods;

	public ArrayList fields;

	public String name;

	public String fullName;

	public boolean nestedPrivate;

	public boolean nestedPublic;

	/**
	 * The Concern counter part of this type
	 */
	protected Concern concern;

	/**
	 * The optional update signature
	 */
	protected Signature signature;

	public Type()
	{
		methods = new ArrayList();
		fields = new ArrayList();
	}

	/**
	 * @param field
	 */
	public void addField(FieldInfo field)
	{
		fields.add(field);
		field.setParent(this);
	}

	/**
	 * @param method
	 */
	public void addMethod(MethodInfo method)
	{
		methods.add(method);
		method.setParent(this);
	}

	/**
	 * @return java.util.List
	 */
	public List getFields()
	{
		return fields;
	}

	/**
	 * @return java.lang.String
	 */
	public String getFullName()
	{
		return fullName;
	}

	/**
	 * @param inName
	 */
	public void setFullName(String inName)
	{
		fullName = inName;
	}

	/**
	 * @param name
	 * @param types
	 * @return Composestar.dotnet.LAMA.DotNETMethodInfo
	 */
	public MethodInfo getMethod(String name, String[] types)
	{
		for (Iterator it = methods.iterator(); it.hasNext();)
		{
			MethodInfo mi = (MethodInfo) it.next();
			if (mi.getName().equals(name) && mi.hasParameters(types))
			{
				return mi;
			}
		}
		return null;
	}

	/**
	 * @return java.util.List
	 */
	public List getMethods()
	{
		return methods;
	}

	/**
	 * @return The updated signature of this type. Can be null when this type
	 *         does not have an updated signature.
	 */
	public Signature getSignature()
	{
		return signature;
	}

	/**
	 * Set the signature record for this type
	 * 
	 * @param value
	 */
	public void setSignature(Signature value)
	{
		signature = value;
	}

	/**
	 * @return boolean
	 */
	public boolean isNestedPrivate()
	{
		return nestedPrivate;
	}

	/**
	 * @return boolean
	 */
	public boolean isNestedPublic()
	{
		return nestedPublic;
	}

	/**
	 * @return java.lang.String
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * @param inName
	 * @roseuid 4029F83F0366
	 */
	public void setName(String inName)
	{
		name = inName;
	}

	/** Stuff for annotations * */

	public ArrayList annotationInstances = new ArrayList();

	public void addAnnotationInstance(Annotation annotation)
	{
		annotationInstances.add(annotation);
	}

	public void removeAnnotationInstance(Annotation annotation)
	{
		annotationInstances.remove(annotation);
	}

	public List getAnnotationInstances()
	{
		return annotationInstances;
	}

	// stuff for LOLA

	/**
	 * @see Composestar.Core.LAMA.ProgramElement#getUnitName()
	 */
	@Override
	public String getUnitName()
	{
		return getFullName();
	}

	/**
	 * @see Composestar.Core.LAMA.ProgramElement#hasUnitAttribute(java.lang.String)
	 */
	@Override
	public boolean hasUnitAttribute(String attribute)
	{
		return getUnitAttributes().contains(attribute);
	}

	public abstract String namespace();

	public abstract void setParentNamespace(ProgramElement parentNS);

	public abstract void addChildType(ProgramElement childType);

	public abstract void addImplementedBy(ProgramElement aClass);

	public abstract void addParameterType(ProgramElement paramType);

	public abstract void addMethodReturnType(ProgramElement returnType);

	public abstract void addFieldType(ProgramElement fieldType);

	public Concern getConcern()
	{
		return concern;
	}

	public void setConcern(Concern value)
	{
		concern = value;
	}
}
