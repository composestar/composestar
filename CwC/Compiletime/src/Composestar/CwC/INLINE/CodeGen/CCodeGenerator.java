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

import java.util.List;

import weavec.cmodel.type.VoidType;
import Composestar.Core.CpsRepository2.JoinPointContextArgument;
import Composestar.Core.CpsRepository2.FilterElements.BinaryMEOperator;
import Composestar.Core.CpsRepository2.FilterElements.MELiteral;
import Composestar.Core.CpsRepository2.FilterElements.UnaryMEOperator;
import Composestar.Core.CpsRepository2.TypeSystem.CpsObject;
import Composestar.Core.CpsRepository2Impl.FilterElements.AndMEOper;
import Composestar.Core.CpsRepository2Impl.FilterElements.NotMEOper;
import Composestar.Core.CpsRepository2Impl.FilterElements.OrMEOper;
import Composestar.Core.FIRE2.model.FireModel.FilterDirection;
import Composestar.Core.INLINE.CodeGen.FilterActionCodeGenerator;
import Composestar.Core.INLINE.CodeGen.StringCodeGenerator;
import Composestar.Core.INLINE.model.FilterActionInstruction;
import Composestar.Core.LAMA.MethodInfo;
import Composestar.Core.LAMA.ParameterInfo;
import Composestar.Core.LAMA.Type;
import Composestar.CwC.LAMA.CwCFunctionInfo;
import Composestar.CwC.LAMA.CwCType;

/**
 * Produces ANSI-C code
 * 
 * @author Michiel Hendriks
 */
public class CCodeGenerator extends StringCodeGenerator
{
	protected static final String RETURN_FLOW_LABEL = "__cstar_return_flow";

	protected static final String LABEL_FORMAT = "__cstar_%s";

	protected AbstractHeaderFileGenerator headerGenerator;

	public void setHeaderGenerator(AbstractHeaderFileGenerator hgen)
	{
		headerGenerator = hgen;
	}

	protected static String indent(String input)
	{
		if (input == null)
		{
			return "";
		}
		return "\t" + input.replaceAll("\n(.)", "\n\t$1");
	}

