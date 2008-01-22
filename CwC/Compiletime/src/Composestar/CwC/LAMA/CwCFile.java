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

package Composestar.CwC.LAMA;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import weavec.cmodel.declaration.ModuleDeclaration;
import Composestar.Core.LAMA.FieldInfo;
import Composestar.Core.LAMA.LangNamespace;
import Composestar.Core.LAMA.MethodInfo;
import Composestar.Core.LAMA.ProgramElement;
import Composestar.Core.LAMA.Type;
import Composestar.Core.LAMA.UnitResult;

/**
 * A CwCFile is a primitive concern. It does not actually define a type in C. It
 * is used to group all declared variables and functions in a single file, and
 * thus considered as a type.
 * 
 * @author Michiel Hendriks
 */
public class CwCFile extends Type
{
	private static final long serialVersionUID = -6978972108837895349L;

	protected ModuleDeclaration modDecl;

	protected String dirPath;

	protected LangNamespace langNamespace;

	public CwCFile()
	{
		super();
	}

	public CwCFile(ModuleDeclaration decl)
	{
		this();
		modDecl = decl;
	}

	public ModuleDeclaration getModuleDeclaration()
	{
		return modDecl;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.LAMA.Type#addChildType(Composestar.Core.LAMA.ProgramElement)
	 */
	@Override
	public void addChildType(ProgramElement childType)
	{
		throw new IllegalArgumentException("CwCFile can not contain child types");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.LAMA.Type#addFieldType(Composestar.Core.LAMA.ProgramElement)
	 */
	@Override
	public void addFieldType(ProgramElement fieldType)
	{
		throw new IllegalArgumentException("CwCFile can never be used in a field");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.LAMA.Type#addImplementedBy(Composestar.Core.LAMA.ProgramElement)
	 */
	@Override
	public void addImplementedBy(ProgramElement class1)
	{
		throw new IllegalArgumentException("CwCFile can not be implemented by");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.LAMA.Type#addMethodReturnType(Composestar.Core.LAMA.ProgramElement)
	 */
	@Override
	public void addMethodReturnType(ProgramElement returnType)
	{
		throw new IllegalArgumentException("CwCFile can never be a return type for a method");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.LAMA.Type#addParameterType(Composestar.Core.LAMA.ProgramElement)
	 */
	@Override
	public void addParameterType(ProgramElement paramType)
	{
		throw new IllegalArgumentException("CwCFile can never be used as a method parameter");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.LAMA.Type#namespace()
	 */
	@Override
	public String namespace()
	{
		return dirPath;
	}

	public void setNamespace(String path)
	{
		dirPath = path;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.LAMA.Type#setParentNamespace(Composestar.Core.LAMA.ProgramElement)
	 */
	@Override
	public void setParentNamespace(ProgramElement parentNS)
	{
		langNamespace = (LangNamespace) parentNS;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.LAMA.ProgramElement#getUnitAttributes()
	 */
	@Override
	public Collection getUnitAttributes()
	{
		return Collections.emptySet();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.LAMA.ProgramElement#getUnitRelation(java.lang.String)
	 */
	@Override
	public UnitResult getUnitRelation(String argumentName)
	{
		if ("ParentNamespace".equals(argumentName))
		{
			return new UnitResult(langNamespace);
		}
		else if ("ChildMethods".equals(argumentName))
		{
			return new UnitResult(new HashSet<MethodInfo>(methods));
		}
		else if ("ChildFields".equals(argumentName))
		{
			return new UnitResult(new HashSet<FieldInfo>(fields));
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.LAMA.ProgramElement#getUnitType()
	 */
	@Override
	public String getUnitType()
	{
		return "Class";
	}

}
