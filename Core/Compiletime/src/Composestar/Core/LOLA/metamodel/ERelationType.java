/*
 * This file is part of the Compose* project.
 * http://composestar.sourceforge.net
 * Copyright (C) 2008 University of Twente.
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

package Composestar.Core.LOLA.metamodel;

/**
 * @author Michiel Hendriks
 */
public enum ERelationType
{
	TYPE("Type"),

	// Annotations
	ANNOTATIONS("Annotations"), ATTACHED_CLASSES("AttachedClasses"), ATTACHED_INTERFACES("AttachedInterfaces"),
	ATTACHED_METHODS("AttachedMethods"),
	ATTACHED_FIELDS("AttachedFields"),
	ATTACHED_PARAMETERS("AttachedParameters"),

	// Namespace
	PARENT_NAMESPACE("ParentNamespace"),

	// Class
	CLASS("Class"), CHILD_CLASSES("ChildClasses"), PARENT_CLASS("ParentClass"), CHILD_METHODS("ChildMethods"),
	CHILD_FIELDS("ChildFields"), PARAMETER_CLASS("ParameterClass"), METHOD_RETURN_CLASS("MethodReturnClass"),
	FIELD_CLASS("FieldClass"), IMPLEMENTS("Implements"),

	// Interface
	INTERFACE("Interface"), CHILD_INTERFACES("ChildInterfaces"), PARENT_INTERFACE("ParentInterface"), IMPLEMENTED_BY(
			"ImplementedBy"), PARAMETER_INTERFACE("ParameterInterface"), METHOD_RETURN_INTERFACE(
			"MethodReturnInterface"), FIELD_INTERFACE("FieldInterface"),

	// Method
	CHILD_PARAMETERS("ChildParameters"), RETURN_CLASS("ReturnClass"), RETURN_INTERFACE("ReturnInterface"), RETURN_TYPE(
			"ReturnType"),

	// Parameter
	PARENT_METHOD("ParentMethod");

	private String name;

	ERelationType(String name)
	{
		this.name = name;
	}

	@Override
	public String toString()
	{
		return name;
	}

	public boolean equals(String other)
	{
		return name.equals(other);
	}
}
