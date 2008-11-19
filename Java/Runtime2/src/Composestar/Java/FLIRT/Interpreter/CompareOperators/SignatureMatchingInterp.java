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

import java.util.List;
import java.util.logging.Logger;

import Composestar.Core.CpsRepository2.TypeSystem.CpsObject;
import Composestar.Core.CpsRepository2.TypeSystem.CpsProgramElement;
import Composestar.Core.CpsRepository2.TypeSystem.CpsSelector;
import Composestar.Core.CpsRepository2.TypeSystem.CpsVariable;
import Composestar.Core.CpsRepository2Impl.FilterElements.SignatureMatching;
import Composestar.Core.CpsRepository2Impl.TypeSystem.CpsSelectorMethodInfo;
import Composestar.Core.LAMA.MethodInfo;
import Composestar.Core.LAMA.ProgramElement;
import Composestar.Core.LAMA.Type;
import Composestar.Core.LAMA.Signatures.MethodInfoWrapper;
import Composestar.Core.LAMA.Signatures.MethodRelation;
import Composestar.Core.LAMA.Signatures.MethodStatus;
import Composestar.Core.LAMA.Signatures.Signature;
import Composestar.Java.FLIRT.FLIRTConstants;
import Composestar.Java.FLIRT.Interpreter.FilterExecutionContext;
import Composestar.Java.FLIRT.Utils.Invoker;

/**
 * @author Michiel Hendriks
 */
public class SignatureMatchingInterp extends CompareOperatorInterpreter<SignatureMatching>
{
	public static final Logger logger = Logger.getLogger(FLIRTConstants.INTERPRETER + ".MatchSign");

	/*
	 * (non-Javadoc)
	 * @seeComposestar.Java.FLIRT.Interpreter.CompareOperators.
	 * CompareOperatorInterpreter#acceptsClass()
	 */
	@Override
	public Class<SignatureMatching> acceptsClass()
	{
		return SignatureMatching.class;
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
		if (!(lhs instanceof CpsSelector))
		{
			// only selectors are allowed on the lhs
			return false;
		}
		if (context.getMessage().getTarget() == null)
		{
			// not even possible
			return false;
		}
		if (!(rhs instanceof CpsProgramElement))
		{
			// right hand side must be a type program elements
			return false;
		}

		// special case for inner targets, check the actual type
		if (rhs instanceof CpsObject && ((CpsObject) rhs).isInnerObject())
		{
			MethodInfo mi = null;
			if (lhs instanceof CpsSelectorMethodInfo)
			{
				mi = ((CpsSelectorMethodInfo) lhs).getMethodInfo();;
			}
			return Invoker.objectHasMethod(((CpsObject) rhs).getObject(), ((CpsSelector) lhs).getName(), context
					.getMessage().getArguments(), mi);
		}

		ProgramElement pe = ((CpsProgramElement) rhs).getProgramElement();
		if (!(pe instanceof Type))
		{
			// should have been a type
			return false;
		}
		Type targetType = (Type) pe;

		Signature sig = targetType.getSignature();
		List<MethodInfo> ms = null;
		if (sig == null)
		{
			ms = targetType.getMethods();
		}

		if (lhs instanceof CpsSelectorMethodInfo)
		{
			// we know the MethodInfo we want to check
			return hasMethod(((CpsSelectorMethodInfo) lhs).getMethodInfo(), sig, ms);
		}

		Type baseType = context.getMessage().getTarget().getTypeReference().getReference();
		if (baseType == null)
		{
			// shouldn't even happen
			return false;
		}

		// go through all methods in the base type with a given name
		Signature baseSig = baseType.getSignature();
		String selector = ((CpsSelector) lhs).getName();
		if (baseSig != null)
		{
			for (MethodInfoWrapper wrap : baseSig.getMethodInfoWrapper(selector))
			{
				if (wrap.getStatus() == MethodStatus.EXISTING && wrap.getRelation() != MethodRelation.REMOVED)
				{
					if (hasMethod(wrap.getMethodInfo(), sig, ms))
					{
						return true;
					}
				}
			}
		}
		else
		{
			for (MethodInfo m : baseType.getMethods())
			{
				if (selector.equals(m.getName()) && hasMethod(m, sig, ms))
				{
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * True if the signature method is in the signature or methodinfo list
	 * 
	 * @param sigMethod
	 * @param sig
	 * @param mis
	 * @return
	 */
	protected boolean hasMethod(MethodInfo sigMethod, Signature sig, List<MethodInfo> mis)
	{
		if (sig != null)
		{
			MethodInfoWrapper wrap = sig.getMethodInfoWrapper(sigMethod);
			if (wrap == null)
			{
				return false;
			}
			return wrap.getStatus() == MethodStatus.EXISTING && wrap.getRelation() != MethodRelation.REMOVED;
		}
		else
		{
			for (MethodInfo m : mis)
			{
				if (m.checkEquals(sigMethod))
				{
					return true;
				}
			}
		}
		return false;
	}
}
