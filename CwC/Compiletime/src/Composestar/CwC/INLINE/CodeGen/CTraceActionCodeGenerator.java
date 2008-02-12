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
import java.util.Set;

import weavec.cmodel.type.CType;
import weavec.cmodel.type.FloatingType;
import weavec.cmodel.type.IntegerType;
import weavec.cmodel.type.PointerType;
import weavec.cmodel.type.TypedefType;
import Composestar.Core.INLINE.CodeGen.CodeGenerator;
import Composestar.Core.INLINE.CodeGen.FilterActionCodeGenerator;
import Composestar.Core.INLINE.lowlevel.InlinerResources;
import Composestar.Core.INLINE.model.FilterAction;
import Composestar.Core.LAMA.MethodInfo;
import Composestar.Core.LAMA.ParameterInfo;
import Composestar.CwC.Filters.ExtraFilters;
import Composestar.CwC.LAMA.CwCParameterInfo;
import Composestar.CwC.LAMA.CwCType;

/**
 * Code generator for the TraceIn and TraceOut filter actions
 * 
 * @author Michiel Hendriks
 */
public class CTraceActionCodeGenerator implements FilterActionCodeGenerator<String>
{

	public CTraceActionCodeGenerator()
	{}

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
		String[] result = { ExtraFilters.TRACE_IN_ACTION, ExtraFilters.TRACE_OUT_ACTION };
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.INLINE.CodeGen.FilterActionCodeGenerator#generate(Composestar.Core.INLINE.CodeGen.CodeGenerator,
	 *      Composestar.Core.INLINE.model.FilterAction)
	 */
	public String generate(CodeGenerator<String> codeGen, FilterAction action)
	{
		StringBuffer sb = new StringBuffer();
		sb.append("printf(\"");
		if (action.getType().equals(ExtraFilters.TRACE_IN_ACTION))
		{
			sb.append(">>> Trace IN:   ");
		}
		else if (action.getType().equals(ExtraFilters.TRACE_OUT_ACTION))
		{
			sb.append("<<< Trace OUT:  ");
		}
		else
		{
			sb.append("??? ");
		}
		sb.append(codeGen.getCurrentMethod().getName());

		List<String> formatStrings = new ArrayList<String>();
		List<String> arguments = new ArrayList<String>();
		createArgumentData(codeGen.getCurrentMethod(), formatStrings, arguments);

		sb.append(" ( ");
		for (int i = 0; i < formatStrings.size(); i++)
		{
			if (i > 0)
			{
				sb.append(", ");
			}
			sb.append(formatStrings.get(i));
		}
		sb.append(" )");

		if (action.getType().equals(ExtraFilters.TRACE_OUT_ACTION)
				&& codeGen.hasReturnValue(codeGen.getCurrentMethod()))
		{
			sb.append(" return = ");
			FormatSpec fspec = getFormatSpec(((CwCType) codeGen.getCurrentMethod().getReturnType()).getCType());
			if (fspec != null)
			{
				if (fspec.pointerCnt <= 1)
				{
					sb.append(fspec.format);
					if (fspec.pointerCnt == 0)
					{
						arguments.add("returnValue");
					}
					else
					{
						arguments.add("*returnValue");
					}
				}
				else
				{
					// print pointer address
					sb.append("%p");
					arguments.add("returnValue");
				}
			}
			else
			{
				sb.append("<complex type>");
			}
		}

		sb.append("\\n\"");
		for (int i = 0; i < arguments.size(); i++)
		{
			sb.append(", ");
			sb.append(arguments.get(i));
		}
		sb.append(");\n");
		return sb.toString();
	}

	static class FormatSpec
	{
		int pointerCnt;

		String format;

		public FormatSpec(String fmt, int cnt)
		{
			format = fmt;
			pointerCnt = cnt;
		}
	}

	protected void createArgumentData(MethodInfo func, List<String> fmt, List<String> args)
	{
		for (ParameterInfo pi : (List<ParameterInfo>) func.getParameters())
		{
			CwCParameterInfo cwcpi = (CwCParameterInfo) pi;
			CType ctype = cwcpi.getObjectDeclaration().getType();
			FormatSpec fspec = getFormatSpec(ctype);
			if (fspec != null)
			{
				if (fspec.pointerCnt <= 1)
				{
					fmt.add(fspec.format);
					if (fspec.pointerCnt == 0)
					{
						args.add(pi.getName());
					}
					else
					{
						args.add("*" + pi.getName());
					}
				}
				else
				{
					// print pointer address
					fmt.add("%p");
					args.add(pi.getName());
				}
			}
			else
			{
				fmt.add("<complex type>");
			}
		}
	}

	protected FormatSpec getFormatSpec(CType ctype)
	{
		int pointerCnt = 0;
		while (ctype != null)
		{
			if (ctype instanceof PointerType)
			{
				pointerCnt++;
				ctype = ((PointerType) ctype).getBaseType();
			}
			else if (ctype instanceof TypedefType)
			{
				ctype = ((TypedefType) ctype).getBaseType();
			}
			else if (ctype instanceof IntegerType)
			{
				String base;
				switch (((IntegerType) ctype).getSign())
				{
					case UNSIGNED:
						base = "u";
					default:
						base = "d";
				}
				switch (((IntegerType) ctype).getKind())
				{
					case CHAR:
						if (pointerCnt > 0)
						{
							// char pointer = string
							return new FormatSpec("\\\"%s\\\"", pointerCnt - 1);
						}
						return new FormatSpec("\\'%c\\'", pointerCnt);
					case SHORT:
						return new FormatSpec("%h" + base, pointerCnt);
					case LONGLONG:
					case LONG:
						return new FormatSpec("%l" + base, pointerCnt);
					default:
						return new FormatSpec("%" + base, pointerCnt);
				}
			}
			else if (ctype instanceof FloatingType)
			{
				switch (((FloatingType) ctype).getKind())
				{
					case LONGDOUBLE:
						return new FormatSpec("%Lf", pointerCnt);
					case DOUBLE:
					default:
						return new FormatSpec("%f", pointerCnt);
				}
			}
			else
			{
				// can't say any more about this
				break;
			}
		}
		if (pointerCnt > 0)
		{
			// print pointer address
			return new FormatSpec("%p", 0);
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
		return null;
	}

	public Set<String> getDependencies(CodeGenerator<String> codeGen, String action)
	{
		return null;
	}

	public Set<String> getImports(CodeGenerator<String> codeGen, String action)
	{
		return null;
	}
}
