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

import Composestar.Core.CpsRepository2.TypeSystem.CpsVariable;
import Composestar.Core.CpsRepository2Impl.FilterElements.AnnotationMatching;
import Composestar.Java.FLIRT.FLIRTConstants;
import Composestar.Java.FLIRT.Interpreter.FilterExecutionContext;

/**
 * @author Michiel Hendriks
 */
public class AnnotationMatchingInterp extends CompareOperatorInterpreter<AnnotationMatching>
{
	public static final Logger logger = Logger.getLogger(FLIRTConstants.INTERPRETER + ".MatchAnnot");

	/*
	 * (non-Javadoc)
	 * @seeComposestar.Java.FLIRT.Interpreter.CompareOperators.
	 * CompareOperatorInterpreter#acceptsClass()
	 */
	@Override
	public Class<AnnotationMatching> acceptsClass()
	{
		return AnnotationMatching.class;
	}

	/*
	 * (non-Javadoc)
	 * @seeComposestar.Java.FLIRT.Interpreter.CompareOperators.
	 * CompareOperatorInterpreter
	 * #matches(Composestar.Core.CpsRepository2.TypeSystem.CpsVariable,
	 * Composestar.Core.CpsRepository2.TypeSystem.CpsVariable,
	 * Composestar.Java.FLIRT.Interpreter.FilterExecutionContext)
	 */
	@Override
	public boolean matches(CpsVariable lhs, CpsVariable rhs, FilterExecutionContext context)
	{
		throw new UnsupportedOperationException("Annotation matching has not been implemented");
	}
}
