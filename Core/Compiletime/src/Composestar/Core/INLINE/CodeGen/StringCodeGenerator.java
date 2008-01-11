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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.BinaryOperator;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.CondLiteral;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Condition;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.ConditionExpression;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.ConditionVariable;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Target;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.UnaryOperator;
import Composestar.Core.CpsProgramRepository.CpsConcern.References.ConcernReference;
import Composestar.Core.CpsProgramRepository.CpsConcern.References.DeclaredObjectReference;
import Composestar.Core.CpsProgramRepository.CpsConcern.References.Reference;
import Composestar.Core.INLINE.model.Block;
import Composestar.Core.INLINE.model.Branch;
import Composestar.Core.INLINE.model.FilterAction;
import Composestar.Core.INLINE.model.FilterCode;
import Composestar.Core.INLINE.model.Instruction;
import Composestar.Core.INLINE.model.Jump;
import Composestar.Core.LAMA.MethodInfo;
import Composestar.Core.LAMA.Type;
import Composestar.Utils.Logging.CPSLogger;

// FIXME: change the runtime exception to a more specific runtime exception
// subclass
/**
 * Produces string source code.
 * 
 * @author Michiel Hendriks
 */
public abstract class StringCodeGenerator implements CodeGenerator<String>
{
	protected static final CPSLogger logger = CPSLogger.getCPSLogger(MODULE_NAME);

	protected MethodInfo method;

	protected int methodId;

	protected List<FilterAction> returnActions;

	protected Map<String, FilterActionCodeGenerator<String>> faCodeGens;

