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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import Composestar.Core.CpsRepository2.RepositoryEntity;
import Composestar.Core.CpsRepository2.FilterElements.MatchingExpression;
import Composestar.Core.CpsRepository2Impl.FilterElements.NotMEOper;
import Composestar.Core.CpsRepository2Impl.FilterElements.OrMEOper;
import Composestar.Core.FIRE2.model.ExecutionState;
import Composestar.Core.FIRE2.model.ExecutionTransition;
import Composestar.Core.FIRE2.model.FlowTransition;

/**
 * Create a compact matching expression based on the branches in the execution
 * graph.
 * 
 * @author Michiel Hendriks
 */
public final class ExpressionCompactor
{
	/**
	 * Create the compact matching expression
	 * 
	 * @param start
	 * @param accept
	 * @param reject
	 * @return
	 */
	public static MatchingExpression compact(ExecutionState start, ExecutionState accept, ExecutionState reject)
	{
		Map<ExecutionState, State> history = new HashMap<ExecutionState, State>();
		State acceptExpr = new State(accept);
		history.put(accept, acceptExpr);
		State rejectExpr = new State(reject);
		history.put(reject, rejectExpr);

		createGraph(start, history);

		return createExpression(acceptExpr, new HashSet<State>());
	}

	/**
	 * Transform the graph to a matching expression. This is done by flattening
	 * the graph starting from the accept state.
	 * 
	 * @param state
	 * @param history
	 * @return
	 */
	private static MatchingExpression createExpression(State state, Set<State> history)
	{
		history.add(state);
		if (state.incoming.size() == 0)
		{
			RepositoryEntity re = state.state.getFlowNode().getRepositoryLink();
			if (re instanceof MatchingExpression)
			{
				return (MatchingExpression) re;
			}
			// shouldn't happen
			return null;
		}
		else if (state.incoming.size() == 1)
		{
			createExpression(state.incoming.get(0).source, history);
			state = state.incoming.get(0).source;
			RepositoryEntity re = state.state.getFlowNode().getRepositoryLink();
			if (re instanceof MatchingExpression)
			{
				return (MatchingExpression) re;
			}
			// shouldn't happen
			return null;
		}

		OrMEOper or = new OrMEOper();
		MatchingExpression result = or;
		int idx = 0;
		for (Transition t : state.incoming)
		{
			if (history.contains(t.source))
			{
				continue;
			}
			MatchingExpression elm = createExpression(t.source, history);
			if (!t.isTrue)
			{
				NotMEOper not = new NotMEOper();
				not.setOperand(elm);
				elm = not;
			}
			if (idx % 2 == 0)
			{
				or.setLHS(elm);
			}
			else
			{
				OrMEOper newOr = new OrMEOper();
				or.setRHS(newOr);
				or = newOr;
				or.setLHS(elm);
			}
			idx++;
		}
		if (or.getRHS() == null)
		{
			OrMEOper parent = (OrMEOper) or.getOwner();
			parent.setRHS(or.getLHS());
			or = parent;
		}
		return result;
	}

	/**
	 * Create the compact graph
	 * 
	 * @param state
	 * @param history
	 * @return
	 */
	private static State createGraph(ExecutionState state, Map<ExecutionState, State> history)
	{
		State first = null;
		while (true)
		{
			List<ExecutionTransition> trans = state.getOutTransitionsEx();
			if (trans.size() == 0)
			{
				// can't happen
				return null;
			}
			else if (trans.size() == 1)
			{
				state = trans.get(0).getEndState();
				if (history.containsKey(state))
				{
					return history.get(state);
				}
				continue;
			}

			Branch cb = new Branch(state);
			history.put(state, cb);
			if (first == null)
			{
				first = cb;
			}
			for (ExecutionTransition tran : trans)
			{
				State ce = history.get(tran.getEndState());
				if (ce == null)
				{
					ce = createGraph(tran.getEndState(), history);
				}
				if (tran.getFlowTransition().getType() == FlowTransition.FLOW_TRUE_TRANSITION)
				{
					cb.trueBranch = ce;
					ce.incoming.add(new Transition(cb, ce, true));
				}
				else
				{
					cb.falseBranch = ce;
					ce.incoming.add(new Transition(cb, ce, false));
				}
			}
			break;
		}
		return first;
	}

	/**
	 * A branch in the graph
	 * 
	 * @author Michiel Hendriks
	 */
	private static class Branch extends State
	{
		public State trueBranch;

		public State falseBranch;

		public Branch(ExecutionState es)
		{
			super(es);
		}
	}

	/**
	 * A state in the compact graph, only the accept and reject actions will be
	 * an instance of this thing
	 * 
	 * @author Michiel Hendriks
	 */
	private static class State
	{
		public List<Transition> incoming = new ArrayList<Transition>();

		public ExecutionState state;

		public State(ExecutionState es)
		{
			state = es;
		}
	}

	/**
	 * A transition in the graph
	 * 
	 * @author Michiel Hendriks
	 */
	private static class Transition
	{
		public State source, dest;

		public boolean isTrue;

		public Transition(State src, State dst, boolean ist)
		{
			source = src;
			dest = dst;
			isTrue = ist;
		}
	}
}
