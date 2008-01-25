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

import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.And;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.BinaryOperator;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.CondLiteral;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.False;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Not;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Or;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.True;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.UnaryOperator;
import Composestar.Core.INLINE.CodeGen.StringCodeGenerator;
import Composestar.Core.INLINE.model.FilterAction;
import Composestar.Core.LAMA.MethodInfo;
import Composestar.Core.LAMA.ParameterInfo;

/**
 * Produces ANSI-C code
 * 
 * @author Michiel Hendriks
 */
public class CCodeGenerator extends StringCodeGenerator
{
	protected static final String RETURN_FLOW_LABEL = "__cstar_return_flow";

	protected static final String LABEL_FORMAT = "__cstar_%s";

	protected static String indent(String input)
	{
		return "\t" + input.replaceAll("\n(.)", "\n\t$1");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.INLINE.CodeGen.CodeGenerator#emitBinaryOperator(Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.BinaryOperator,
	 *      java.lang.Object, java.lang.Object)
	 */
	public String emitBinaryOperator(BinaryOperator op, String lhs, String rhs)
	{
		if (op instanceof And)
		{
			return String.format("( %s && %s )", lhs, rhs);
		}
		else if (op instanceof Or)
		{
			return String.format("( %s || %s )", lhs, rhs);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.INLINE.CodeGen.CodeGenerator#emitBlock(java.lang.Object)
	 */
	public String emitBlock(String code)
	{
		return String.format("{\n%s}\n", indent(code));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.INLINE.CodeGen.CodeGenerator#emitBranch(java.lang.Object,
	 *      java.lang.Object, java.lang.Object)
	 */
	public String emitBranch(String condition, String trueBranch, String falseBranch)
	{
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
	 * 
	 * @see Composestar.Core.INLINE.CodeGen.CodeGenerator#emitCondLiteral(Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.CondLiteral)
	 */
	public String emitCondLiteral(CondLiteral literal)
	{
		if (literal instanceof True)
		{
			return "1";
		}
		else if (literal instanceof False)
		{
			return "0";
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.INLINE.CodeGen.CodeGenerator#emitFilterCode(java.lang.Object,
	 *      java.lang.Object, java.lang.Object)
	 */
	public String emitFilterCode(String fmConditions, String onCall, String onReturn)
	{
		StringBuffer sb = new StringBuffer();
		sb.append(String.format("\n{\nif ( !CSTAR_is_inner_call(%d) ) { /* inner call check */\n", methodId));
		boolean hasFmCond = fmConditions != null && fmConditions.trim().length() > 0;
		if (hasFmCond)
		{
			sb.append("if ( ");
			sb.append(fmConditions);
			sb.append(" ) {\n");
		}

		sb.append("\tJoinPointContext __JPC;\n");
		sb.append(String.format("\t%s returnValue;\n", method.getReturnTypeString()));

		// return action identifier
		if (returnActions.size() > 0)
		{
			sb.append(String.format("\tint __cstar_return_actions[%d], __cstar_return_actions_cnt = 0;\n",
					returnActions.size()));
		}

		sb.append("\t/* init JPC */;\n");
		sb.append(String.format("\t__JPC.startSelector = \"%s\";\n", method.getName()));
		sb.append("\t__JPC.returnValue = &returnValue;\n");
		List<ParameterInfo> pis = method.getParameters();
		sb.append(String.format("\t__JPC.argc = %d;\n", pis.size()));
		if (pis.size() > 0)
		{
			sb.append(String.format("\t__JPC.argv = (void **) malloc(%d * sizeof(void *));\n", pis.size()));
			for (int i = 0; i < pis.size(); i++)
			{
				ParameterInfo pi = pis.get(i);
				sb.append(String.format("\t__JPC.argv[%d] = &%s; /* %s */\n", i, pi.getName(), pi
						.getParameterTypeString()));
			}
		}
		else
		{
			// requires #include <stdio.h>
			sb.append("\t__JPC.argv = NULL;\n");
		}

		sb.append(indent(onCall));
		sb.append(emitLabel(-1));
		if (onReturn != null)
		{
			sb.append("\t{\n");
			sb.append(indent(indent(onReturn)));
			sb.append("\t}\n");
		}
		sb.append("\treturn returnValue;\n");
		if (hasFmCond)
		{
			sb.append("} /* end filter module condition check */\n");
		}
		sb.append("} /* end filter code */\n");
		sb.append(String.format("CSTAR_reset_inner_call(%d);\n}\n", methodId));
		return sb.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.INLINE.CodeGen.CodeGenerator#emitMethodCall(Composestar.Core.LAMA.MethodInfo,
	 *      java.util.List, java.lang.Object)
	 */
	public String emitMethodCall(MethodInfo method, List<String> args, Object context)
	{
		// TODO Auto-generated method stub
		return method.getName() + "()";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.INLINE.CodeGen.CodeGenerator#emitSetInnerCall(int)
	 */
	public String emitSetInnerCall(int methodId)
	{
		return String.format("CSTAR_set_inner_call(%d);\n", methodId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.INLINE.CodeGen.CodeGenerator#emitReturnActions()
	 */
	public String emitReturnActions()
	{
		StringBuffer sb = new StringBuffer();
		sb.append("int __cstar_return_actions_proc = 0;\n");
		sb.append("while ( __cstar_return_actions_proc < __cstar_return_actions_cnt ) {\n");
		sb.append("\tswitch( __cstar_return_actions[__cstar_return_actions_proc] ) {\n");
		for (int i = 0; i < returnActions.size(); i++)
		{
			sb.append("\tcase ");
			sb.append(i);
			sb.append(":\n");
			sb.append(indent(indent(emitFilterAction(returnActions.get(i)))));
			sb.append("\t\tbreak;\n");
		}
		sb.append("\t}\n");
		sb.append("\t__cstar_return_actions_proc++;\n");
		sb.append("}\n");
		return sb.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.INLINE.CodeGen.CodeGenerator#emitReturnFilterAction(int,
	 *      Composestar.Core.INLINE.model.FilterAction)
	 */
	public String emitReturnFilterAction(int idx, FilterAction filterAction)
	{
		return String.format("__cstar_return_actions[__cstar_return_actions_cnt++] = %d; /* queue: %s */\n", idx,
				filterAction.getType());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.INLINE.CodeGen.CodeGenerator#emitShortOr(java.lang.Object,
	 *      java.lang.Object)
	 */
	public String emitShortOr(String lhs, String rhs)
	{
		return String.format("%s || %s", lhs, rhs);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.INLINE.CodeGen.CodeGenerator#emitUnaryOperator(Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.UnaryOperator,
	 *      java.lang.Object)
	 */
	public String emitUnaryOperator(UnaryOperator op, String expr)
	{
		if (op instanceof Not)
		{
			return String.format("!%s", expr);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
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
	 * 
	 * @see Composestar.Core.INLINE.CodeGen.CodeGenerator#emitLabel(int)
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.INLINE.CodeGen.CodeGenerator#emitLabel(int)
	 */
	public String emitLabel(int labelId)
	{
		if (labelId == -1)
		{
			return String.format("\n%s:\n", RETURN_FLOW_LABEL);
		}
		else
		{
			return String.format("\n" + LABEL_FORMAT + ":\n", labelId);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.INLINE.CodeGen.CodeGenerator#emitDefaultFilterAction(Composestar.Core.INLINE.model.FilterAction)
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.INLINE.CodeGen.CodeGenerator#emitDefaultFilterAction(Composestar.Core.INLINE.model.FilterAction)
	 */
	public String emitDefaultFilterAction(FilterAction filterAction)
	{
		return "/* " + filterAction.getType() + " */ ;\n";
	}
}
