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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

//
// !! Compose* Runtime Warning !!
//
// This class is referenced in the Compose* Runtime for .NET 1.1
// Do not use Java features added after Java 2.0
//

public abstract class Type extends ProgramElement
{
	// note: it must be an implemented type and not an interface (for .net 1.1
	// deserialization)
	public ArrayList methods;

	public ArrayList fields;

	public String name;

	public String fullName;

	public boolean nestedPrivate;

	public boolean nestedPublic;

	public Type()
	{
		UnitRegister.instance().registerLanguageUnit(this);
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
		this.annotationInstances.add(annotation);
	}

	public void removeAnnotationInstance(Annotation annotation)
	{
		this.annotationInstances.remove(annotation);
	}

	public List getAnnotationInstances()
	{
		return this.annotationInstances;
	}

	// stuff for LOLA

	/**
	 * @see Composestar.Core.LAMA.ProgramElement#getUnitName()
	 */
	public String getUnitName()
	{
		return getFullName();
	}

	/**
	 * @see Composestar.Core.LAMA.ProgramElement#hasUnitAttribute(java.lang.String)
	 */
	public boolean hasUnitAttribute(String attribute)
	{
		return getUnitAttributes().contains(attribute);
	}
}
