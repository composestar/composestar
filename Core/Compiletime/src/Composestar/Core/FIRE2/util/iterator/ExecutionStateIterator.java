/*
 * Created on 10-mrt-2006
 *
 */
package Composestar.Core.FIRE2.util.iterator;

import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Stack;

import Composestar.Core.FIRE2.model.ExecutionModel;
import Composestar.Core.FIRE2.model.ExecutionState;
import Composestar.Core.FIRE2.model.ExecutionTransition;

/**
 * @author Arjan de Roo
 */
public class ExecutionStateIterator implements Iterator
{
	public Stack unvisitedStates = new Stack();

	public HashSet visitedStates = new HashSet();

	public ExecutionStateIterator(ExecutionModel model)
	{
		ExecutionModel model1 = model;

		Enumeration enumer = model.getEntranceStates();
		while (enumer.hasMoreElements())
		{
			unvisitedStates.push(enumer.nextElement());
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
	public Object next()
	{
		ExecutionState state;

		if (!hasNext())
		{
			throw new NoSuchElementException();
		}

		// get next state:
		state = (ExecutionState) unvisitedStates.pop();

		// add to visitedStates:
		visitedStates.add(state);

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

		Enumeration enumer = state.getOutTransitions();
		while (enumer.hasMoreElements())
		{
			ExecutionTransition transition = (ExecutionTransition) enumer.nextElement();
			nextState = transition.getEndState();

			if (!visitedStates.contains(nextState))
			{
				unvisitedStates.push(nextState);
			}
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