	protected static String indent(String input, int depth)
	{
		if (input == null)
		{
			return "";
		}
		StringBuffer it = new StringBuffer();
		while (depth-- > 0)
		{
			it.append("\t");
		}
		return it.toString() + input.replaceAll("\n(.)", "\n" + it.toString() + "$1");
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.INLINE.CodeGen.CodeGenerator#emitBinaryOperator(Composestar
	 * .Core.CpsProgramRepository.CpsConcern.Filtermodules.BinaryOperator,
	 * java.lang.Object, java.lang.Object)
	 */
	public String emitBinaryOperator(BinaryMEOperator op, String lhs, String rhs)
	{
		if (op instanceof AndMEOper)
		{
			return String.format("( %s && %s )", lhs, rhs);
		}
		else if (op instanceof OrMEOper)
		{
			return String.format("( %s || %s )", lhs, rhs);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.INLINE.CodeGen.CodeGenerator#emitBlock(java.lang.Object)
	 */
	public String emitBlock(String code)
	{
		return String.format("{\n%s}\n", indent(code));
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.INLINE.CodeGen.CodeGenerator#emitBranch(java.lang.Object
	 * , java.lang.Object, java.lang.Object)
	 */
	public String emitBranch(String condition, String trueBranch, String falseBranch)
	{
		if ((trueBranch == null || trueBranch.trim().length() == 0)
				&& (falseBranch == null || falseBranch.trim().length() == 0))
		{
			return String.format("if ( %s ) /* nop */;", condition);
		}
		if (trueBranch == null || trueBranch.trim().length() == 0)
		{
			return String.format("if ( !( %s ) ) %s", condition, falseBranch);
		}
		if (falseBranch == null || falseBranch.trim().length() == 0)
		{
			return String.format("if ( %s ) %s", condition, trueBranch);
		}
		return String.format("if ( %s ) %s \nelse %s", condition, trueBranch, falseBranch);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.INLINE.CodeGen.CodeGenerator#emitCondLiteral(Composestar
	 * .Core.CpsProgramRepository.CpsConcern.Filtermodules.CondLiteral)
	 */
	public String emitCondLiteral(MELiteral literal)
	{
		if (literal.getLiteralValue())
		{
			return "1";
		}
		else
		{
			return "0";
		}
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.INLINE.CodeGen.CodeGenerator#emitFilterCode(java.lang
	 * .Object, java.lang.Object, java.lang.Object)
	 */
	public String emitFilterCode(String fmConditions, String onCall, String onReturn)
	{
		StringBuffer sb = new StringBuffer();
		sb.append("\n{ /* start filter code */\n");
		if (filterDirection == FilterDirection.INPUT)
		{
			sb.append(String.format("if ( !CSTAR_is_inner_call(%d) ) { \n", methodId));
		}
		boolean hasFmCond = fmConditions != null && fmConditions.trim().length() > 0;
		if (hasFmCond)
		{
			sb.append("if ( ");
			sb.append(fmConditions);
			sb.append(" ) {\n");
		}

		if (createJPC != JoinPointContextArgument.UNUSED && createJPC != JoinPointContextArgument.NONE)
		{
			sb.append(String.format("\t%s %s;\n", getJPCType(false), getJPCVariable(false)));
		}
		if (hasReturnValue(method))
		{
			// FIXME: need unique name here (specially for output filters)
			// also it needs to be outside this scope
			sb.append(String.format("\t%s returnValue;\n", method.getReturnTypeString()));
		}

		// return action identifier
		if (returnActions.size() > 0)
		{
			sb.append(String.format("\tint __cstar_return_actions[%d], __cstar_return_actions_cnt = 0;\n",
					returnActions.size()));
		}

		for (FilterActionInstruction action : allActions)
		{
			FilterActionCodeGenerator<String> facg = faCodeGens.get(action.getType());
			if (facg != null)
			{
				String initString = facg.generateMethodInit(this, action);
				if (initString != null)
				{
					sb.append(indent(initString));
				}
			}
		}

		if (createJPC != JoinPointContextArgument.UNUSED && createJPC != JoinPointContextArgument.NONE)
		{
			String jpcVarName = getJPCVariable(false);
			sb.append("\t/* init JPC */\n");
			sb.append(String.format("\t%s.startTarget = \"%s\";\n", jpcVarName, method.parent().getFullName()));
			sb.append(String.format("\t%s.startSelector = \"%s\";\n", jpcVarName, method.getName()));
			if (hasReturnValue(method))
			{
				sb.append(String.format("\t%s.returnValue = &returnValue;\n", jpcVarName));
				sb.append(String.format("\t%s.hasReturn = 1;\n", jpcVarName));
			}
			else
			{
				sb.append(String.format("\t%s.hasReturn = 0;\n", jpcVarName));
			}
			List<ParameterInfo> pis = method.getParameters();
			sb.append(String.format("\t%s.argc = %d;\n", jpcVarName, pis.size()));
			if (pis.size() > 0)
			{
				imports.add("<stdlib.h>");
				sb
						.append(String.format("\t%s.argv = (void **) malloc(%d * sizeof(void *));\n", jpcVarName, pis
								.size()));
				for (int i = 0; i < pis.size(); i++)
				{
					ParameterInfo pi = pis.get(i);
					sb.append(String.format("\t%s.argv[%d] = &%s; /* %s */\n", jpcVarName, i, pi.getName(), pi
							.getParameterTypeString()));
				}
			}
			else
			{
				// requires #include <stdio.h>
				imports.add("<stddef.h>");
				sb.append(String.format("\t%s.argv = NULL /*((void *)0)*/;\n", getJPCVariable(false)));
			}
		}

		sb.append(indent(onCall));
		sb.append(emitLabel(-1));
		if (onReturn != null)
		{
			sb.append("\t{\n");
			sb.append(indent(onReturn, 2));
			sb.append("\t}\n");
		}
		// if (filterDirection == FilterDirection.INPUT)
		// {
		if (hasReturnValue(method))
		{
			sb.append("\treturn returnValue;\n");
		}
		else
		{
			sb.append("\treturn;\n");
		}
		// }
		if (hasFmCond)
		{
			sb.append("} /* end filter module condition check */\n");
		}
		sb.append("} /* end filter code */\n");
		if (filterDirection == FilterDirection.INPUT)
		{
			sb.append(String.format("CSTAR_reset_inner_call(%d);\n}\n", methodId));
		}
		return sb.toString();
	}

	public boolean hasReturnValue(MethodInfo method)
	{
		CwCFunctionInfo cwcfunc = (CwCFunctionInfo) method;
		CwCType cwctype = (CwCType) cwcfunc.getReturnType();
		return !(cwctype.getCType() instanceof VoidType);
	}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Core.INLINE.CodeGen.CodeGenerator#emitSetInnerCall(int)
	 */
	public String emitSetInnerCall(int methodId)
	{
		return String.format("CSTAR_set_inner_call(%d);\n", methodId);
	}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Core.INLINE.CodeGen.CodeGenerator#emitReturnActions()
	 */
	public String emitReturnActions()
	{
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < returnActions.size(); i++)
		{
			String action = emitFilterAction(returnActions.get(i));
			if (action == null || action.trim().length() == 0)
			{
				continue;
			}
			sb.append("\tcase ");
			sb.append(i);
			sb.append(": {\n");
			sb.append(indent(action, 3));
			sb.append("\t\t}\n\t\tbreak;\n");
		}
		if (sb.length() == 0)
		{
			// no code for return actions
			return "";
		}

		StringBuffer sb2 = new StringBuffer();
		sb2.append("int __cstar_return_actions_proc = 0;\n");
		sb2.append("while ( __cstar_return_actions_proc < __cstar_return_actions_cnt ) {\n");
		sb2.append("\tswitch( __cstar_return_actions[__cstar_return_actions_proc++] ) {\n");
		sb2.append(sb.toString());
		sb2.append("\t}\n");
		sb2.append("}\n");
		return sb2.toString();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.INLINE.CodeGen.CodeGenerator#emitReturnFilterAction(int,
	 * Composestar.Core.INLINE.model.FilterAction)
	 */
	public String emitReturnFilterAction(int idx, FilterActionInstruction filterAction)
	{
		return String.format("__cstar_return_actions[__cstar_return_actions_cnt++] = %d; /* queue: %s */\n", idx,
				filterAction.getType());
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.INLINE.CodeGen.CodeGenerator#emitShortOr(java.lang.Object
	 * , java.lang.Object)
	 */
	public String emitShortOr(String lhs, String rhs)
	{
		return String.format("%s || %s", lhs, rhs);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.INLINE.CodeGen.CodeGenerator#emitUnaryOperator(Composestar
	 * .Core.CpsProgramRepository.CpsConcern.Filtermodules.UnaryOperator,
	 * java.lang.Object)
	 */
	public String emitUnaryOperator(UnaryMEOperator op, String expr)
	{
		if (op instanceof NotMEOper)
		{
			return String.format("!%s", expr);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Core.INLINE.CodeGen.CodeGenerator#emitJump(int)
	 */
	public String emitJump(int labelId)
	{
		if (labelId == -1)
		{
			return String.format("goto %s;\n", RETURN_FLOW_LABEL);
		}
		else
		{
			return String.format("goto %s;\n", String.format(LABEL_FORMAT, labelId));
		}
	}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Core.INLINE.CodeGen.CodeGenerator#emitLabel(int)
	 */
	/*
	 * (non-Javadoc)
	 * @see Composestar.Core.INLINE.CodeGen.CodeGenerator#emitLabel(int)
	 */
	public String emitLabel(int labelId)
	{
		if (labelId == -1)
		{
			// FIXME: needs to be a unique label
			return String.format("\n%s:\n", RETURN_FLOW_LABEL);
		}
		else
		{
			return String.format("\n" + LABEL_FORMAT + ":\n", labelId);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.INLINE.CodeGen.CodeGenerator#emitDefaultFilterAction
	 * (Composestar.Core.INLINE.model.FilterAction)
	 */
	public String emitDefaultFilterAction(FilterActionInstruction filterAction)
	{
		return "/* " + filterAction.getType() + " */ ;\n";
	}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Core.INLINE.CodeGen.CodeGenerator#getJPCType()
	 */
	public String getJPCType(boolean asReference)
	{
		if (asReference)
		{
			return "JoinPointContext*";
		}
		return "JoinPointContext";
	}

	/*
	 * (non-Javadoc)
	 * @see Composestar.Core.INLINE.CodeGen.CodeGenerator#getJPCVariable()
	 */
	public String getJPCVariable(boolean asReference)
	{
		if (asReference)
		{
			return "&__JPC";
		}
		return "__JPC";
	}

	/*
	 * (non-Javadoc)
	 * @seeComposestar.Core.INLINE.CodeGen.CodeGenerator#emitJpcInitialization(
	 * Composestar.Core.INLINE.model.FilterAction)
	 */
	public String emitJpcInitialization(FilterActionInstruction filterAction, JoinPointContextArgument jpc)
	{
		if (jpc == JoinPointContextArgument.NONE || jpc == JoinPointContextArgument.UNUSED)
		{
			return "";
		}
		StringBuffer sb = new StringBuffer();
		String jpcVarName = getJPCVariable(false);

		CpsObject target = filterAction.getMessage().getTarget();
		Type targetType = target.getTypeReference().getReference();

		if (targetType != null)
		{
			sb.append(String.format("%s.currentTarget = \"%s\";\n", jpcVarName, targetType.getFullName()));
		}
		sb.append(String.format("%s.currentSelector = \"%s\";\n", jpcVarName, filterAction.getMessage().getSelector()
				.getName()));

		// "substitution" message doesn't exist
		// target = filterAction.getSubstitutedMessage().getTarget();
		// targetType = null;
		// if (Target.INNER.equals(target.getName()) ||
		// Target.SELF.equals(target.getName()))
		// {
		// targetType = method.parent();
		// }
		// else
		// {
		// Concern crn = target.getRefToConcern();
		// if (crn != null)
		// {
		// targetType = (Type) crn.getPlatformRepresentation();
		// }
		// }
		// if (targetType != null)
		// {
		// sb.append(String.format("%s.substTarget = \"%s\";\n", jpcVarName,
		// targetType.getFullName()));
		// }
		// sb.append(String.format("%s.substSelector = \"%s\";\n", jpcVarName,
		// filterAction.getSubstitutedMessage()
		// .getSelector()));
		return sb.toString();
	}

	/*
	 * (non-Javadoc)
	 * @seeComposestar.Core.INLINE.CodeGen.StringCodeGenerator#emitMethodCall(
	 * Composestar.Core.LAMA.MethodInfo, java.util.List, java.lang.Object)
	 */
	@Override
	public String emitMethodCall(MethodInfo method, List<String> args, Object context)
	{
		if (headerGenerator != null)
		{
			if (method != getCurrentMethod())
			{
				// not needed for self
				headerGenerator.addMethod(method);
			}
		}
		// because context is not relevant in C
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < args.size(); i++)
		{
			if (sb.length() > 0)
			{
				sb.append(", ");
			}
			sb.append(args.get(i));
		}
		return method.getName() + "(" + sb.toString() + ")";
	}

	public String getBaseType()
	{
		return null;
	}
}
