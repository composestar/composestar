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
	/**
	 * The final states found during the matching process
	 */
	protected Set<CombinedState> endStates;

	/**
	 * If true the matching was finished
	 */
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
		/**
		 * The resource operations that were encountered
		 */
		private List<String> operations;

		/**
		 * The followed transitions
		 */
		private List<ExecutionTransition> transitions;

		public MatchTrace(List<ExecutionTransition> transitions, List<String> operations)
		{
			this.transitions = transitions;
			this.operations = operations;
		}

		/**
		 * @return the list of operations
		 */
		public List<String> getOperations()
		{
			return Collections.unmodifiableList(operations);
		}

		/**
		 * @return the visited transitions
		 */
		public List<ExecutionTransition> getTransition()
		{
			return Collections.unmodifiableList(transitions);
		}
	}

	/**
	 * A combination of execution and regular state used during the NFA matching
	 */
	protected static class CombinedState
	{
		/**
		 * The execution state
		 */
		public ExecutionState executionState;

		/**
		 * The regular state
		 */
		public RegularState regularState;

		/**
		 * The transition trace followed so far
		 */
		private TransitionTrace trace;

		/**
		 * Resource operations queued to be processed during the return flow
		 */
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

		/**
		 * Set the return flow operations
		 * 
		 * @param ops
		 */
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

		/**
		 * @return the operations performed on the message's return flow
		 */
		public List<String> getReturnOps()
		{
			if (returnOps == null)
			{
				return Collections.emptyList();
			}
			return Collections.unmodifiableList(returnOps);
		}

		/*
		 * (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
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

			return executionState.equals(state.executionState) && regularState.equals(state.regularState);
		}

		/*
		 * (non-Javadoc)
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode()
		{
			int res = 31;
			if (returnOps != null)
			{
				res += returnOps.hashCode();
			}
			return res + executionState.hashCode() + regularState.hashCode();
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
		/**
		 * The previous transition
		 */
		public TransitionTrace heading;

		/**
		 * The last execution transition that is part of this trace element
		 */
		public ExecutionTransition last;

		/**
		 * A list of operations part of this trace
		 */
		public LabelSequence operations;

		public TransitionTrace()
		{}

		public TransitionTrace(TransitionTrace heading, ExecutionTransition last, LabelSequence operations)
		{
			this.heading = heading;
			this.last = last;
			this.operations = operations;
		}

		/**
		 * @return the list of operations
		 */
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

		/**
		 * convert the linked list to a normal list
		 * 
		 * @return
		 */
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
