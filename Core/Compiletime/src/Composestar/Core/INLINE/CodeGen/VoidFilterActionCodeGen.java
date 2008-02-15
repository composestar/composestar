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

package Composestar.Core.INLINE.CodeGen;

import java.util.Set;

import Composestar.Core.INLINE.lowlevel.InlinerResources;
import Composestar.Core.INLINE.model.FilterAction;

/**
 * A code generator that does not produce any code.
 * 
 * @author Michiel Hendriks
 */
public class VoidFilterActionCodeGen<T> implements FilterActionCodeGenerator<T>
{
	protected String[] filterTypes;

	/**
	 * @param types
	 */
	public VoidFilterActionCodeGen(String[] types)
	{
		filterTypes = types;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.INLINE.CodeGen.FilterActionCodeGenerator#generate(Composestar.Core.INLINE.CodeGen.CodeGenerator,
	 *      Composestar.Core.INLINE.model.FilterAction)
	 */
	public T generate(CodeGenerator<T> codeGen, FilterAction action)
	{
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.INLINE.CodeGen.FilterActionCodeGenerator#supportedTypes()
	 */
	public String[] supportedTypes()
	{
		return filterTypes;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.INLINE.CodeGen.FilterActionCodeGenerator#setInlinerResources(Composestar.Core.INLINE.lowlevel.InlinerResources)
	 */
	public void setInlinerResources(InlinerResources resources)
	{}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.INLINE.CodeGen.FilterActionCodeGenerator#methodInit(Composestar.Core.INLINE.CodeGen.CodeGenerator,
	 *      Composestar.Core.INLINE.model.FilterAction)
	 */
	public T generateMethodInit(CodeGenerator<T> codeGen, FilterAction action)
	{
		return null;
	}

	public Set<String> getDependencies(CodeGenerator<T> codeGen, String action)
	{
		return null;
	}

	public Set<String> getImports(CodeGenerator<T> codeGen, String action)
	{
		return null;
	}

}
