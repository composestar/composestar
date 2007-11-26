/*
 * Created on 30-mei-2006
 *
 */
package Composestar.Core.FIRE2.util.regex;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;

import Composestar.Core.FIRE2.model.ExecutionModel;
import Composestar.Core.FIRE2.model.ExecutionState;
import Composestar.Core.FIRE2.model.ExecutionTransition;

/**
 * @author Arjan de Roo
 */
@Deprecated
public class Matcher
{
	private Pattern pattern;

	private ExecutionModel model;

	private Labeler labeler;

	private Set<CombinedState> processedStates;

	private Stack<CombinedState> unvisitedStates;

	private CombinedState endState;

	private boolean matchDone = false;

	public Matcher(Pattern pattern, ExecutionModel model, Labeler labeler)
	{
		this.pattern = pattern;
		this.model = model;
		this.labeler = labeler;
	}

	public int processedStatesCount()
	{
		return processedStates.size();
	}

	public boolean matches()
	{
		matchDone = true;
		initialize();
		return process();
	}

	/**
	 * initializes start states.
	 */
	private void initialize()
	{
		processedStates = new HashSet<CombinedState>();
		unvisitedStates = new Stack<CombinedState>();

		Iterator<ExecutionState> states = model.getEntranceStates();
		RegularState regularState = pattern.getStartState();
		while (states.hasNext())
		{
			ExecutionState state = states.next();
			addState(new CombinedState(state, regularState));
		}
	}

	private boolean process()
	{
		while (!unvisitedStates.empty())
		{
			CombinedState state = unvisitedStates.pop();
			if (process(state))
			{
				return true;
			}
		}

		return false;
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
						executionTransition);
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
							executionTransition);
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
			unvisitedStates.push(state);
		}
	}

	private boolean isEndState(CombinedState state)
	{
		if (state.regularState.equals(pattern.getEndState()) && state.executionState.getOutTransitionsEx().size() == 0)
		{
			endState = state;
			return true;
		}
		else
		{
			return false;
		}
	}

	public List<ExecutionTransition> matchTrace()
	{
		if (!matchDone)
		{
			matches();
		}

		if (endState != null)
		{
			return Collections.unmodifiableList(endState.trace.toList());
		}
		else
		{
			return null;
		}
	}

	private class CombinedState
	{
		public ExecutionState executionState;

		public RegularState regularState;

		// private Vector trace;
		private TransitionTrace trace;

		public CombinedState(ExecutionState executionState, RegularState regularState)
		{
			this.executionState = executionState;
			this.regularState = regularState;
			trace = new TransitionTrace();
			// trace = new Vector();
		}

		public CombinedState(ExecutionState executionState, RegularState regularState, CombinedState previousState)
		{
			this.executionState = executionState;
			this.regularState = regularState;
			trace = previousState.trace;
			// trace = new Vector();
			// trace.addAll( previousState.trace );
		}

		public CombinedState(ExecutionState executionState, RegularState regularState, CombinedState previousState,
				ExecutionTransition transition)
		{
			this.executionState = executionState;
			this.regularState = regularState;
			trace = new TransitionTrace(previousState.trace, transition);
			// trace = new Vector();
			// trace.addAll( previousState.trace );
			// trace.add( transition );
		}

		@Override
		public boolean equals(Object obj)
		{
			if (!(obj instanceof CombinedState))
			{
				return false;
			}

			CombinedState state = (CombinedState) obj;

			return executionState.equals(state.executionState) && regularState.equals(state.regularState);
		}

		@Override
		public int hashCode()
		{
			return executionState.hashCode() + regularState.hashCode();
		}
	}

	/**
	 * LinkedList kind of class to efficiently construct the trace during
	 * matching.
	 * 
	 * @author Arjan de Roo
	 */
	private class TransitionTrace
	{
		public TransitionTrace heading;

		public ExecutionTransition last;

		public TransitionTrace()
		{

		}

		public TransitionTrace(TransitionTrace heading, ExecutionTransition last)
		{
			this.heading = heading;
			this.last = last;
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
