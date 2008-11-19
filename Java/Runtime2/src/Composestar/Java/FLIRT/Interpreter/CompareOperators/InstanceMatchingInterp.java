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

package Composestar.Java.FLIRT.Interpreter.CompareOperators;

import java.util.logging.Logger;

import Composestar.Core.CpsRepository2.TypeSystem.CpsLiteral;
import Composestar.Core.CpsRepository2.TypeSystem.CpsObject;
import Composestar.Core.CpsRepository2.TypeSystem.CpsProgramElement;
import Composestar.Core.CpsRepository2.TypeSystem.CpsSelector;
import Composestar.Core.CpsRepository2.TypeSystem.CpsVariable;
import Composestar.Core.CpsRepository2Impl.FilterElements.InstanceMatching;
import Composestar.Core.CpsRepository2Impl.TypeSystem.CpsSelectorMethodInfo;
import Composestar.Core.LAMA.MethodInfo;
import Composestar.Core.LAMA.ProgramElement;
import Composestar.Java.FLIRT.FLIRTConstants;
import Composestar.Java.FLIRT.Interpreter.FilterExecutionContext;

/**
 * @author Michiel Hendriks
 */
public class InstanceMatchingInterp extends CompareOperatorInterpreter<InstanceMatching>
{
	public static final Logger logger = Logger.getLogger(FLIRTConstants.INTERPRETER + ".MatchInst");

	/*
	 * (non-Javadoc)
	 * @seeComposestar.Java.FLIRT.Interpreter.CompareOperators.
	 * CompareOperatorInterpreter#acceptsClass()
	 */
	@Override
	public Class<InstanceMatching> acceptsClass()
	{
		return InstanceMatching.class;
	}

	/*
	 * (non-Javadoc)
	 * @seeComposestar.Java.FLIRT.Interpreter.CompareOperators.
	 * CompareOperatorInterpreter
	 * #matches(Composestar.Core.CpsRepository2.TypeSystem.CpsVariable,
	 * Composestar.Core.CpsRepository2.TypeSystem.CpsVariable)
	 */
	@Override
	public boolean matches(CpsVariable lhs, CpsVariable rhs, FilterExecutionContext context)
	{
		if (lhs instanceof CpsObject)
		{
			// must be same objects
			return lhs == rhs;
		}
		else if (lhs instanceof CpsSelector && rhs instanceof CpsSelector)
		{
			if (lhs instanceof CpsSelectorMethodInfo && rhs instanceof CpsSelectorMethodInfo)
			{
				// special case, if both selectors are method info selectors,
				// compare the method infos
				if (((CpsSelectorMethodInfo) lhs).getMethodInfo().checkEquals(
						((CpsSelectorMethodInfo) rhs).getMethodInfo()))
				{
					return true;
				}
			}
			// name matching on CpsSelector
			if (((CpsSelector) lhs).getName().equals(((CpsSelector) rhs).getName()))
			{
				return true;
			}
		}
		else if (lhs instanceof CpsSelector && rhs instanceof CpsLiteral)
		{
			if (((CpsSelector) lhs).getName().equals(((CpsLiteral) rhs).getLiteralValue()))
			{
				return true;
			}
		}
		else if (lhs instanceof CpsSelector && rhs instanceof CpsProgramElement)
		{
			ProgramElement pe = ((CpsProgramElement) rhs).getProgramElement();
			if (pe instanceof MethodInfo)
			{
				if (lhs instanceof CpsSelectorMethodInfo)
				{
					if (((CpsSelectorMethodInfo) lhs).getMethodInfo().checkEquals((MethodInfo) pe))
					{
						return true;
					}
				}
				else
				{
					if (((CpsSelector) lhs).getName().equals(((MethodInfo) pe).getName()))
					{
						return true;
					}
				}
			}
		}
		return false;
	}
}
