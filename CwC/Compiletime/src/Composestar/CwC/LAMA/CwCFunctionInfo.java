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
import java.util.Set;

import weavec.cmodel.declaration.FunctionDeclaration;
import Composestar.Core.LAMA.MethodInfo;
import Composestar.Core.LAMA.ProgramElement;
import Composestar.Core.LAMA.Type;
import Composestar.Core.LAMA.UnitResult;

/**
 * Defines the method info for the defined C functions.
 * 
 * @author Michiel Hendriks
 */
public class CwCFunctionInfo extends MethodInfo
{
	private static final long serialVersionUID = -2567532950309340694L;

	protected FunctionDeclaration funcDecl;

	/**
	 * If true this method has a varargs construction
	 */
	protected boolean varargs;

	public CwCFunctionInfo()
	{
		super();
	}

	public CwCFunctionInfo(FunctionDeclaration decl)
	{
		this();
		funcDecl = decl;
		setName(funcDecl.getName());
	}

	public FunctionDeclaration getFunctionDeclaration()
	{
		return funcDecl;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.LAMA.MethodInfo#getClone(java.lang.String,
	 *      Composestar.Core.LAMA.Type)
	 */
	@Override
	public MethodInfo getClone(String name, Type actualParent)
	{
		CwCFunctionInfo mi = new CwCFunctionInfo(funcDecl);

		mi.setName(name);

		// set MethodInfo variables
		mi.parent = actualParent;
		mi.parameters = parameters;
		mi.returnType = returnType;
		mi.returnTypeString = returnTypeString;

		return mi;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.LAMA.MethodInfo#isDeclaredHere()
	 */
	@Override
	public boolean isDeclaredHere()
	{
		return true; // no "overloading"
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.LAMA.MethodInfo#isPrivate()
	 */
	@Override
	public boolean isPrivate()
	{
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.LAMA.MethodInfo#isProtected()
	 */
	@Override
	public boolean isProtected()
	{
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.LAMA.MethodInfo#isPublic()
	 */
	@Override
	public boolean isPublic()
	{
		return true; // methods are always public
	}

	public void setVarArgs(boolean value)
	{
		varargs = value;
	}

	public boolean hasVarArgs()
	{
		return varargs;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.LAMA.ProgramElement#getUnitAttributes()
	 */
	@Override
	public Collection getUnitAttributes()
	{
		Set<String> res = new HashSet<String>();
		res.add("public");
		if (varargs)
		{
			res.add("varargs");
		}
		return res;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.LAMA.ProgramElement#getUnitRelation(java.lang.String)
	 */
	@Override
	public UnitResult getUnitRelation(String argumentName)
	{
		if ("ParentClass".equals(argumentName) && "Class".equals(parent.getUnitType()))
		{
			return new UnitResult(parent);
		}
		else if ("ChildParameters".equals(argumentName))
		{
			return new UnitResult(new HashSet<ProgramElement>(parameters));
		}
		else if ("ReturnClass".equals(argumentName) && "Class".equals(getReturnType().getUnitType()))
		{
			// this is never true, return type is of unittype "Type"
			return new UnitResult(getReturnType());
		}
		else if ("ReturnType".equals(argumentName) && "Type".equals(getReturnType().getUnitType()))
		{
			// this is never true, return type is of unittype "Type"
			return new UnitResult(getReturnType());
		}
		return new UnitResult(Collections.emptySet());
	}

}
