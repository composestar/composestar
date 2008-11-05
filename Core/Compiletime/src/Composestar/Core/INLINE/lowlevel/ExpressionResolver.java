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

package Composestar.Core.INLINE.lowlevel;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import Composestar.Core.CpsRepository2.RepositoryEntity;
import Composestar.Core.CpsRepository2.FilterElements.BinaryMEOperator;
import Composestar.Core.CpsRepository2.FilterElements.FilterElement;
import Composestar.Core.CpsRepository2.FilterElements.MECompareStatement;
import Composestar.Core.CpsRepository2.FilterElements.MECondition;
import Composestar.Core.CpsRepository2.FilterElements.MELiteral;
import Composestar.Core.CpsRepository2.FilterElements.MatchingExpression;
import Composestar.Core.CpsRepository2.FilterElements.UnaryMEOperator;
import Composestar.Core.CpsRepository2Impl.FilterElements.AndMEOper;
import Composestar.Core.CpsRepository2Impl.FilterElements.MEConditionImpl;
import Composestar.Core.CpsRepository2Impl.FilterElements.MELiteralImpl;
import Composestar.Core.CpsRepository2Impl.FilterElements.NotMEOper;
import Composestar.Core.CpsRepository2Impl.FilterElements.OrMEOper;
import Composestar.Core.FIRE2.model.ExecutionState;
import Composestar.Core.FIRE2.model.ExecutionTransition;
import Composestar.Core.FIRE2.model.FlowNode;
import Composestar.Core.FIRE2.model.FlowTransition;

/**
 * Clones a matching expression and replaces single flowTrue/flowFalse
 * transitions with constants
 * 
 * @author Michiel Hendriks
 */
public final class ExpressionResolver
{
	/**
	 * Create a copy of a MatchingExpression by replacing non-branching
	 * expressions with constants.
	 * 
	 * @param start
	 * @param accept
	 * @param reject
	 * @return
	 */
	public static final MatchingExpression createExpression(ExecutionState start, ExecutionState accept,
			ExecutionState reject)
	{
		if (!start.getFlowNode().containsName(FlowNode.FILTER_ELEMENT_NODE))
		{
			return null;
		}
		RepositoryEntity re = start.getFlowNode().getRepositoryLink();
		if (!(re instanceof FilterElement))
		{
			return null;
		}
		FilterElement filterElement = (FilterElement) re;

		Set<ExecutionState> history = new HashSet<ExecutionState>();
		history.add(accept);
		history.add(reject);
		Map<MatchingExpression, MatchingExpression> replacements = new HashMap<MatchingExpression, MatchingExpression>();
		createReplacementMap(start, replacements, history);

		MatchingExpression baseExpr = filterElement.getMatchingExpression();
		return consolidateExpression(baseExpr, replacements);
	}

