/*
 * Created on 10-mrt-2006
 *
 */
package Composestar.Core.FIRE2.util.iterator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Stack;

import Composestar.Core.FIRE2.model.ExecutionModel;
import Composestar.Core.FIRE2.model.ExecutionState;
import Composestar.Core.FIRE2.model.ExecutionTransition;

/**
 * @author Arjan de Roo
 */
public class ExecutionStateIterator implements Iterator<ExecutionState>
{
	public Stack<ExecutionState> unvisitedStates = new Stack<ExecutionState>();

	public Set<ExecutionState> iteratedStates = new HashSet<ExecutionState>();

	public ExecutionStateIterator(ExecutionModel model)
	{
		Iterator<ExecutionState> it = model.getEntranceStates();
		while (it.hasNext())
		{
			ExecutionState obj = it.next();
			unvisitedStates.push(obj);
			iteratedStates.add(obj);
		}
	}

	/**
	 * @see java.util.Iterator#hasNext()
	 */
	public boolean hasNext()
	{
		return !unvisitedStates.isEmpty();
	}

	/**
	 * @see java.util.Iterator#next()
	 */
	public ExecutionState next()
	{
		ExecutionState state;

		if (!hasNext())
		{
			throw new NoSuchElementException();
		}

		// get next state:
		state = unvisitedStates.pop();

		// add next unvisitedStates to the unvisitedStates stack:
		addNextStates(state);

		return state;
	}

	/**
	 * adds the next states of the given state to the unvisitedstates stack.
	 * 
	 * @param state
	 */
	private void addNextStates(ExecutionState state)
	{
		ExecutionState nextState;
		List<ExecutionState> addStates = new ArrayList<ExecutionState>();
		for (ExecutionTransition transition : state.getOutTransitionsEx())
		{
			nextState = transition.getEndState();

			if (!iteratedStates.contains(nextState))
			{
				addStates.add(nextState);
				iteratedStates.add(nextState);
			}
		}

		// Add states to unvisitedStates stack in reversed order of the
		// transition iterator, to ensure that the endstate of an earlier
		// transition is visited before the endstate of a later transition.
		for (int i = addStates.size() - 1; i >= 0; i--)
		{
			unvisitedStates.push(addStates.get(i));
		}
	}

	/**
	 * @see java.util.Iterator#remove()
	 */
	public void remove()
	{
		throw new UnsupportedOperationException();
	}

}
