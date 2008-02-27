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

package CachingFilters;

import java.util.HashSet;
import java.util.Set;

import Composestar.Core.INLINE.CodeGen.CodeGenerator;
import Composestar.Core.INLINE.CodeGen.FilterActionCodeGenerator;
import Composestar.Core.INLINE.lowlevel.InlinerResources;
import Composestar.Core.INLINE.model.FilterAction;
import Composestar.Core.LAMA.MethodInfo;
import Composestar.Core.LAMA.ParameterInfo;

/**
 * @author Michiel Hendriks
 */
public class CachingFilterActionCodeGen implements FilterActionCodeGenerator<String>
{

	public CachingFilterActionCodeGen()
	{}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.INLINE.CodeGen.FilterActionCodeGenerator#generate(Composestar.Core.INLINE.CodeGen.CodeGenerator,
	 *      Composestar.Core.INLINE.model.FilterAction)
	 */
	public String generate(CodeGenerator<String> codeGen, FilterAction action)
	{
		MethodInfo mi = codeGen.getCurrentMethod();
		if (!codeGen.hasReturnValue(mi))
		{
			return null;
		}
		if (mi.getParameters().size() != 1)
		{
			return null;
		}
		int methodId = codeGen.getCurrentMethodId();
		String arg0 = ((ParameterInfo) mi.getParameters().get(0)).getName();

		if (Caching.CACHE_ACTION.equals(action.getType()))
		{
			StringBuffer sb = new StringBuffer();
			sb.append(String.format("if (CSTAR_is_cached(%d, %s)) {\n", methodId, arg0));
			sb.append(String.format("CSTAR_get_cache(%d, %s, &returnValue);\n", methodId, arg0));
			sb.append(String.format("return returnValue;\n", methodId, arg0));
			sb.append("}");
			return sb.toString();
		}
		else if (Caching.CACHE_RETURN_ACTION.equals(action.getType()))
		{
			return String.format("CSTAR_set_cache(%d, %s, returnValue);\n", methodId, arg0);
		}
		else if (Caching.INVALIDATE_ACTION.equals(action.getType()))
		{
			return String.format("CSTAR_clear_cache();\n");
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.INLINE.CodeGen.FilterActionCodeGenerator#generateMethodInit(Composestar.Core.INLINE.CodeGen.CodeGenerator,
	 *      Composestar.Core.INLINE.model.FilterAction)
	 */
	public String generateMethodInit(CodeGenerator<String> codeGen, FilterAction action)
	{
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.INLINE.CodeGen.FilterActionCodeGenerator#getDependencies(Composestar.Core.INLINE.CodeGen.CodeGenerator,
	 *      java.lang.String)
	 */
	public Set<String> getDependencies(CodeGenerator<String> codeGen, String action)
	{
		// TODO: caching.o
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.INLINE.CodeGen.FilterActionCodeGenerator#getImports(Composestar.Core.INLINE.CodeGen.CodeGenerator,
	 *      java.lang.String)
	 */
	public Set<String> getImports(CodeGenerator<String> codeGen, String action)
	{
		Set<String> res = new HashSet<String>();
		res.add("\"caching.h\"");
		return res;
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
	 * @see Composestar.Core.INLINE.CodeGen.FilterActionCodeGenerator#supportedTypes()
	 */
	public String[] supportedTypes()
	{
		String[] res = { Caching.CACHE_ACTION, Caching.CACHE_RETURN_ACTION, Caching.INVALIDATE_ACTION };
		return res;
	}

}