	/**
	 * Create the actual clone of the expression.
	 * 
	 * @param baseExpr
	 * @param replacements
	 * @return
	 */
	private static MatchingExpression consolidateExpression(MatchingExpression baseExpr,
			Map<MatchingExpression, MatchingExpression> replacements)
	{
		// replace it
		if (replacements.containsKey(baseExpr))
		{
			return replacements.get(baseExpr);
		}
		// clone the literal
		if (baseExpr instanceof MELiteral)
		{
			return new MELiteralImpl(((MELiteral) baseExpr).getLiteralValue());
		}
		// clone a condition
		if (baseExpr instanceof MECondition)
		{
			MEConditionImpl me = new MEConditionImpl();
			me.setCondition(((MECondition) baseExpr).getCondition());
			return me;
		}
		// clone the standard boolean operators
		if (baseExpr instanceof NotMEOper)
		{
			NotMEOper me = new NotMEOper();
			MatchingExpression oper = consolidateExpression(((NotMEOper) baseExpr).getOperand(), replacements);
			if (oper instanceof MELiteral)
			{
				// simplify the expression
				return new MELiteralImpl(!((MELiteral) oper).getLiteralValue());
			}
			me.setOperand(oper);
			return me;
		}
		if (baseExpr instanceof AndMEOper)
		{
			AndMEOper me = new AndMEOper();
			MatchingExpression lhs = consolidateExpression(((AndMEOper) baseExpr).getLHS(), replacements);
			MatchingExpression rhs = consolidateExpression(((AndMEOper) baseExpr).getRHS(), replacements);
			if (lhs instanceof MELiteral)
			{
				// simplify the expression
				if (!((MELiteral) lhs).getLiteralValue())
				{
					return new MELiteralImpl(false);
				}
				else
				{
					return rhs;
				}
			}
			if (rhs instanceof MELiteral)
			{
				// simplify the expression
				if (!((MELiteral) rhs).getLiteralValue())
				{
					return new MELiteralImpl(false);
				}
				else
				{
					return lhs;
				}
			}
			me.setLHS(lhs);
			me.setRHS(rhs);
			return me;
		}
		if (baseExpr instanceof OrMEOper)
		{
			OrMEOper me = new OrMEOper();
			MatchingExpression lhs = consolidateExpression(((OrMEOper) baseExpr).getLHS(), replacements);
			MatchingExpression rhs = consolidateExpression(((OrMEOper) baseExpr).getRHS(), replacements);
			if (lhs instanceof MELiteral)
			{
				// simplify the expression
				if (((MELiteral) lhs).getLiteralValue())
				{
					return new MELiteralImpl(true);
				}
				else
				{
					return rhs;
				}
			}
			if (rhs instanceof MELiteral)
			{
				// simplify the expression
				if (((MELiteral) rhs).getLiteralValue())
				{
					return new MELiteralImpl(true);
				}
				else
				{
					return lhs;
				}
			}
			me.setLHS(lhs);
			me.setRHS(rhs);
			return me;
		}
		// fallback
		try
		{
			if (baseExpr instanceof BinaryMEOperator)
			{
				BinaryMEOperator me = (BinaryMEOperator) baseExpr.getClass().newInstance();
				me.setLHS(consolidateExpression(((BinaryMEOperator) baseExpr).getLHS(), replacements));
				me.setRHS(consolidateExpression(((BinaryMEOperator) baseExpr).getRHS(), replacements));
				return me;
			}
			if (baseExpr instanceof UnaryMEOperator)
			{
				UnaryMEOperator me = (UnaryMEOperator) baseExpr.getClass().newInstance();
				me.setOperand(consolidateExpression(((UnaryMEOperator) baseExpr).getOperand(), replacements));
				return me;
			}
		}
		catch (InstantiationException e)
		{
			// TODO error
			return null;
		}
		catch (IllegalAccessException e)
		{
			// TODO error
			return null;
		}
		if (baseExpr instanceof MECompareStatement)
		{
			// TODO: error, these should have been resolved
		}
		// TODO: error
		return null;
	}

	/**
	 * Create a replacement map for all matching expressions. A matching
	 * expression with only 1 outgoing edge doesn't branch and can thus be
	 * replaced by a constant True or False
	 * 
	 * @param state
	 * @param replacements
	 * @param history
	 */
	public static final void createReplacementMap(ExecutionState state,
			Map<MatchingExpression, MatchingExpression> replacements, Set<ExecutionState> history)
	{
		while (true)
		{
			if (history.contains(state))
			{
				return;
			}
			history.add(state);
			List<ExecutionTransition> trans = state.getOutTransitionsEx();
			if (trans.isEmpty())
			{
				return;
			}
			if (trans.size() == 1)
			{
				ExecutionTransition t = trans.get(0);
				if (t.getFlowTransition().getType() == FlowTransition.FLOW_NEXT_TRANSITION)
				{
					state = t.getEndState();
					continue;
				}

				RepositoryEntity re = state.getFlowNode().getRepositoryLink();
				if (!(re instanceof MatchingExpression))
				{
					state = t.getEndState();
					continue;
				}
				MatchingExpression me = (MatchingExpression) re;
				if (t.getFlowTransition().getType() == FlowTransition.FLOW_FALSE_TRANSITION)
				{
					replacements.put(me, new MELiteralImpl(false));
				}
				else if (t.getFlowTransition().getType() == FlowTransition.FLOW_TRUE_TRANSITION)
				{
					replacements.put(me, new MELiteralImpl(true));
				}
			}

			for (int i = 0; i < trans.size() - 1; i++)
			{
				createReplacementMap(trans.get(i).getEndState(), replacements, history);
			}
			state = trans.get(trans.size() - 1).getEndState();
		}
	}
}
