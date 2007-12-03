/*
 * Created on 30-mei-2006
 *
 */
package Composestar.Core.FIRE2.util.regex;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;

import Composestar.Core.FIRE2.model.ExecutionModel;
import Composestar.Core.FIRE2.model.ExecutionState;
import Composestar.Core.FIRE2.model.ExecutionTransition;

/**
 * @author Arjan de Roo
 */
public class LegacyMatcher extends AbstractMatcher
{
	private Pattern pattern;

	private ExecutionModel model;

	private Labeler labeler;

	private Set<CombinedState> processedStates;

	private Queue<CombinedState> unvisitedStates;

	public LegacyMatcher(Pattern pattern, ExecutionModel model, Labeler labeler)
	{
		super();
		this.pattern = pattern;
		this.model = model;
		this.labeler = labeler;
	}

	public int processedStatesCount()
	{
		return processedStates.size();
	}

	@Override
	public boolean matches()
	{
		matchDone = true;
		initialize();
		return endStates.size() > 0;
	}

	/**
	 * initializes start states.
	 */
	private void initialize()
	{
		Iterator<ExecutionState> states = model.getEntranceStates();
		RegularState regularState = pattern.getStartState();
		while (states.hasNext())
		{
			processedStates = new HashSet<CombinedState>();
			unvisitedStates = new LinkedList<CombinedState>();

			ExecutionState state = states.next();
			addState(new CombinedState(state, regularState));
			process();
		}
	}

	private void process()
	{
		while (!unvisitedStates.isEmpty())
		{
			CombinedState state = unvisitedStates.remove();
			if (process(state))
			{
				return;
			}
		}
		return;
	}

	private boolean process(CombinedState state)
	{
		RegularState regularState;
		RegularState[] nextStates;

		CombinedState newState;

		// empty transition in regular machine:
		regularState = state.regularState;
		for (RegularTransition regularTransition : regularState.getOutTransitions())
		{
			if (regularTransition.isEmpty())
			{
				newState = new CombinedState(state.executionState, regularTransition.getEndState(), state);
				if (isEndState(newState))
				{
					return true;
				}
				else
				{
					addState(newState);
				}
			}
		}

		// empty and non-empty transition in ExecutionModel:
		ExecutionState executionState = state.executionState;
		for (ExecutionTransition executionTransition : executionState.getOutTransitionsEx())
		{
			LabelSequence sequence = labeler.getLabels(executionTransition);
			if (sequence.isEmpty())
			{
				newState = new CombinedState(executionTransition.getEndState(), state.regularState, state,
						executionTransition, null);
				if (isEndState(newState))
				{
					return true;
				}
				else
				{
					addState(newState);
				}
			}
			else
			{
				nextStates = getNextStates(regularState, sequence);
				for (RegularState nextState : nextStates)
				{
					newState = new CombinedState(executionTransition.getEndState(), nextState, state,
							executionTransition, sequence);
					if (isEndState(newState))
					{
						return true;
					}
					else
					{
						addState(newState);
					}
				}
			}
		}

		return false;
	}

	private RegularState[] getNextStates(RegularState state, LabelSequence sequence)
	{
		RegularState[] currentStates = { state };
		List<RegularState> v = new Vector<RegularState>();

		for (String label : sequence.getLabelsEx())
		{
			for (RegularState currentState : currentStates)
			{
				v.addAll(getNextStates(currentState, label));
			}

			currentStates = v.toArray(new RegularState[v.size()]);
		}

		return currentStates;
	}

	private Collection<RegularState> getNextStates(RegularState state, String label)
	{
		Set<RegularState> result = new HashSet<RegularState>();
		for (RegularTransition transition : state.getOutTransitions())
		{
			if (transition.match(label))
			{
				result.addAll(lambdaClosure(transition.getEndState()));
			}
		}

		return result;
	}

	/**
	 * Returns the lambda (empty transition) closure of the given state, i.e.
	 * all states reachable from the given state (including this given state)
	 * that are reachable with empty transitions.
	 * 
	 * @param state The starting state.
	 * @return The lambda closure of the given state.
	 */
	private Collection<RegularState> lambdaClosure(RegularState state)
	{
		Set<RegularState> result;
		Stack<RegularState> checkNext;
		RegularState currentState, nextState;

		result = new HashSet<RegularState>();
		result.add(state);

		checkNext = new Stack<RegularState>();
		checkNext.push(state);

		while (!checkNext.isEmpty())
		{
			currentState = checkNext.pop();
			for (RegularTransition transition : currentState.getOutTransitions())
			{
				nextState = transition.getEndState();
				if (transition.isEmpty() && !result.contains(nextState))
				{
					result.add(nextState);
					checkNext.push(nextState);
				}
			}
		}

		return result;
	}

	private void addState(CombinedState state)
	{
		if (!processedStates.contains(state))
		{
			processedStates.add(state);
			unvisitedStates.add(state);
		}
	}

	private boolean isEndState(CombinedState state)
	{
		if (state.regularState.equals(pattern.getEndState()) && state.executionState.getOutTransitionsEx().size() == 0)
		{
			endStates.add(state);
			return true;
		}
		else
		{
			return false;
		}
	}
}