	public StringCodeGenerator()
	{
		faCodeGens = new HashMap<String, FilterActionCodeGenerator<String>>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.INLINE.CodeGen.CodeGenerator#generate(Composestar.Core.INLINE.model.FilterCode,
	 *      Composestar.Core.LAMA.MethodInfo, int)
	 */
	public String generate(FilterCode code, MethodInfo currentMethod, int currentMethodId)
	{
		method = currentMethod;
		methodId = currentMethodId;
		returnActions = new ArrayList<FilterAction>();
		return code.accept(this).toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.INLINE.CodeGen.CodeGenerator#getCurrentMethod()
	 */
	public MethodInfo getCurrentMethod()
	{
		return method;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.INLINE.model.Visitor#visitBlock(Composestar.Core.INLINE.model.Block)
	 */
	public Object visitBlock(Block block)
	{
		StringBuffer sb = new StringBuffer();
		for (Instruction i : block.getInstructionsEx())
		{
			Object ic = i.accept(this);
			if (ic != null && ic.toString().trim().length() > 0)
			{
				sb.append(ic.toString());
			}
		}
		if (sb.length() == 0)
		{
			return null;
		}
		return emitBlock(sb.toString());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.INLINE.model.Visitor#visitBranch(Composestar.Core.INLINE.model.Branch)
	 */
	public Object visitBranch(Branch branch)
	{
		String condition = null;
		if (branch.getConditionExpression() != null)
		{
			condition = visitConditionExpression(branch.getConditionExpression());
		}
		else if (branch.getCondition() != null)
		{
			condition = emitCondition(branch.getCondition());
		}
		String trueBranch = null;
		if (branch.getTrueBlock() != null)
		{
			trueBranch = (String) branch.getTrueBlock().accept(this);
		}
		String falseBranch = null;
		if (branch.getFalseBlock() != null)
		{
			falseBranch = (String) branch.getFalseBlock().accept(this);
		}
		return emitBranch(condition, trueBranch, falseBranch);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.INLINE.model.Visitor#visitFilterAction(Composestar.Core.INLINE.model.FilterAction)
	 */
	public Object visitFilterAction(FilterAction filterAction)
	{
		if (!filterAction.isOnCall())
		{
			int idx = returnActions.size();
			returnActions.add(filterAction);
			return emitReturnFilterAction(idx, filterAction);
		}
		return emitFilterAction(filterAction);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.INLINE.model.Visitor#visitFilterCode(Composestar.Core.INLINE.model.FilterCode)
	 */
	public Object visitFilterCode(FilterCode filterCode)
	{
		String fmConditions = null;
		List<Condition> fmConds = filterCode.getCheckConditionsEx();
		if (fmConds != null && fmConds.size() > 0)
		{
			fmConditions = emitCondition(fmConds.get(0));
			for (int i = 1; i < fmConds.size(); i++)
			{
				fmConditions = emitShortOr(fmConditions, emitCondition(fmConds.get(i)));
			}
		}
		String onCall = filterCode.getInstruction().accept(this).toString();
		String onReturn = null;
		if (returnActions.size() > 0)
		{
			onReturn = emitReturnActions();
		}
		return emitFilterCode(fmConditions, onCall, onReturn);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.INLINE.model.Visitor#visitJump(Composestar.Core.INLINE.model.Jump)
	 */
	public Object visitJump(Jump jump)
	{
		if (jump.getTarget().isUsed() || jump.getTarget().getId() == -1)
		{
			return emitJump(jump.getTarget().getId());
		}
		return null;
	}

	protected String visitConditionExpression(ConditionExpression expr)
	{
		if (expr instanceof BinaryOperator)
		{
			BinaryOperator bop = (BinaryOperator) expr;
			return emitBinaryOperator(bop, visitConditionExpression(bop.getLeft()), visitConditionExpression(bop
					.getRight()));
		}
		else if (expr instanceof UnaryOperator)
		{
			return emitUnaryOperator((UnaryOperator) expr,
					visitConditionExpression(((UnaryOperator) expr).getOperand()));
		}
		else if (expr instanceof CondLiteral)
		{
			return emitCondLiteral((CondLiteral) expr);
		}
		else if (expr instanceof ConditionVariable)
		{
			return emitCondition(((ConditionVariable) expr).getCondition().getRef());
		}
		logger.error(String.format("Unhandled condition expression type: \"%s\"", expr.getClass().getName()));
		throw new RuntimeException("Unhandled condition expression type");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.INLINE.CodeGen.CodeGenerator#register(Composestar.Core.INLINE.CodeGen.FilterActionCodeGenerator)
	 */
	public void register(FilterActionCodeGenerator<String> facg)
	{
		for (String type : facg.supportedTypes())
		{
			if (faCodeGens.containsKey(type))
			{
				logger.warn(String.format("Already a filter action code generator registered for type \"%s\": %s",
						type, faCodeGens.get(type).getClass().getName()));
			}
			faCodeGens.put(type, facg);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.INLINE.CodeGen.CodeGenerator#emitCondition(Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Condition)
	 */
	public String emitCondition(Condition cond)
	{
		Reference ref = cond.getShortref();
		Type type = null;
		if (ref instanceof DeclaredObjectReference)
		{
			DeclaredObjectReference dor = (DeclaredObjectReference) ref;
			if (dor.getName().equals(Target.INNER) || dor.getName().equals(Target.SELF))
			{
				type = method.parent();
			}
			else
			{
				type = (Type) dor.getRef().getType().getRef().getPlatformRepresentation();
			}
		}
		else if (ref instanceof ConcernReference)
		{
			ConcernReference cor = (ConcernReference) ref;
			type = (Type) cor.getRef().getPlatformRepresentation();
		}
		else
		{
			logger.error(String.format("Unknown reference type: \"%s\"", ref.getClass().getName()));
			throw new RuntimeException("Unknown reference type");
		}
		String[] args = new String[0];
		MethodInfo mi = type.getMethod((String) cond.getDynObject("selector"), args);

		// TODO: if mi == null find method with a certain argument?
		if (mi == null)
		{
			args = new String[1];
			args[0] = "";
			mi = type.getMethod((String) cond.getDynObject("selector"), args);
		}

		// TODO: resolve context (interal/external)
		if (mi != null)
		{
			return emitMethodCall(mi, null, null);
		}

		return " /* unable to resolve method */ ";

		// logger.error(String.format("Method condition for \"%s\" not
		// found:%s.%s", cond.getName(), type.getFullName(),
		// cond.getDynObject("selector")));
		// throw new RuntimeException("Method condition not found");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.INLINE.CodeGen.CodeGenerator#emitFilterAction(Composestar.Core.INLINE.model.FilterAction)
	 */
	public String emitFilterAction(FilterAction filterAction)
	{
		FilterActionCodeGenerator<String> facg = faCodeGens.get(filterAction.getType());
		if (facg == null)
		{
			return emitDefaultFilterAction(filterAction);
		}
		else
		{
			String res = facg.generate(this, filterAction);
			if (res == null)
			{
				// dummy filter actions don't produce a result
				return null;
			}
			return res;
		}
	}
}
