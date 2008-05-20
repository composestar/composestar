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

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;

import Composestar.Core.FIRE2.model.ExecutionModel;
import Composestar.Core.FIRE2.model.ExecutionState;
import Composestar.Core.FIRE2.model.ExecutionTransition;
import Composestar.Core.FIRE2.model.FlowNode;
import Composestar.Core.Master.ModuleNames;
import Composestar.Utils.Logging.CPSLogger;

/**
 * Improved matcher that also takes return action into proper account.
 * 
 * @author Michiel Hendriks
 */
public class MatcherEx extends AbstractMatcher
{
	protected static final CPSLogger logger = CPSLogger.getCPSLogger(ModuleNames.FIRE + ".regex");

	/**
	 * The pattern used for matching
	 */
	private Pattern pattern;

	/**
	 * The execution model that is used as source
	 */
	private ExecutionModel model;

	/**
	 * The labeler instance that provides the labels for the execution nodes and
	 * transitions
	 */
	private Labeler labeler;

	/**
	 * Visited states
	 */
	private Set<CombinedState> done;

	// both Stack or Queue can be used, but a Stack often results in a earlier
	// match
	/**
	 * States that have to be visited
	 */
	private Stack<CombinedState> todo;

	public MatcherEx(Pattern inPattern, ExecutionModel inModel, Labeler inLabeler)
	{
		pattern = inPattern;
		model = inModel;
		labeler = inLabeler;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.FIRE2.util.regex.AbstractMatcher#matches()
	 */
	@Override
	public boolean matches()
	{
		Iterator<ExecutionState> it = model.getEntranceStates();
		while (it.hasNext())
		{
			processExecModel(it.next());
		}
		matchDone = true;
		return endStates.size() > 0;
	}

	/**
	 * Process the model from a given execution state
	 * 
	 * @param startState
	 */
	private void processExecModel(ExecutionState startState)
	{
		logger.info("Processing exec module for " + startState.getMessage().toString() + " with pattern: "
				+ pattern.toString());
		done = new HashSet<CombinedState>();
		todo = new Stack<CombinedState>();

		CombinedState state = new CombinedState(startState, pattern.getStartState());
		if (!addCheckedState(state))
		{
			while (!todo.isEmpty())
			{
				if (processState(todo.pop()))
				{
					break;
				}
			}
			logger.debug(String.format("Finished processing with %d states left to process, total %d", todo.size(),
					done.size()));
		}
	}

	/**
	 * Add a state to the todo list but first check for a possible end state
	 * (returns true when an end state has been detected).
	 * 
	 * @param state
	 * @return
	 */
	private boolean addCheckedState(CombinedState state)
	{
		if (done.contains(state))
		{
			return false;
		}
		if (isEndState(state))
		{
			return true;
		}
		todo.push(state);
		// not really done yet, but prevents duplicates from being added to
		// the todo
		done.add(state);

		// add lambda transitions
		for (RegularTransition rt : state.regularState.getOutTransitions())
		{
			if (rt.isEmpty())
			{
				CombinedState newstate = new CombinedState(state.executionState, rt.getEndState(), state);
				if (addCheckedState(newstate))
				{
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * @param state
	 * @return true if this state is an final state
	 */
	private boolean isEndState(CombinedState state)
	{
		if (state.regularState.isGreedyEnd() || state.regularState.equals(pattern.getEndState())
				&& state.executionState.getOutTransitionsEx().size() == 0)
		{
			endStates.add(state);
			return true;
		}
		return false;
	}

	/**
	 * Process the given state
	 * 
	 * @param state
	 * @return true when an end has been reached
	 */
	private boolean processState(CombinedState state)
	{
		if (false && logger.isDebugEnabled())
		{
			logger.debug(String.format("Processing... regular state: %s; labels: %s; return ops %s", state.regularState
					.getStateId(), state.executionState.getFlowNode().getNamesEx(), state.getReturnOps()));
		}

		FlowNode fnode = state.executionState.getFlowNode();
		boolean isReturnAction = fnode.containsName(FlowNode.ACCEPT_RETURN_ACTION_NODE)
				|| fnode.containsName(FlowNode.REJECT_RETURN_ACTION_NODE);
		boolean isReturnFlow = fnode.containsName(FlowNode.RETURN_ACTION_NODE);

		for (ExecutionTransition trans : state.executionState.getOutTransitionsEx())
		{
			LabelSequence sequence = labeler.getLabels(trans);

			if (isReturnFlow)
			{
				LabelSequence newSeq = new LabelSequence();
				newSeq.addLabels(sequence.getLabelsEx());
				newSeq.addLabels(state.getReturnOps());
				sequence = newSeq;
			}

			if (sequence.isEmpty())
			{
				CombinedState newState = new CombinedState(trans.getEndState(), state.regularState, state, trans, null);
				if (addCheckedState(newState))
				{
					return true;
				}
			}
			else
			{
				if (isReturnAction)
				{
					CombinedState newState = new CombinedState(trans.getEndState(), state.regularState, state, trans,
							null);
					newState.setReturnOps(sequence.getLabelsEx());
					if (addCheckedState(newState))
					{
						return true;
					}
					continue;
				}

				Set<RegularState> nextStates = getNextStates(state.regularState, sequence);
				for (RegularState regstate : nextStates)
				{
					CombinedState newState = new CombinedState(trans.getEndState(), regstate, state, trans, sequence);
					if (addCheckedState(newState))
					{
						return true;
					}
				}
			}
		}

		return false;
	}

	/**
	 * Get the next available states in the pattern based on the current state
	 * and labels
	 * 
	 * @param startState
	 * @param sequence
	 * @return
	 */
	private Set<RegularState> getNextStates(RegularState startState, LabelSequence sequence)
	{
		Queue<RegularState> states = new LinkedList<RegularState>();
		addState(states, startState);
		for (String lbl : sequence.getLabelsEx())
		{
			Set<RegularState> visited = new HashSet<RegularState>();
			Queue<RegularState> queue = new LinkedList<RegularState>(states);
			states.clear();
			while (queue.size() > 0)
			{
				RegularState state = queue.remove();
				visited.add(state);
				for (RegularTransition rt : state.getOutTransitions())
				{
					if (rt.isEmpty())
					{
						RegularState rst = rt.getEndState();
						if (!queue.contains(rst) && !visited.contains(rst))
						{
							queue.add(rst);
						}
					}
					else if (rt.match(lbl))
					{
						addState(states, rt.getEndState());
					}
				}
			}
		}
		return new HashSet<RegularState>(states);
	}

	/**
	 * Resolve lambda transitions
	 * 
	 * @param states
	 * @param state
	 */
	protected void addState(Queue<RegularState> states, RegularState state)
	{
		if (states.contains(state))
		{
			return;
		}
		states.add(state);

		// add lambda transitions
		for (RegularTransition rt : state.getOutTransitions())
		{
			if (rt.isEmpty())
			{
				addState(states, rt.getEndState());
			}
		}
	}
}
