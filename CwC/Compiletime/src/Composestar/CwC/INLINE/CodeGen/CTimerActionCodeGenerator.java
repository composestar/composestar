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

package Composestar.CwC.INLINE.CodeGen;

import java.util.HashSet;
import java.util.Set;

import Composestar.Core.INLINE.CodeGen.CodeGenerator;
import Composestar.Core.INLINE.CodeGen.FilterActionCodeGenerator;
import Composestar.Core.INLINE.lowlevel.InlinerResources;
import Composestar.Core.INLINE.model.FilterAction;
import Composestar.CwC.Filters.ExtraFilters;

/**
 * @author Michiel Hendriks
 */
public class CTimerActionCodeGenerator implements FilterActionCodeGenerator<String>
{
	public CTimerActionCodeGenerator()
	{}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.INLINE.CodeGen.FilterActionCodeGenerator#generate(Composestar.Core.INLINE.CodeGen.CodeGenerator,
	 *      Composestar.Core.INLINE.model.FilterAction)
	 */
	public String generate(CodeGenerator<String> codeGen, FilterAction action)
	{
		if (action.getType().equals(ExtraFilters.TIMER_START_ACTION))
		{
			return String.format("timer_%s = clock();\n", System.identityHashCode(codeGen.getCurrentMethod()));
		}
		else if (action.getType().equals(ExtraFilters.TIMER_STOP_ACTION))
		{
			StringBuffer sb = new StringBuffer();
			sb.append("printf(\"!!! Function \\\"");
			sb.append(codeGen.getCurrentMethod().getName());
			sb.append("\\\" in module \\\"");
			sb.append(codeGen.getCurrentMethod().parent().getName());
			sb.append("\\\" took %ld clock ticks to execute\\n\", (clock() - timer_");
			sb.append(System.identityHashCode(codeGen.getCurrentMethod()));
			sb.append("));\n");
			return sb.toString();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.INLINE.CodeGen.FilterActionCodeGenerator#methodInit(Composestar.Core.INLINE.CodeGen.CodeGenerator,
	 *      Composestar.Core.INLINE.model.FilterAction)
	 */
	public String generateMethodInit(CodeGenerator<String> codeGen, FilterAction action)
	{
		if (action.getType().equals(ExtraFilters.TIMER_START_ACTION))
		{
			return String.format("long timer_%s = 0;\n", System.identityHashCode(codeGen.getCurrentMethod()));
		}
		return null;
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
		String[] result = { ExtraFilters.TIMER_START_ACTION, ExtraFilters.TIMER_STOP_ACTION };
		return result;
	}

	public Set<String> getDependencies(CodeGenerator<String> codeGen, String action)
	{
		return null;
	}

	public Set<String> getImports(CodeGenerator<String> codeGen, String action)
	{
		Set<String> result = new HashSet<String>();
		result.add("<time.h>");
		result.add("<stdio.h>");
		return result;
	}

}
