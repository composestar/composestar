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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import Composestar.Core.LOLA.metamodel.ERelationType;
import Composestar.Core.LOLA.metamodel.EUnitType;

/**
 * Namespace unit type
 * 
 * @author havingaw
 */
public class LangNamespace extends ProgramElement
{
	private static final long serialVersionUID = -9184363245232975615L;

	private HashSet childClasses;

	private HashSet childInterfaces;

	private LangNamespace parentNamespace;

	private HashSet childNamespaces;

	private String name;

	/**
	 * @param id
	 */
	public LangNamespace()
	{
		this(null);
	}

	public LangNamespace(String inName)
	{
		name = inName;
		childClasses = new HashSet();
		childInterfaces = new HashSet();
		parentNamespace = null;
		childNamespaces = new HashSet();
	}

	/**
	 * @return Hashset containing LangType children of this namespace
	 */
	public HashSet getChildClasses()
	{
		return childClasses;
	}

	public LangNamespace getParentNamespace()
	{
		return parentNamespace;
	}

	/**
	 * @param inParentNamespace The parentNamespace to set.
	 */
	public void setParentNamespace(LangNamespace inParentNamespace)
	{
		parentNamespace = inParentNamespace;
	}

	/**
	 * @param inChildClasses The childClasses to set.
	 */
	public void setChildClasses(HashSet inChildClasses)
	{
		childClasses = inChildClasses;
	}

	/**
	 * @return Returns the childInterfaces.
	 */
	public HashSet getChildInterfaces()
	{
		return childInterfaces;
	}

	/**
	 * @param inChildInterfaces The childInterfaces to set.
	 */
	public void setChildInterfaces(HashSet inChildInterfaces)
	{
		childInterfaces = inChildInterfaces;
	}

	public void addChildClass(ProgramElement childClass)
	{
		childClasses.add(childClass);
	}

	public void addChildInterface(ProgramElement childInterface)
	{
		childInterfaces.add(childInterface);
	}

	/**
	 * @return Returns the childNamespaces.
	 */
	public Set getChildNamespaces()
	{
		return childNamespaces;
	}

	/**
	 * @param childNamespaces The childNamespaces to set.
	 */
	public void setChildNamespaces(HashSet childNamespaces)
	{
		this.childNamespaces = childNamespaces;
	}

	public void addChildNamespace(LangNamespace childNamespace)
	{
		childNamespaces.add(childNamespace);
	}

	@Override
	public String toString()
	{
		return "LangNamespace(name=" + getUnitName() + ')';
	}

	/**
	 * @param name The name to set.
	 */
	public void setUnitName(String name)
	{
		this.name = name;
	}

	/** Stuff for LOLA* */

	/**
	 * @return Returns the unit name.
	 */
	@Override
	public String getUnitName()
	{
		return name;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.LAMA.ProgramElement#getUnitRelation(java.lang.String)
	 */

	@Override
	public UnitResult getUnitRelation(String argumentName)
	{
		if (ERelationType.CHILD_NAMESPACES.equals(argumentName))
		{
			return new UnitResult(childNamespaces);
		}
		else if (ERelationType.PARENT_NAMESPACE.equals(argumentName))
		{
			return new UnitResult(parentNamespace);
		}
		else if (ERelationType.CHILD_CLASSES.equals(argumentName))
		{
			return new UnitResult(childClasses);
		}
		else if (ERelationType.CHILD_INTERFACES.equals(argumentName))
		{
			return new UnitResult(childInterfaces);
		}
		else
		{
			System.err.println("LangNamespace: unknown unit relation: " + argumentName);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Core.LAMA.ProgramElement#getUnitType()
	 */
	@Override
	public String getUnitType()
	{
		return EUnitType.NAMESPACE.toString();
	}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Core.LAMA.ProgramElement#hasAttribute(java.lang.String)
	 */
	@Override
	public boolean hasUnitAttribute(String attribute)
	{
		// Namespaces don't have any attributes because they're not 'real' units
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Core.LAMA.ProgramElement#getUnitAttributes()
	 */
	@Override
	public Collection getUnitAttributes()
	{
		return new HashSet();
	}
}
