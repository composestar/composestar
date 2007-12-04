/*
 * This file is part of the Compose* project.
 * http://composestar.sourceforge.net
 * Copyright (C) 2007 University of Twente.
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

package Composestar.Core.FIRE2.util.regex;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import Composestar.Core.FIRE2.model.ExecutionState;
import Composestar.Core.FIRE2.model.ExecutionTransition;

/**
 * Base class for the matcher of the FIRE2 regular expression toolset. Used for
 * the transition of the legacy matcher to the new matcher.
 * 
 * @author Michiel Hendrik
 */
public abstract class AbstractMatcher
{
	protected Set<CombinedState> endStates;

	protected boolean matchDone = false;

	protected AbstractMatcher()
	{
		super();
		endStates = new HashSet<CombinedState>();
	}

	/**
	 * Return true when the given execution model matches the given pattern.
	 * 
	 * @return
	 */
	public abstract boolean matches();

	/**
	 * Returns all traces in the execution model that matches the given pattern,
	 * or null if there were no matches.
	 * 
	 * @return
	 */
	public List<MatchTrace> matchTraces()
	{
		if (!matchDone)
		{
			matches();
		}

		if (endStates.isEmpty())
		{
			return null;
		}

		List<MatchTrace> result = new ArrayList<MatchTrace>();
		for (CombinedState endState : endStates)
		{
			MatchTrace mt = new MatchTrace(endState.trace.toList(), endState.trace.operationList());
			result.add(mt);
		}
		return result;
	}

	/**
	 * Encapsulates a single matching trace.
	 * 
	 * @author Michiel Hendriks
	 */
	public static class MatchTrace
	{
		private List<String> operations;

		private List<ExecutionTransition> transitions;

		public MatchTrace(List<ExecutionTransition> transitions, List<String> operations)
		{
			this.transitions = transitions;
			this.operations = operations;
		}

		public List<String> getOperations()
		{
			return Collections.unmodifiableList(operations);
		}

		public List<ExecutionTransition> getTransition()
		{
			return Collections.unmodifiableList(transitions);
		}
	}

	protected static class CombinedState
	{
		public ExecutionState executionState;

		public RegularState regularState;

		private TransitionTrace trace;

		protected List<String> returnOps;

		public CombinedState(ExecutionState executionState, RegularState regularState)
		{
			this.executionState = executionState;
			this.regularState = regularState;
			trace = new TransitionTrace();
		}

		public CombinedState(ExecutionState executionState, RegularState regularState, CombinedState previousState)
		{
			this.executionState = executionState;
			this.regularState = regularState;
			trace = previousState.trace;
			setReturnOps(previousState.returnOps);
		}

		public CombinedState(ExecutionState executionState, RegularState regularState, CombinedState previousState,
				ExecutionTransition transition, LabelSequence sequence)
		{
			this.executionState = executionState;
			this.regularState = regularState;
			trace = new TransitionTrace(previousState.trace, transition, sequence);
			setReturnOps(previousState.returnOps);
		}

		public void setReturnOps(List<String> ops)
		{
			if (ops == null)
			{
				returnOps = null;
				return;
			}
			if (returnOps != null)
			{
				returnOps = new ArrayList<String>(returnOps);
				returnOps.addAll(ops);
			}
			else
			{
				returnOps = ops;
			}
		}

		public List<String> getReturnOps()
		{
			if (returnOps == null)
			{
				return Collections.emptyList();
			}
			return Collections.unmodifiableList(returnOps);
		}

		@Override
		public boolean equals(Object obj)
		{
			if (!(obj instanceof CombinedState))
			{
				return false;
			}

			CombinedState state = (CombinedState) obj;

			if (returnOps == null)
			{
				if (state.returnOps != null)
				{
					return false;
				}
			}
			else if (!returnOps.equals(state.returnOps))
			{
				return false;
			}

			return executionState.equals(executionState) && regularState.equals(state.regularState);
		}

		@Override
		public int hashCode()
		{
			int res = 31;
			if (returnOps != null)
			{
				res += returnOps.hashCode();
			}
			return executionState.hashCode() + regularState.hashCode();
		}
	}

	/**
	 * LinkedList kind of class to efficiently construct the trace during
	 * matching.
	 * 
	 * @author Arjan de Roo
	 */
	protected static class TransitionTrace
	{
		public TransitionTrace heading;

		public ExecutionTransition last;

		public LabelSequence operations;

		public TransitionTrace()
		{}

		public TransitionTrace(TransitionTrace heading, ExecutionTransition last, LabelSequence operations)
		{
			this.heading = heading;
			this.last = last;
			this.operations = operations;
		}

		public List<String> operationList()
		{
			List<String> v;
			if (heading == null)
			{
				v = new ArrayList<String>();
			}
			else
			{
				v = heading.operationList();
			}
			if (operations != null)
			{
				v.addAll(operations.getLabelsEx());
			}
			return v;
		}

		public List<ExecutionTransition> toList()
		{
			if (heading == null)
			{
				return new LinkedList<ExecutionTransition>();
			}
			else
			{
				List<ExecutionTransition> v = heading.toList();
				v.add(last);
				return v;
			}
		}
	}

}
