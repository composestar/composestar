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

import java.util.List;

import Composestar.Core.CpsRepository2.Filters.FilterActionNames;
import Composestar.Core.CpsRepository2Impl.TypeSystem.CpsSelectorMethodInfo;
import Composestar.Core.INLINE.lowlevel.InlinerResources;
import Composestar.Core.INLINE.model.FilterActionInstruction;
import Composestar.Core.LAMA.MethodInfo;
import Composestar.Core.LAMA.ParameterInfo;
import Composestar.Core.LAMA.Type;
import Composestar.Core.LAMA.Signatures.MethodInfoWrapper;
import Composestar.Core.LAMA.Signatures.MethodRelation;
import Composestar.Core.LAMA.Signatures.Signature;

/**
 * Produces the code for a dispatch action
 * 
 * @author Michiel Hendriks
 */
public abstract class DispatchActionCodeGen implements FilterActionCodeGenerator<String>
{
	protected InlinerResources inlinerResources;

	public DispatchActionCodeGen()
	{}

	public DispatchActionCodeGen(InlinerResources inresc)
	{
		setInlinerResources(inresc);
	}

	public void setInlinerResources(InlinerResources resources)
	{
		inlinerResources = resources;
	}

	/*
	 * (non-Javadoc)
	 * @seeComposestar.Core.INLINE.CodeGen.FilterActionCodeGenerator#generate(
	 * Composestar.Core.INLINE.CodeGen.CodeGenerator,
	 * Composestar.Core.INLINE.model.FilterAction)
	 */
	public String generate(CodeGenerator<String> codeGen, FilterActionInstruction action)
	{
		MethodInfo currentMethod = codeGen.getCurrentMethod();

		Type targetType = action.getMessage().getTarget().getTypeReference().getReference();
		if (targetType == null)
		{
			throw new RuntimeException("Could not resolve the type of the target of the dispatch action");
		}

		List<ParameterInfo> paramInfo = currentMethod.getParameters();
		String[] params = new String[paramInfo.size()];
		for (int i = 0; i < paramInfo.size(); i++)
		{
			ParameterInfo pi = paramInfo.get(i);
			params[i] = pi.getParameterTypeString();
		}
		Signature sig = targetType.getSignature();
		MethodInfo method = null;
		if (sig == null)
		{
			method = targetType.getMethod(action.getMessage().getSelector().getName(), params);
		}
		else
		{
			if (action.getMessage().getSelector() instanceof CpsSelectorMethodInfo)
			{
				MethodInfoWrapper wrap =
						sig.getMethodInfoWrapper(((CpsSelectorMethodInfo) action.getMessage().getSelector())
								.getMethodInfo());
				if (wrap.getRelation() == MethodRelation.ADDED || wrap.getRelation() == MethodRelation.NORMAL)
				{
					method = wrap.getMethodInfo();
				}
			}
			if (method == null)
			{
				for (MethodInfoWrapper wrap : sig.getMethodInfoWrapper(action.getMessage().getSelector().getName()))
				{
					if (wrap.getRelation() == MethodRelation.ADDED || wrap.getRelation() == MethodRelation.NORMAL)
					{
						if (wrap.getMethodInfo().hasParameters(params))
						{
							method = wrap.getMethodInfo();
						}
					}
				}
			}
		}
		List<String> args = getJpcArguments(paramInfo);
		Object context = null;
		String prefix = "";

		// TODO: resolve target and context (i.e. the variable that was created,
		// which would be the name of the DeclaredPbjectReference)

		if (action.getMessage().getTarget().isInnerObject())
		{
			prefix = codeGen.emitSetInnerCall(inlinerResources.getMethodId(method));
		}
		return emitAction(codeGen.emitMethodCall(method, args, context), prefix, codeGen.hasReturnValue(method));
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.INLINE.CodeGen.FilterActionCodeGenerator#supportedTypes
	 * ()
	 */
	public String[] supportedTypes()
	{
		String[] types = { FilterActionNames.DISPATCH_ACTION, FilterActionNames.SEND_ACTION };
		return types;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.INLINE.CodeGen.FilterActionCodeGenerator#methodInit(
	 * Composestar.Core.INLINE.CodeGen.CodeGenerator,
	 * Composestar.Core.INLINE.model.FilterAction)
	 */
	public String generateMethodInit(CodeGenerator<String> codeGen, FilterActionInstruction action)
	{
		return null;
	}

	/**
	 * Produces a list of method arguments based on the provided parameter
	 * signature
	 * 
	 * @param parameters
	 * @return
	 */
	protected abstract List<String> getJpcArguments(List<ParameterInfo> parameters);

	/**
	 * Emit the actual code for the dispatch action
	 * 
	 * @param methodCall the method call code
	 * @param prefix the prefix (call to the set_inner_call method)
	 * @param hasReturn if true store the return variable
	 * @return
	 */
	protected abstract String emitAction(String methodCall, String prefix, boolean hasReturn);
}
