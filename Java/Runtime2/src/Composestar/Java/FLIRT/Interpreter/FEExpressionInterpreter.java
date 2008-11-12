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

package Composestar.Java.FLIRT.Interpreter;

import Composestar.Core.CpsRepository2.PropertyNames;
import Composestar.Core.CpsRepository2.PropertyPrefix;
import Composestar.Core.CpsRepository2.FilterElements.BinaryFilterElementOperator;
import Composestar.Core.CpsRepository2.FilterElements.CanonAssignment;
import Composestar.Core.CpsRepository2.FilterElements.CanonProperty;
import Composestar.Core.CpsRepository2.FilterElements.FilterElement;
import Composestar.Core.CpsRepository2.FilterElements.FilterElementExpression;
import Composestar.Core.CpsRepository2.TypeSystem.CpsLiteral;
import Composestar.Core.CpsRepository2.TypeSystem.CpsProgramElement;
import Composestar.Core.CpsRepository2.TypeSystem.CpsVariable;
import Composestar.Core.CpsRepository2Impl.FilterElements.CORFilterElmOper;
import Composestar.Core.CpsRepository2Impl.FilterElements.CanonAssignmentImpl;
import Composestar.Core.CpsRepository2Impl.TypeSystem.CpsSelectorImpl;
import Composestar.Core.CpsRepository2Impl.TypeSystem.CpsSelectorMethodInfo;
import Composestar.Core.LAMA.MethodInfo;
import Composestar.Core.LAMA.ProgramElement;
import Composestar.Java.FLIRT.Env.RTMessage;

/**
 * Filter element expression interpreter
 * 
 * @author Michiel Hendriks
 */
public class FEExpressionInterpreter
{
	/**
	 * Interpret this filter element
	 * 
	 * @param fex
	 * @param context
	 * @return True when the message is accepted by a filter element
	 */
	public static boolean interpret(FilterElementExpression fex, FilterExecutionContext context)
	{
		if (fex instanceof FilterElement)
		{
			return interpretFilterElement((FilterElement) fex, context);
		}
		else if (fex instanceof BinaryFilterElementOperator)
		{
			return interpretBinOper((BinaryFilterElementOperator) fex, context);
		}
		else
		{
			// TODO error
		}
		return false;
	}

	/**
	 * Interpreted a binary filter element operator
	 * 
	 * @param fex
	 * @param context
	 * @return
	 */
	public static boolean interpretBinOper(BinaryFilterElementOperator fex, FilterExecutionContext context)
	{
		if (fex instanceof CORFilterElmOper)
		{
			if (interpret(fex.getLHS(), context))
			{
				return true;
			}
			else return interpret(fex.getRHS(), context);
		}
		else
		{
			// TODO error
		}
		return false;
	}

	/**
	 * Interpret a filter element
	 * 
	 * @param fex
	 * @param context
	 * @return
	 */
	public static boolean interpretFilterElement(FilterElement fex, FilterExecutionContext context)
	{
		if (MatchingExpressionInterpreter.interpret(fex.getMatchingExpression(), context))
		{
			FilterArguments farg = context.getFilterArguments();
			if (farg == null)
			{
				farg = new FilterArguments();
				context.setFilterArguments(farg);
			}
			RTMessage msg = context.getMessage();
			for (CanonAssignment asg : fex.getAssignments())
			{
				if (asg.getProperty() == null)
				{
					continue;
				}

				CpsVariable value = getValue(asg.getValue(), msg, farg);

				if (value == null)
				{
					// TODO warn
					continue;
				}

				if (asg.getProperty().getPrefix() == PropertyPrefix.FILTER)
				{
					CanonAssignmentImpl arg = new CanonAssignmentImpl();
					arg.setProperty(asg.getProperty());
					arg.setValue(value);
					farg.add(arg);
				}
				else if (asg.getProperty().getPrefix() == PropertyPrefix.MESSAGE)
				{
					// convert selectors when possible
					if (PropertyNames.SELECTOR.equals(asg.getProperty().getBaseName()))
					{
						if (value instanceof CpsLiteral)
						{
							value = new CpsSelectorImpl(((CpsLiteral) value).getLiteralValue());
						}
						else if (value instanceof CpsProgramElement)
						{
							ProgramElement pe = ((CpsProgramElement) value).getProgramElement();
							if (pe instanceof MethodInfo)
							{
								value = new CpsSelectorMethodInfo((MethodInfo) pe);
							}
						}
					}
					try
					{
						msg.setProperty(asg.getProperty().getBaseName(), value);
					}
					catch (IllegalArgumentException e)
					{
						// TODO error
					}
				}
				else
				{
					// TODO warning, unknown property
				}
			}
			return true;
		}
		return false;
	}

	/**
	 * Return the real value of a message, resolving possible deferred values.
	 * 
	 * @param var
	 * @param msg
	 * @return
	 */
	public static CpsVariable getValue(CpsVariable var, RTMessage msg, FilterArguments farg)
	{
		CpsVariable result = var;
		while (result instanceof CanonProperty)
		{
			CanonProperty prop = (CanonProperty) result;
			if (prop.getPrefix() == PropertyPrefix.FILTER)
			{
				result = farg.get(prop.getBaseName());
			}
			else if (prop.getPrefix() == PropertyPrefix.MESSAGE)
			{
				result = msg.getProperty(prop.getBaseName());
			}
			else if (PropertyNames.INNER.equals(prop.getName()))
			{
				result = msg.getInner();
			}
			else
			{
				result = null;
			}
		}
		return result;
	}
}
