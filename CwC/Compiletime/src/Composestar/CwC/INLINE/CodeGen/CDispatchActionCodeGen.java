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

import java.util.ArrayList;
import java.util.List;

import Composestar.Core.INLINE.CodeGen.DispatchActionCodeGen;
import Composestar.Core.INLINE.lowlevel.InlinerResources;
import Composestar.Core.LAMA.ParameterInfo;

/**
 * The C implementation of the DispatchAction code generator
 * 
 * @author Michiel Hendriks
 */
public class CDispatchActionCodeGen extends DispatchActionCodeGen
{

	public CDispatchActionCodeGen(InlinerResources inresc)
	{
		super(inresc);
	}

	@Override
	protected List<String> getJpcArguments(List<ParameterInfo> parameters)
	{
		List<String> result = new ArrayList<String>();
		for (int i = 0; i < parameters.size(); i++)
		{
			result.add(parameters.get(i).getName());
		}
		return result;
	}

	@Override
	protected String emitAction(String methodCall, String prefix, boolean hasReturn)
	{
		if (hasReturn)
		{
			return prefix + "returnValue" + " = " + methodCall + ";\n";
		}
		else
		{
			return prefix + methodCall + ";\n";
		}
	}

}
