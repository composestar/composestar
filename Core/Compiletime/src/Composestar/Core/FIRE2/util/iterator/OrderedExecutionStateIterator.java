package Composestar.Core.FIRE2.util.iterator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

import Composestar.Core.FIRE2.model.ExecutionModel;
import Composestar.Core.FIRE2.model.ExecutionState;
import Composestar.Core.FIRE2.model.ExecutionTransition;

public class OrderedExecutionStateIterator implements Iterator<ExecutionState>
{
	private HashMap<ExecutionState, MutableInteger> unvisitedTransitionCount;

	private LinkedList<ExecutionState> nextStates;

	public OrderedExecutionStateIterator(ExecutionModel model)
	{
		initialize(model);
	}

	private void initialize(ExecutionModel model)
	{
		unvisitedTransitionCount = new HashMap<ExecutionState, MutableInteger>();

		List<ExecutionTransition> transitions = getTransitions(model);
		for (ExecutionTransition transition : transitions)
		{
			ExecutionState endState = transition.getEndState();
			if (unvisitedTransitionCount.containsKey(endState))
			{
				MutableInteger count = unvisitedTransitionCount.get(endState);
				count.value++;
			}
			else
			{
				MutableInteger count = new MutableInteger();
				count.value = 1;
				unvisitedTransitionCount.put(endState, count);
			}
		}

		nextStates = new LinkedList<ExecutionState>();

		Iterator<ExecutionState> entranceStates = model.getEntranceStates();
		while (entranceStates.hasNext())
		{
			ExecutionState entranceState = entranceStates.next();
			nextStates.add(entranceState);
		}
	}

	public boolean hasNext()
	{
		return !nextStates.isEmpty();
	}

	public ExecutionState next()
	{
		if (nextStates.isEmpty())
		{
			throw new NoSuchElementException();
		}

		ExecutionState state = nextStates.removeFirst();

		// Decrement counter of next nodes
		for (ExecutionTransition transition : state.getOutTransitionsEx())
		{
			ExecutionState endState = transition.getEndState();
			MutableInteger count = unvisitedTransitionCount.get(endState);
			count.value--;
			if (count.value == 0)
			{
				nextStates.add(endState);
			}
		}

		return state;
	}

	public void remove()
	{
		throw new UnsupportedOperationException();
	}

	private List<ExecutionTransition> getTransitions(ExecutionModel model)
	{
		ArrayList<ExecutionTransition> list = new ArrayList<ExecutionTransition>();

		Iterator<ExecutionState> stateIter = new ExecutionStateIterator(model);
		while (stateIter.hasNext())
		{
			ExecutionState state = stateIter.next();
			for (ExecutionTransition trans : state.getOutTransitionsEx())
			{
				list.add(trans);
			}
		}

		return list;
	}

	private static class MutableInteger
	{
		private int value;
	}

}
